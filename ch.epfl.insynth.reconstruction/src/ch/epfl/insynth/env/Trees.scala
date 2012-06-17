package ch.epfl.insynth.env

import ch.epfl.insynth.trees._

import scala.collection.mutable.Set

import scala.collection.mutable.Map

trait Node // extends FormatableIntermediate

class SimpleNode(decls:List[Declaration], params:Map[Type, ContainerNode]) extends Node {
  def getDecls = decls
  def getParams = params
}

/**
 * container for tree nodes
 */
class ContainerNode(var nodes:Set[SimpleNode]) extends Node {
  
  def this() = this(Set.empty)
  
  def addNode(node:SimpleNode){
    nodes += node
  }
  
  def getNodes = nodes
}

case class FormatNode(node: Node) extends ch.epfl.insynth.print.Formatable {
  override def toDocument = toDocument(node)
  
  def toDocument(node: Node): scala.text.Document = {
    import ch.epfl.insynth.print.FormatHelpers._

    node match {
      case sn:SimpleNode =>
        "SimpleNode" :: nestedBrackets(
            seqToDoc(sn.getDecls, ",", { d:Declaration => strToDoc(d.getSimpleName) })
//            :/:
//            seqToDoc(map.toList, ",", 
//              { 
//            	p:(Type, ContainerNode) => paren(p._1.toDocument) :: "->" ::
//            	nestedBrackets(p._2.toDocument)
//              }
//            )
        )
      case cn:ContainerNode =>
        nestedBrackets(seqToDoc(cn.getNodes.toList, ",", toDocument(_:Node)))        
    }
  }
}