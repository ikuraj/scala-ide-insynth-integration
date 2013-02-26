package ch.epfl.lambda

import ch.epfl.insynth.{ trees => InSynth }
import ch.epfl.insynth.env.SimpleNode
import ch.epfl.insynth.trees._
import ch.epfl.insynth.env.Declaration
import ch.epfl.insynth.env.Leaf
import ch.epfl.scala.{ trees => Scala }

object LambdaTransformer extends (SimpleNode => Set[Term]) {
    
  /** The context is a list of variable names paired with their type. */
  type Context = List[(Variable, InSynth.Type)]
  val emptyContext = List()
  
  def apply(root: SimpleNode) =
    transform(root, emptyContext)  
    
  private def transform(node: SimpleNode, context:Context): Set[Term] = {
    
    val goalType = node.tpe
    
    // NOTE can be solved with a functional solution (stream of variables, not so pretty) 
    object variableGenerator {
      private var counter = 0
      
      def getFreshVariableName = "var_" + { counter+=1; counter }
    }
    
	/**
	 * @param queryType type of context variables to return
	 * @return a set of variables in context with a given type
	 */
	def getAllTermsFromContext(queryType: InSynth.Type): Set[Term] =
	  (Set[Term]() /: context) {
	    (set, contextEntry) => {
		  contextEntry match {
	      	case (variable, `queryType`) => set + variable
	      	case _ => set
		  }  
	    }
	  }
    
    /**
     * examines the declaration of the current node and returns the set of terms of
     * the goalType according to those declarations
     * @return set of terms which evaluate to goalType
     */
    def getMatchingTypeFromDeclaration:Set[Term] = {
	    // check each declaration
	    (Set[Term]() /: node.decls) {
	      (set, declaration) => {
	        declaration.inSynthType match {
	          // if declaration is of desired type, just return corresponding variable
	          case `goalType` if declaration.fullName != "abs$inst" =>
	            set + Variable(declaration.fullName)
	          // if declaration's return type is of desired type, return the appropriate
	          // application term
	          case `goalType` =>
	            set
	          case Arrow(TSet(parameterList), `goalType`) => {
	            val mapOfSetsOfParameterTypes =
            		// go through all needed parameters and generate appropriate terms
            		// NOTE the result will be list of sets of terms (a set for each parameter)
	            			              
            		(Map[InSynth.Type, Set[Term]]() /: parameterList) {
		              (map, parameterType) => {
		                // get node with the needed type, deeper in down the tree
		                val containerNode = node.params(parameterType)
		                map + 
		                (parameterType -> 
		                	// get all possible terms from each node
			                (Set[Term]() /: containerNode.nodes) {
			                  (set, node) => node match {
			                    case Leaf(`parameterType`) => set ++ getAllTermsFromContext(parameterType)
			                    case nprime@SimpleNode(_, `parameterType`, _) => set ++ transform(nprime, context)
			                    case _ => throw new RuntimeException("Cannot go down for type: " + parameterType)
			                  }
			                }	
		                )
		              }
		            } 
	            // add all possible combinations of application term to the set
	            val setOfApplications = declaration.scalaType match {
          		  case Scala.Method(receiver, params, retType) =>
              	  	generateApplication(
              	  	    Variable(declaration.fullName), 
              	  	    receiver +: params.flatten, 
              	  	    mapOfSetsOfParameterTypes
          	  	    )
          		  case Scala.Function(list, _) =>			            
              	  	generateApplication(
      	  			  Variable(declaration.fullName),
      	  			  list,
      	  			  mapOfSetsOfParameterTypes
      	  			)
          		  case _ => throw new RuntimeException // cannot happen	              
	            }
	            set ++ setOfApplications
	          }
	          case _:Instance => throw new UnsupportedOperationException // not implemented yet
	          case _:Const => throw new RuntimeException // should not happen	          
	          case _ => throw new RuntimeException("Failed matching "
                + declaration.inSynthType + " for goal type " + goalType) // should not happen
                //+ "I have declarations: " + declaration) // should not happen
	        }
	      }
	    } 
    } 
        
    // check each case of the goal type
    node.tpe match {
	    // goal type is arrow type, we need to cover new abstraction also
	    case arrowTpe@Arrow(TSet(parameterList), returnType) => {
	      import variableGenerator._
	                
          // find a declaration that will tell me that I should make an abstraction
          val declaration = node.decls.find {
	        _ match {
	          case Declaration("abs$inst", `arrowTpe`, t:Scala.Function) => true
	          case _ => false
	        } 
	      }//.orElse(throw new RuntimeException)
        
        /**
         * return abstractions which represent taking of all the parameters and returning
         * some term of the goal type
         * @param parameterList parameters to be handled
         * @param newContext context with all the parameters included
         * @param makeTerm a function in which we can "plug" all found terms
         */
	    // NOTE declaration should tell me in which order should the variables be generated
        def getAbstractions(parameterList: List[Scala.ScalaType],
		    newContext: Context, makeTerm:(Term => Term)): Set[Term] = {
                
		    import TypeTransformer.{ transform => typeTransform }
		    
		    // get node with the needed type, deeper in down the tree
		    val containerNode = node.params(returnType)
		      	
            parameterList match {
	            case List() =>
	              // for each container node
	              val setOfInnerTerms =
	                (Set[Term]() /: containerNode.nodes) {
	            	  (set, node) => node match {
		              	case Leaf(`returnType`) => set ++ getAllTermsFromContext(returnType)
		              	case nprime@SimpleNode(_, `returnType`, _) => 
	              		  set ++ transform(nprime, newContext)
		              	case _ => throw new RuntimeException
	            	  }
	              	}
	              // "plug" all terms into our function to return the abstraction
	              setOfInnerTerms map makeTerm
	            case parameter::rest =>
	              // list is not empty, add another abstraction which takes this parameter
	              val freshVar = Variable(getFreshVariableName)
	              val parameterInSynthType = typeTransform(parameter)
	              getAbstractions(
            		  rest, newContext :+ (freshVar, parameterInSynthType),
            		  t => makeTerm(
    				    Lambda(freshVar, typeTransformInSynthToLambda(parameterInSynthType), t)
    				  )
        		  )
          	}
          }
          
          // set of terms which correspond to generated abstractions (will be
    	  // empty if InSynth tree does not contain appropriate declaration)
          val setOfFunctionTerms = declaration match {
            case Some(Declaration(_, _, Scala.Function(typeList, _))) => {
              getAbstractions(typeList, context, identity)
            }
            case _ => Set()
          }
          
          // we return abstraction terms union terms when matching declarations
          setOfFunctionTerms ++ getMatchingTypeFromDeclaration
	    }
	    case Const(name) =>
	      // return only the set of terms matching given declarations
	      getMatchingTypeFromDeclaration
	      
        // not implemented yet
	    case _:Instance => throw new UnsupportedOperationException
	    case _:IArrow => throw new UnsupportedOperationException
	    case _:InSynth.Variable => throw new UnsupportedOperationException
    }
  }
  
  
  /**
   * given a list of sets of terms and a term returns a set of terms which represent
   * all possible combinations for an application terms in the form term x1 x2 ... xn
   * where ∀i. 0 ≤ i ≤ n-1 → xᵢ ∈ list(i)
   * @param list of set of terms
   * @return set of application terms
   */
  private def generateApplication(term :Term, list:List[Scala.ScalaType], map: Map[InSynth.Type, Set[Term]]): Set[App] = {
    import TypeTransformer.{ transform => typeTransform }
        
    list match {
	  // we cannot have an empty list, because there are no parameters to apply to term
	  case List() => throw new RuntimeException
	  // if we have only one element, do the application without a recursive call
	  case List(tpe) =>
	    val set = map(typeTransform(tpe))
	    (Set[App]() /: set) {
    	  (set, element) =>		  	
		  	set + App(term, element)
	    }
      // for a list, get recursive application terms for all elements except the last
	  // and then plug them to the top level App
	  case list:List[Scala.ScalaType] =>
	    val innerApplications = generateApplication(term, list.init, map)
	  	(Set[App]() /: map(typeTransform(list.last))) {
  		  (currentListElementSet, element) =>
		  	(currentListElementSet /: innerApplications) {
		  	  (set, innerApplication) => set + App(innerApplication, element) 
		  	}  		  	
	  	}
  	}
  	
  }
  
  /**
   * return a Lambda type which corresponds to the given InSynth type
   * @param tpe InSynth type to transform
   * @return corresponding Lambda type
   */
  private def typeTransformInSynthToLambda(tpe: InSynth.Type):Type =
	  tpe match {
	  	case Const(name) => TypeConst(name)
	  	case Arrow(TSet(first::rest), returnType) =>
	  	  TypeComplex(
		    typeTransformInSynthToLambda(first),
		    typeTransformInSynthToLambda(Arrow(TSet(rest), returnType))
		  )
	  	case Arrow(TSet(List()), returnType) =>
	  	  typeTransformInSynthToLambda(returnType)
	  	case _:Instance => throw new UnsupportedOperationException
	    case _:IArrow => throw new UnsupportedOperationException
	    case _:InSynth.Variable => throw new UnsupportedOperationException
	  }
  
  
  /**
   * given a list of sets of terms and a term returns a set of terms which represent
   * all possible combinations for an application terms in the form term x1 x2 ... xn
   * where ∀i. 0 ≤ i ≤ n-1 → xᵢ ∈ list(i)
   * @param list of set of terms
   * @return set of application terms
   */
  private def generateApplication(term :Term, list: List[Set[Term]]): Set[App] =
    list match {
	  // we cannot have an empty list, because there are no parameters to apply to term
	  case List() => throw new RuntimeException
	  // if we have only one element, do the application without a recursive call
	  case List(set) => 
	    (Set[App]() /: set) {
    	  (set, element) =>		  	
		  	set + App(term, element)
	    }
      // for a list, get recursive application terms for all elements except the last
	  // and then plug them to the top level App
	  case _:List[Set[Term]] =>
	    val innerApplications = generateApplication(term, list.init)
	  	(Set[App]() /: list.last) {
  		  (currentListElementSet, element) =>
		  	(currentListElementSet /: innerApplications) {
		  	  (set, innerApplication) => set + App(innerApplication, element) 
		  	}  		  	
	  	}
  	}
}