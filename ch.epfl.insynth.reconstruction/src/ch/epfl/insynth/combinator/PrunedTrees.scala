package ch.epfl.insynth.combinator

import ch.epfl.insynth.trees.Type

/**
 * can return the type of the (sub)tree
 */
trait Typable {
  def getType: Type
}

/**
 * abstract tree node
 */
abstract class Node(tpe: Type) extends Typable with FormatableIntermediate {
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
case class ContainerNode(var nodes:Set[Node]) extends FormatableIntermediate {
  def addNode(node:Node) {
    nodes += node
  }  
  def getNodes = nodes
}

trait FormatableIntermediate extends ch.epfl.insynth.print.Formatable {
  def toDocument = {
    import ch.epfl.insynth.print.FormatHelpers._

    this match {
      case AbsNode(tpe) => "Leaf" :: paren(tpe.toDocument)
      case SimpleNode(decls, tpe, map) =>
        "SimpleNode" :: paren(tpe.toDocument) :: nestedBrackets(
            seqToDoc(decls, ",", { d:Declaration => strToDoc(d.getSimpleName) })
            :/:
            seqToDoc(map.toList, ",", 
              { 
            	p:(Type, ContainerNode) => paren(p._1.toDocument) :: "->" ::
            	nestedBrackets(p._2.toDocument)
              }
            )
        )
      case ContainerNode(nodes) =>
        nestedBrackets(seqToDoc(nodes.toList, ",", (_:Node).toDocument))
        //"Container"
    }
  }
}
