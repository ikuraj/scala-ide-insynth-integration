package ch.epfl.insynth.reconstruction

import ch.epfl.scala.{ trees => Scala }
import ch.epfl.insynth.env._
import ch.epfl.insynth.reconstruction.trees. { Node => IntermediateNode, _}
import scala.annotation.tailrec
import ch.epfl.insynth.{ trees => InSynth }

object IntermediateTransformer extends (SimpleNode => Set[IntermediateNode]){
  
  import InSynth.TypeTransformer.{ transform => typeTransform }

  /** The context is a list of variable names paired with their type. */
  type Context = List[(String, Scala.ScalaType)]
  val emptyContext = List()
  
  def apply(root: SimpleNode) = {
    // calculate the goal type as being the only parameter in the query node
    val goalType = 
      root.decls.head match {
    	// TODO should be bottom type here
      	case NormalDeclaration(_, _, Scala.Function(head :: rest, retType)) => retType
      	case _ => throw new RuntimeException 
      }
    
    // start the transformation by going from the root node (query node), empty
    // context and trying to find the goal type
    transform(root, emptyContext, goalType)
  }
    
  /**
   * transform method, called recursively as we descent down the tree 
   * @param node current node
   * @param context environment (typing) context
   * @param goalType the type that we want to get
   * @return set of nodes that describe how to generate expression of goalType
   */
  private def transform(node: SimpleNode, context:Context, goalType: Scala.ScalaType): Set[IntermediateNode] = {
        
    // NOTE can be solved with a functional solution (stream of variables, not so pretty) 
    object variableGenerator {
      private var counter = 0
      
      def getFreshVariableName = "var_" + { counter+=1; counter }
    }
    
	/**
	 * @param queryType type of context variables to return
	 * @return a set of variables in context with a given type
	 */
	def getAllTermsFromContext(queryType: Scala.ScalaType): Set[IntermediateNode] =
	  (Set[IntermediateNode]() /: context) {
	    (set, contextEntry) => {
		  contextEntry match {
	      	case (name, `queryType`) => set + Variable(queryType, name)
	      	case _ => set
		  }  
	    }
	  }
	
	/**
	 * @param queryType InSynth type of the variable in the context
	 * @return a set of variables in context with a given InSynth type
	 */
	def getAllFunctionsFromContextByInSynthType(queryType: InSynth.Type): Set[Variable] =
	  (Set[Variable]() /: context) {
	    (set, contextEntry) => {
		  contextEntry match {
	      	case (name, variableType) if typeTransform(variableType) == queryType
  			  => set + Variable(variableType, name)
	      	case _ => set
		  }  
	    }
	  }
	
	/**
	 * returns an application node which generates the goal type according to the given
	 * scala function type - it applies parameters of fun to the functionNode argument
	 * @param fun scala function type
	 * @param functionNode the node which represents the function term
	 * @return application node
	 */
	def generateApplicationAccordingToFunction(fun: Scala.Function,
    functionNode: IntermediateNode): Application = {
      fun match {
        // fun directly returns the needed type just generate the application node
        // without recursive calls
        case Scala.Function(params, `goalType`) => {
          val mapOfSetsOfParameterTypes = getMapTypesToParameters(params)
          val paramsSetList:List[Set[IntermediateNode]] =
            params map { st:Scala.ScalaType => mapOfSetsOfParameterTypes(st) } 
          Application(
            fun,
            Set(functionNode) :: paramsSetList 
		  )
        }
        // fun returns another function to which we need to apply arguments, include
        // a recursive call
        case Scala.Function(params, innerFun:Scala.Function) =>
          val mapOfSetsOfParameterTypes = getMapTypesToParameters(params)
          val paramsSetList:List[Set[IntermediateNode]] =
            params map { st:Scala.ScalaType => mapOfSetsOfParameterTypes(st) }
          generateApplicationAccordingToFunction(
            innerFun,
            Application(fun, Set(functionNode) :: paramsSetList)
          )
      }
    }
	
	/**
	 * given a list of (Scala) types, returns a map from a Scala type to
	 * a set of nodes which can generate an expression of that type 
	 * @param parameterList list of types
	 * @return map from Scala type to a set of nodes
	 */
	def getMapTypesToParameters(parameterList: List[Scala.ScalaType]) = 
		// go through all needed parameters and generate appropriate nodes
		// NOTE we eliminate duplicates in order to avoid redundant computation	            			              
		(Map[Scala.ScalaType, Set[IntermediateNode]]() /: (parameterList distinct)) {
	      (map, parameterType) => {
	        // corresponding InSynth type
	        val parameterTypeInSynth = typeTransform(parameterType)
	        // get node with the needed type, deeper in down the tree
	        val containerNode = node.params(parameterTypeInSynth)
	        map + 
	        // add inner nodes to the set of solutions
	        (parameterType -> 
	        	// get all possible terms from each node
	            (Set[IntermediateNode]() /: containerNode.nodes) {
	              (set, node) => node match {
	                // if leaf node search the context
	                case Leaf(`parameterType`) => set ++ getAllTermsFromContext(parameterType)
	                // if simple node with the type that we need, recursively transform it
	                case nprime@SimpleNode(_, `parameterTypeInSynth`, _) => set ++ transform(nprime, context, parameterType)
	                // should not happen
	                case _ => throw new RuntimeException("Cannot go down for type: " + parameterType)
	              }
	            }	
	        )
	      }
	    } 
    
    /**
     * examines the declarations of the current node and returns the set of terms of
     * the goalType according to those declarations
     * @return set of terms which evaluate to goalType
     */
    def getMatchingTypeFromDeclaration:Set[IntermediateNode] = {
      
	    // check each declaration
	    (Set[IntermediateNode]() /: node.decls) {
	      (set, declaration) => {
	        declaration match {
	          // if declaration is of desired type, just return corresponding variable
	          case nd@NormalDeclaration(_, _, `goalType`) =>
	            set + Identifier(goalType, nd)
	          // if declaration will return the needed type after application
	          case nd:NormalDeclaration 
	          if declarationHasAppropriateReturnType(declaration, goalType) => {
	            // check the declaration scala type
	            val generatedApplication = nd.scalaType match {
	              // treat method as a function in which the function term is
	              // an identifier with the declaration
          		  case Scala.Method(receiver, params, retType) =>
          		    generateApplicationAccordingToFunction(
  		    		  // NOTE give flatten parameters list - Identifier knows about currying
  		    		  Scala.Function(receiver +: params.flatten, retType),
  		    		  Identifier(nd.scalaType, nd)
	        		)
//              	  	generateApplication(
//              	  	    nd, 
//              	  	    receiver +: params.flatten, 
//              	  	    mapOfSetsOfParameterTypes
//          	  	    )
          		  case sf:Scala.Function =>		
          		    generateApplicationAccordingToFunction(
  		    		  sf,
  		    		  Identifier(nd.scalaType, nd)
	        		)	            
//              	  	generateApplication(
//      	  			  nd,
//      	  			  list,
//      	  			  mapOfSetsOfParameterTypes
//      	  			)
          		  case _ => throw new RuntimeException // cannot happen	              
	            }
	            // add generated application to the set
	            set + generatedApplication
	          }	          
	          // if declaration will return the needed type after application (application
	          // uses a variable from the context)
	          case ad:AbsDeclaration 
	          if declarationHasAppropriateReturnType(declaration, goalType) => {
	            // get all variables from the context that can generate type that we want
	            val absFunctions = getAllFunctionsFromContextByInSynthType(ad getType)
	            // for all such variables from the context generate an application node
	            (Set[IntermediateNode]() /: absFunctions) {
	              (set, variable) => {
	                variable.tpe match {
	                  case fun:Scala.Function =>
	                    // add application node to the set
	                    set + generateApplicationAccordingToFunction(
	                    		fun, Variable(fun, variable.name)
                    		  )
            		  // should not happen 
	                  case _ => throw new RuntimeException
	                }
	              }
	            }
	          }
	          // should not happen, such declarations cannot give us type that we need
	          case _ => throw new RuntimeException("Failed matching " + declaration)
	        } // declaration match
	      }
	    } // (Set[IntermediateNode]() /: node.decls)
    } // getMatchingTypeFromDeclaration:Set[IntermediateNode]
        
    // check each case of the goal type
    goalType match {
	    // goal type is function type, we need to add new abstraction
	    case fun@Scala.Function(params, retType) => {
	      import variableGenerator._
	      // create an addition to the current context by inspecting all parameters
	      // of the function
	      val contextDelta:Context = params map { (getFreshVariableName, _) }
    	  // compute all abstraction terms (which differ for the body of abstraction)
	      val setOfAbstractionTerms =
		  // for each node in the given container (which must exist)
	      (Set[Abstraction]() /: node.params(typeTransform(retType)).getNodes) {
	        (set, node) => {
	          val retTypeInsynth = typeTransform(retType)
	          val innerNodeSet: Set[IntermediateNode] = 
	            // generate inner set of nodes
	            node match {
	              case Leaf(`retTypeInsynth`) => getAllTermsFromContext(retType)
	              case nprime@SimpleNode(_, `retType`, _) => transform(nprime, contextDelta ++ context, retType)
	              case _ => throw new RuntimeException("Cannot go down for type: " + retType)
	            }
	          // add the given abstraction to the set
		      set + Abstraction(
	            fun, (params zip contextDelta) map { pair => Variable(pair._1, pair._2._1) },
	            innerNodeSet
	          )
	        }
	      }
	      
          // we return abstraction terms union terms when matching declarations
          setOfAbstractionTerms ++ getMatchingTypeFromDeclaration
	    }
	    case Scala.Const(name) =>
	      // return only the set of terms matching given declarations
	      getMatchingTypeFromDeclaration
	      // NOTE no need to scan context here because the abstraction would be unecessary
	      
        // not implemented yet
	    case _:Scala.Instance => throw new UnsupportedOperationException
	    case _:Scala.Inheritance => throw new UnsupportedOperationException
	    // should not happen
	    case _ => throw new RuntimeException
    }
  }
  
  // XXX possible grouping of declarations with the same scala type!
  
  
//  /**
//   * returns a set of intermediate nodes which represent all possible 
//   * application nodes in the form term x1 x2 ... xn
//   * where ∀i. 0 ≤ i ≤ n-1 → xᵢ ∈ list(i)
//   * @param list of set of nodes
//   * @return set of application nodes
//   */
//  private def generateApplication(decl: NormalDeclaration, list:List[Scala.ScalaType],
//    map: Map[InSynth.Type, Set[IntermediateNode]]): Application = {
//        
//    list match {
//	  // if we have an empty list it is just a call with not parameters
//	  case List() =>
//	    Application(decl.scalaType, List(Set(Identifier(decl.scalaType, decl))))
//      // put the application identifier as the first term and add parameters
//	  case list:List[Scala.ScalaType] => {
//	    val listOfSets:List[Set[IntermediateNode]] =
//	      list map { t:Scala.ScalaType => map(typeTransform(t)) }
//	    val applicationSet:Set[IntermediateNode] =
//	      Set(Identifier(decl.scalaType, decl))
//	    Application(
//          decl.scalaType,
//          listOfSets :+ applicationSet
//		)
//	  }
//  	}
//  	
//  }
  
//  @tailrec
//  /**
//   * @param tpe
//   * @return the return (last Const) type of the parameter scala type
//   */
//  private def getReturnType(tpe: Scala.ScalaType): Scala.Const = 
//    tpe match {
//      case c:Scala.Const => c
//      case Scala.Function(params, f:Scala.Function) => getReturnType(f)
//      case Scala.Function(params, c:Scala.Const) => c
//      case _ => throw new RuntimeException
//    }
  
  /**
   * check if the given declaration and desired type are compatible in terms of the
   * innermost (return) type
   * @param dec declaration to check
   * @param goalType goal type that we are interested in
   * @return true if the declaration can give us the return type we want, otherwise false
   */
  private def declarationHasAppropriateReturnType(dec: Declaration, goalType: Scala.ScalaType): Boolean = {
    import ch.epfl.insynth.trees.TypeTransformer.{ transform => scalaToInSynth }
	    
    // get return type of the goalType
    val goalReturnInSynthType = scalaToInSynth(goalType) match {
	  case InSynth.Arrow(_, retType) => retType
	  case c:InSynth.Const => c
	  case _ => throw new RuntimeException
    } 
    // get return type of the dec declaration
    val declarationGoalType = dec.getType match {
	  case InSynth.Arrow(_, retType) => Some(retType)
      case _ => None
    }
    
    // DBG
//    println(declarationGoalType)
//    println(goalReturnInSynthType)
//    println(declarationGoalType.isDefined && declarationGoalType.get == goalReturnInSynthType)
    
    // check if declaration is an arrow type and its compatible
    declarationGoalType.isDefined && declarationGoalType.get == goalReturnInSynthType
  }
}