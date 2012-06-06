package ch.epfl.insynth.reconstruction.trees

import scala.text.Document

import ch.epfl.scala.trees.{ ScalaType => Type, _ }
import ch.epfl.insynth.combinator.Declaration
import ch.epfl.insynth.print._

/**
 * can return the type of the (sub)tree
 */
trait Typable {
  def getType: Type
}

/**
 * abstract tree node
 * is capable of returning its type and to format itself
 */
abstract class Node extends Typable with FormatableIntermediate

/**
 * a leaf node, descent down the tree finishes at a subclass of this node 
 */
abstract class Leaf(tpe: Type) extends Node {
  def getType = tpe
}

/**
 * variable node represents a variable which is introduced to the typing context
 * within the given expression tree (it an identifier in scope)
 */
case class Variable(tpe: Type, name: String) extends Leaf(tpe)

/**
 * identifier in scope
 * @param decl declaration with more information about the identifier 
 */
case class Identifier(tpe: Type, decl: Declaration) extends Leaf(tpe)

/**
 * application term
 * first element in params is an expression (subtree) to which other parameters are
 * applied
 */
case class Application(tpe: Type, params: List[Set[Node]]) extends Node {
  def getType = tpe
  def getParams = params
}

/**
 * abstraction element introduces new variable into the typing context
 */
case class Abstraction(tpe: Type, vars: List[Variable], subTrees: Set[Node]) extends Node {
  def getType = tpe
}

/**
 * trait that defines how are intermediate nodes formated into pretty print documents
 */
trait FormatableIntermediate extends Formatable {
  def toDocument: Document = {
    import FormatHelpers._

    this match {
      case Variable(tpe, name) => paren(name :: ": " :: tpe.toString) 
      case Identifier(tpe, dec) => dec.getSimpleName
      case Application(tpe, params) => {
        val headDoc:Document = params.head.head match {
          case Variable(_, name) => name
          case n => n.toDocument
        } 
        headDoc :/:
        paren(seqToDoc(params.tail, ",", 
		  {s: Set[Node] => 
          	s.toList match {
          	  case List(el) => (el.toDocument)
          	  case s:List[Node] => nestedBrackets(seqToDoc(s, "|", { f:Formatable => nestedParen(f.toDocument) }))
          	}
		  }
        ))
      }
      case Abstraction(tpe, vars, subtrees) =>
        paren(
          paren(seqToDoc(vars, ",", {d: Formatable => d.toDocument})) :/: "=>" :/:
    	  nestedBrackets(subtrees.head.toDocument)
		)
    }
  }
}
