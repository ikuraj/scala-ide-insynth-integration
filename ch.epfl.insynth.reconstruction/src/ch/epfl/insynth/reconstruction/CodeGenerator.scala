package ch.epfl.insynth.reconstruction

import scala.text.Document
import ch.epfl.insynth.combinator.NormalDeclaration
import ch.epfl.insynth.combinator.AbsDeclaration
import ch.epfl.insynth.reconstruction.trees._
import ch.epfl.insynth.print._
import ch.epfl.scala.{ trees => Scala }

/**
 * class that converts an intermediate tree into a list of output elements (elements
 * capable of Scala code generation)
 */
object CodeGenerator extends (Node => List[CodeGenOutput]) {
  
  import FormatHelpers._
  import Document._

  /**
   * takes the tree and calls the recursive function and maps documents to Output elements
   * @param tree root of intermediate (sub)tree to transform
   * @return list of output (code snippet) elements
   */
  def apply(tree:Node) = {
    tree match {
      case Application(Scala.Function(_, _ /* BottomType */), queryDec :: List(set)) =>
      	transform(set.toList, TransformContext.Expr) map { CodeGenOutput(_:Document) }
      case _ => throw new RuntimeException
    }    
  }
  
  object TransformContext extends Enumeration {
    type TransformContext = Value
    val Expr, App, Par, Arg = Value
  }
  import TransformContext._
  
  /**
   * main method (recursive) for transforming a intermediate (sub)tree
   * @param tree root node of the (sub)tree to transform 
   * @return list of documents containing all combinations of available expression for
   * the given (sub)tree
   */
  def transform(tree: Node, ctx: TransformContext = Expr): List[Document] = {
    tree match {
      // variable (declared previously as arguments)
      case Variable(tpe, name) if ctx==Arg => List ( group(name :: ": " :: transform(tpe) ) )
      case Variable(tpe, name) => List ( name )
      // identifier from the scope
      case Identifier(tpe, dec) => 
        List(dec.getSimpleName)
      // apply parameters in the tail of params according to the head of params 
      case Application(tpe, params) => {
        // so far as we constructed, there should be only one application definition term
        // (which tells us, whether it is a function a method...)
        assert(params.head.size == 1)
        
        // TODO do this parentheses this
//        var parenthesesRequired = true
//        
//        def doParen(doc:Document) = if (parenthesesRequired) paren(doc) else doc
        
        // most usual transformation in which the head is a function
        def firstTermFunctionTransform =
          // go through all possible transformations of functions
          (List[Document]() /: transform(params.head.head, App)) {
              (list, appIdentifier) =>
        	  	// get every possible parameter combination
                (list /: getParamsCombinations(params.tail)) {
            	  (list2, paramsDoc) => list2 :+ group(appIdentifier :: paren(paramsDoc))		      
                }
          }
        
        // match the application definition term
        params.head.head match {
          case Identifier(_, NormalDeclaration(decl))  => {
	        // transform the application identifier in an application context
	        val appIdentifier = transform(params.head.head, App).head
	        
	        // check what the declaration says about the application
	        if (decl.isField) {
	          assert(params.size == 2)
	          // TODO refactor - similar to the method construction
        	  // go through all the receiver objects
        	  (List[Document]() /: transform(params(1).toList, App)) {
		        (listOfDocumentsToReturn, objectDoc) =>
		          listOfDocumentsToReturn :+ group(objectDoc :: "." :: appIdentifier)
		      } 	          
	        } else
	        
	        if (!decl.isMethod) 
	          // constructor call
	          if (decl.isConstructor)
		        // go through all combinations of parameters documents
	    		(List[Document]() /: getParamsCombinations(params.tail)) {
	    		  (list, paramsDoc) => list :+
				    group("new" :/: appIdentifier :: paren(paramsDoc))
	    		}		
              // just a function            
	          else
	        	firstTermFunctionTransform
	        else // if (decl.isMethod)
	          {
	        	val paramsInfo = decl.scalaType match {
	        	  case Scala.Method(_, params, _) => params
	        	  case _ => throw new RuntimeException("Declared method but scala type is not")
	        	}
			    // go through all the receiver objects
			    (List[Document]() /: transform(params(1).toList, App)) {
			      (listOfDocumentsToReturn, objectDoc) => {
			        // go through all combinations of parameters documents
		    		(listOfDocumentsToReturn /: 
		    			getParamsCombinations(params.drop(2), paramsInfo)) {
		    		  (list, paramsDoc) => list :+
					    group(objectDoc :: "." :: appIdentifier :: paramsDoc)
		    		}		      
			      }
			    }
          	  }
			}
          // function that is created as an argument or anything else
          case Identifier(_, _:AbsDeclaration) | _:Variable | _ =>
          	firstTermFunctionTransform
        }
      }
      // abstraction first creates all of its arguments
      case Abstraction(tpe, vars, subtrees) =>
        // for all bodies of this abstraction
        (List[Document]() /: subtrees) {
    	  (listOfAbstractions, body) => {
    	    listOfAbstractions ++
    	    // for all transformations of bodies
    	    (List[Document]() /: transform(body)) {
    	      (listOfBodies, transformedBody) =>
    	    	listOfBodies :+ nestedBrackets(
    	    	  // transform argument variables
    			  paren(seqToDoc(vars, ",", { v:Variable => v.name :: ": " :: transform(v.tpe) })) 
    			  :/: "=>" :/:
    			  // transform the body
				  nestedBrackets(transformedBody)
    			)
    	    }
    	  }
        }
    }
  }
  
  /**
   * transform a scala type into an appropriate document
   * @param scalaType scala type to transform
   * @return an appropriate document
   */
  private def transform(scalaType: Scala.ScalaType): Document =
    scalaType match {
	  case Scala.Function(params, returnType) =>
	    paren(seqToDoc(params, ",", { param:Scala.ScalaType => transform(param) } )) :/:
	    "=>" :/: transform(returnType)	    		
	  case Scala.Const(name) => name
	  case _ => throw new RuntimeException
  	}
  
  /**
   * helper method which transforms each element of the given list
   * @param nodeList parameter list to transform
   * @return a list of documents, concatenation of lists of transformed documents
   * for each element of the parameter list 
   */
  private def transform(nodeList: List[Node], ctx: TransformContext): List[Document] = {
    (List[Document]() /: nodeList) {
      (list, node) => {
        list ++ transform(node, ctx)
      }
    }
  }
  
  /**
   * generates all documents which represent all combinations of parameters according
   * to the given parameter list
   * @param params parameter list for transform
   * @return list of documents with all parameter combinations
   */
  private def getParamsCombinations(params: List[Set[Node]]):List[Document] = {
    def getParamsCombinationsRec(listOfPicked: List[Document], params: List[Set[Node]]):List[Document] = {
      params match {
        case List() =>
          List(foldDoc(listOfPicked.tail, ","))
        case set :: list =>
          (List[Document]() /: transform(set.toList, Par)) {
            (listSoFar, el) => {
              listSoFar ++ getParamsCombinationsRec(listOfPicked :+ el, list)
            }
          }
      }
    }
    
    getParamsCombinationsRec(List(empty), params)
  }
  
  /**
   * generates all documents which represent all combinations of parameters according
   * to the given parameter list and paramsInfo (for curring)
   * @param params parameter list for transform
   * @param paramsInfo parameter list information
   * @return list of documents with all parameter combinations
   */
  private def getParamsCombinations(params: List[Set[Node]], paramsInfo: List[List[Scala.ScalaType]]):List[Document] = {
    def getParamsCombinationsRec(
        params: List[Set[Node]],
        paramsInfo: List[List[Scala.ScalaType]]):List[Document] = 
    {
      paramsInfo match {
        case List(lastList) =>
          assert(1 == paramsInfo.size)
          assert(params.size == lastList.size)
          // return the list of transformed last parentheses parameters
          getParamsCombinations(params) map { paren(_:Document) }
        case currentList :: restOfTheList => {
          val currentListDocuments = getParamsCombinations(params take currentList.size)
          // go through all recursively got documents
          (List[Document]() /: getParamsCombinationsRec((params drop currentList.size), restOfTheList)) {
            (list, currentDocument) =>
              // add the combination with current parentheses documents
              list ++ currentListDocuments map {
                paren(_:Document) :: currentDocument
              }
          }
        }
        case Nil => List(empty)
      }
    }
    
    getParamsCombinationsRec(params, paramsInfo)
  }
  
}

/**
 * class for encapsulation of code snippets
 */
case class CodeGenOutput(doc: Document) extends Formatable {
  def toDocument = doc
  // weight
}