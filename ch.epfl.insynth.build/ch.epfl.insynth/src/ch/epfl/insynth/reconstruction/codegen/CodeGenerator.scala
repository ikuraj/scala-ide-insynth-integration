package ch.epfl.insynth.reconstruction.codegen

import ch.epfl.insynth.reconstruction.intermediate._
import ch.epfl.insynth.trees
import ch.epfl.scala.{ trees => Scala }
import ch.epfl.insynth.print._
import ch.epfl.insynth.reconstruction.combinator.{ NormalDeclaration, AbsDeclaration }

import scala.text.Document
import scala.text.Document.empty

/**
 * class that converts an intermediate tree into a list of output elements (elements
 * capable of Scala code generation)
 * should be extended to provide syntax-style variants
 */
abstract class CodeGenerator extends (Node => List[CodeGenOutput]) {
  // import methods for easier document manipulation
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
  
  /** transform context determines the place of the element to transform */
  object TransformContext extends Enumeration {
    type TransformContext = Value
    // expression, application, parameter, argument (in a function), single parameter
    val Expr, App, Par, Arg, SinglePar = Value
  }
  // import all transform context values
  import TransformContext._
  
  /**
   * main method (recursive) for transforming a intermediate (sub)tree
   * @param tree root node of the (sub)tree to transform 
   * @return list of documents containing all combinations of available expression for
   * the given (sub)tree
   */
  def transform(tree: Node, ctx: TransformContext = Expr): List[Document]
  
  /**
   * transform a scala type into an appropriate document
   * @param scalaType scala type to transform
   * @return an appropriate document
   */
  protected def transform(scalaType: Scala.ScalaType): Document =
    scalaType match {
	  case Scala.Function(params, returnType) =>
	    paren(seqToDoc(params, ",", { param:Scala.ScalaType => transform(param) } )) :/:
	    "=>" :/: transform(returnType)	    		
	  case Scala.Const(name) => name	    		
	  case Scala.Instance(name, list) =>
	    name :: sqBrackets( seqToDoc(list, ",", transform(_:Scala.ScalaType) ) )
	  case _ => throw new RuntimeException
  	}
  
  /**
   * helper method which transforms each element of the given list
   * @param nodeList parameter list to transform
   * @return a list of documents, concatenation of lists of transformed documents
   * for each element of the parameter list 
   */
  protected def transform(nodeList: List[Node], ctx: TransformContext): List[Document] = {
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
  protected def getParamsCombinations(params: List[Set[Node]], ctx: TransformContext = Par):List[Document] = {
    def getParamsCombinationsRec(listOfPicked: List[Document], params: List[Set[Node]]):List[Document] = {
      params match {
        case List() =>
          List(foldDoc(listOfPicked.tail, ","))
        case set :: list =>
          (List[Document]() /: transform(set.toList, ctx)) {
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
  protected def getParamsCombinations(params: List[Set[Node]], paramsInfo: List[List[Scala.ScalaType]], parenthesesRequired: Boolean):List[Document] = {
    def getParamsCombinationsRec(
        params: List[Set[Node]],
        paramsInfo: List[List[Scala.ScalaType]]):List[Document] = 
    {
      paramsInfo match {
        case List(lastList) =>
          assert(1 == paramsInfo.size)
          assert(params.size == lastList.size)
          // return the list of transformed last parentheses parameters
          getParamsCombinations(params)
        case currentList :: restOfTheList => {
          val currentListDocuments = getParamsCombinations(params take currentList.size)
          // go through all recursively got documents
          (List[Document]() /: getParamsCombinationsRec((params drop currentList.size), restOfTheList)) {
            (list, currentDocument) =>
              // add the combination with current parentheses documents
              list ++ currentListDocuments map {
                paren(_:Document) ::
                // if rest of the list is just one element it will be returned with no parentheses
                { if (restOfTheList.size == 1) currentDocument else paren(currentDocument) }
              }
          }
        }
        case Nil => List(empty)
      }
    }
    
    // if there is only one parameter and parentheses will not be outputed we have
    // to transform (potential) abstractions with braces
    if (params.size == 1 && !parenthesesRequired)
      getParamsCombinations(params, SinglePar)
    else
	  getParamsCombinationsRec(params, paramsInfo)
  }
  
  // ternary operator support
  case class Bool(b: Boolean) {
    def ?[X](t: => X) = new { 
      def |(f: => X) = if(b) t else f
    }
  }
	
  object Bool {
    implicit def BooleanBool(b: Boolean) = Bool(b)
  }
  
}

/**
 * class for encapsulation of code snippets
 */
case class CodeGenOutput(doc: Document) extends Formatable {
  def toDocument = doc
  // weight
}