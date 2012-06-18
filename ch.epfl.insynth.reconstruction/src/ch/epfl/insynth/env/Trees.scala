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

case class FormatNode(node: Node, simple: Boolean = false) extends ch.epfl.insynth.print.Formatable {
  override def toDocument = toDocument(node, Set.empty)
  
  def toDocument(node: Node, visited: Set[Node]): scala.text.Document = {
    import ch.epfl.insynth.print.FormatHelpers._
    import scala.text.Document._

    if (!simple)
    node match {
      case sn:SimpleNode =>
        "SimpleNode" :: nestedBrackets(
            "decls: " :/: nestedBrackets(seqToDoc(sn.getDecls, ",", { d:Declaration => strToDoc(d.getSimpleName) }))
            :/:
            "params: " :: break :: seqToDoc(sn.getParams.toList, ",", 
              { 
            	p:(Type, ContainerNode) => FormatType(p._1).toDocument :: "->" ::
            	nestedBrackets(toDocument(p._2, visited + node))
              }
            )
        )
      case cn:ContainerNode =>
        nestedBrackets(seqToDoc(cn.getNodes.toList, ",", 
        { sn:SimpleNode => 
          	if (visited contains sn) 
      		  "already visited (" :: sn.getDecls.head.getSimpleName :: ")"
      		else
      		  toDocument(sn, visited)
        }
        ))        
    }
    
    else 
    node match {
      case sn:SimpleNode =>
        "SimpleNode" :: paren(sn.getDecls.head.getSimpleName)
      case cn:ContainerNode =>
        "Container" :: paren(cn.getNodes.size)  
    }
  }
}