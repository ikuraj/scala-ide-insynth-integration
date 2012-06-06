package ch.epfl.insynth.reconstruction

import scala.text.Document

import ch.epfl.insynth.combinator.NormalDeclaration
import ch.epfl.insynth.reconstruction.trees._
import ch.epfl.insynth.print._
import ch.epfl.scala.{ trees => Scala }

/**
 * class that converts an intermediate tree into a list of output elements (elements
 * capable of Scala code generation)
 */
object Reconstructor extends (Node => List[Output]) {
  
  import FormatHelpers._
  import Document._

  /**
   * takes the tree and calls the recursive function and maps documents to Output elements
   * @param tree root of intermediate (sub)tree to transform
   * @return list of output (code snippet) elements
   */
  def apply(tree:Node) = { 
    transform(tree) map { Output(_:Document) }
  }
  
  /**
   * main method (recursive) for transforming a intermediate (sub)tree
   * @param tree root node of the (sub)tree to transform 
   * @return list of documents containing all combinations of available expression for
   * the given (sub)tree
   */
  def transform(tree: Node): List[Document] = {
    tree match {
      case Variable(tpe, name) => List ( name )
      case Identifier(tpe, dec) => 
        List(dec.getSimpleName)
      case Application(tpe, params) => {
        (List[Document]() /: transform(params.head.toList)) {
    	  (listSoFar, el) => {
    	    (List[Document]() /: getParamsCombinations(params.tail)) {
		      (list2, paramsDoc) => list2 :+ 
    		    group(el :/: paren(paramsDoc))
    	    }
    	  }
        }
      }
      case Abstraction(tpe, vars, subtrees) =>
        (List[Document]() /: subtrees) {
    	  (listOfAbstractions, body) => {
    	    listOfAbstractions ++
    	    (List[Document]() /: transform(body)) {
    	      (listOfBodies, transformedBody) =>
    	    	listOfBodies :+ nestedBrackets(
    	    	  // transform variables first
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
  private def transform(nodeList: List[Node]): List[Document] = {
    (List[Document]() /: nodeList) {
      (list, node) => {
        list ++ transform(node)
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
          (List[Document]() /: transform(set.toList)) {
            (listSoFar, el) => {
              listSoFar ++ getParamsCombinationsRec(listOfPicked :+ el, list)
            }
          }
      }
    }
    
    getParamsCombinationsRec(List(empty), params)
  }
  
}

/**
 * class for encapsulation of code snippets
 */
case class Output(doc: Document) extends Formatable {
  def toDocument = doc
  // weight
}