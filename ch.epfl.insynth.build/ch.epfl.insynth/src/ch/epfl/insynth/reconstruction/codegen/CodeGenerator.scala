package ch.epfl.insynth.reconstruction.codegen

import insynth.reconstruction.stream.Node
import ch.epfl.insynth.scala

import insynth.util.format._

import _root_.scala.text.Document
import _root_.scala.text.Document.empty

/**
 * class that converts an intermediate tree into a list of output elements (elements
 * capable of Scala code generation)
 * should be extended to provide syntax-style variants
 */
abstract class CodeGenerator extends (Node => CodeGenOutput) {
  import ScalaExtractors._

  // import methods for easier document manipulation
  import FormatHelpers._
  import Document._
  // convenience ?: operator
  import Bool._

  implicit def documentToCodeGenOutput = CodeGenOutput(_: Document)

  /**
   * takes the tree and calls the recursive function and maps documents to Output elements
   * @param tree root of intermediate (sub)tree to transform
   * @return list of output (code snippet) elements
   */
  def apply(tree: Node) = {
    tree match {
      case Application(scala.Function(_, _ /* BottomType */), queryDec :: List(list)) =>
      	transform(list, TransformContext.Expr)
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
  def transform(tree: Node, ctx: TransformContext = Expr): Document
  
  /**
   * transform a scala type into an appropriate document
   * @param scalaType scala type to transform
   * @return an appropriate document
   */
  protected def transform(scalaType: scala.ScalaType): Document =
    scalaType match {
	  case scala.Function(params, returnType) =>
	    paren(seqToDoc(params, ",", { param:scala.ScalaType => transform(param) } )) :/:
	    "=>" :/: transform(returnType)	    		
	  case scala.Const(name) => name	    		
	  case scala.Instance(name, list) =>
	    name :: sqBrackets( seqToDoc(list, ",", transform(_:scala.ScalaType) ) )
	  case _ => throw new RuntimeException
  	}
  
  /**
   * generates parameter list according
   * @param params parameter list for transform
   * @return list of documents with all parameter combinations
   */
  protected def getParamsCombinations(params: List[Node], ctx: TransformContext = Par): Document = {
    def getParamsCombinationsRec(listOfPicked: List[Document], params: List[Node]): Document = {
      params match {
        case List() =>
          foldDoc(listOfPicked.tail, ", ")
        case el :: list =>
          getParamsCombinationsRec(listOfPicked :+ transform(el, ctx), list)          
      }
    }
    
    getParamsCombinationsRec(List(empty), params)
  }
  
  /**
   * generates all documents which represent all combinations of parameters according
   * to the given parameter list and paramsInfo (for currying)
   * @param params parameter list for transform
   * @param paramsInfo parameter list information
   * @return list of documents with all parameter combinations
   */
  protected def getParamsCombinations(params: List[Node], paramsInfo: List[List[scala.ScalaType]], parenthesesRequired: Boolean): Document = {
    
    assert(params.size == paramsInfo.flatten.size)
    // convenient solution for currying
    val backToBackParentheses: Document = ")("
    
    def getParamsCombinationsRec(
        params: List[Node],
        paramsInfo: List[List[scala.ScalaType]]): Document = 
    {
      paramsInfo match {
        case List(lastList) =>
          assert(1 == paramsInfo.size)
          assert(params.size == lastList.size)
          // return the list of transformed last parentheses parameters
          getParamsCombinations(params)
        case currentList :: restOfTheList => {
          val currentListDocument = getParamsCombinations(params take currentList.size)
          
          currentListDocument :: backToBackParentheses ::
          	getParamsCombinationsRec((params drop currentList.size), restOfTheList)          
        }
        case Nil => empty
      }
    }
    
    // if there is only one parameter and parentheses will not be outputed we have
    // to transform (potential) abstractions with braces
    val context:TransformContext = (params.size > 1 && !parenthesesRequired) ?	SinglePar | Expr
    	
    // is curried?
    if (paramsInfo.size > 1)
    	getParamsCombinationsRec(params, paramsInfo)
  	else 
      getParamsCombinations(params, context)
  }
    
  // declare an implicit helper
  // ?:: concatenates the documents only if first one is not `empty` otherwise result is `empty`
  sealed case class DocumentHelper(innerDoc: Document) {
    import _root_.scala.text._

    def ?::(argDoc: Document) = addOrEmpty(argDoc, innerDoc)
    def ?::(argDoc: String) = addOrEmpty(argDoc, innerDoc)
    def :/?:(argDoc: Document): Document = innerDoc match {
      case DocNil => argDoc
      case _ => argDoc :/: innerDoc
    }
    def :?/:(argDoc: Document): Document = argDoc match {
      case DocNil => innerDoc
      case _ => argDoc :/: innerDoc
    }
  }	
  
  object DocumentHelper {    
	  implicit def DocumentHelperCast(d: Document) = DocumentHelper(d)
	  implicit def DocumentHelperCast(s: String) = DocumentHelper(s)
  }
  
  // ternary operator support
  case class Bool(b: Boolean) {
    def ?[X](t: => X) = new { 
      def |(f: => X) = if(b) t else f
    }
  }
	
  object Bool {
    implicit def BooleanBool(b: Boolean): Bool = Bool(b)
  }
  
}

/**
 * class for encapsulation of code snippets
 */
case class CodeGenOutput(doc: Document) extends Formatable {
  def toDocument = doc
  // weight
}