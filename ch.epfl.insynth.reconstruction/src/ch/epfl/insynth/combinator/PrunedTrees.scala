package ch.epfl.insynth.combinator

import ch.epfl.insynth.trees.Type
import ch.epfl.insynth.trees.FormatType

/**
 * can return the type of the (sub)tree
 */
trait Typable {
  def getType: Type
}

/**
 * abstract tree node
 */
abstract class Node(tpe: Type) extends Typable {
  def getType: Type = tpe  
}

case class AbsNode(tpe: Type) extends Node(tpe)

case class SimpleNode(decls:List[Declaration], tpe: Type, params:Map[Type, ContainerNode])
extends Node(tpe) {
  def getDecls = decls
  def getParams = params
}

/**
 * container for tree nodes
 */
case class ContainerNode(var nodes:Set[Node]) {
  def addNode(node:Node) {
    nodes += node
  }  
  def getNodes = nodes
}

object FormatPrNode {
    def apply(node: Node) = new FormatPrNode(node: Node)
}

class FormatPrNode(node: Node) extends ch.epfl.insynth.print.Formatable {
  def toDocument = toDocument(node)
  
  def toDocument(node: Any): scala.text.Document = {
    import ch.epfl.insynth.print.FormatHelpers._

    node match {
      case AbsNode(tpe) => "Leaf" :: paren(FormatType(tpe).toDocument)
      case SimpleNode(decls, tpe, map) =>
        "SimpleNode" :: paren(FormatType(tpe).toDocument) :: nestedBrackets(
            seqToDoc(decls, ",", { d:Declaration => strToDoc(d.getSimpleName) })
            :/:
            seqToDoc(map.toList, ",", 
              { 
            	p:(Type, ContainerNode) => paren(FormatType(p._1).toDocument) :: "->" ::
            	nestedBrackets(toDocument(p._2))
              }
            )
        )
      case ContainerNode(nodes) =>
        nestedBrackets(seqToDoc(nodes.toList, ",", toDocument(_:Node)))
        //"Container"
      // should not happen
      case _ => throw new RuntimeException
    }
  }
}
