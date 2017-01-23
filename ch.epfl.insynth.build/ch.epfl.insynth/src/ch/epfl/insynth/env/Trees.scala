package ch.epfl.insynth.env

import ch.epfl.insynth.trees._
import scala.collection.mutable.Set
import scala.collection.mutable.Map
import ch.epfl.scala.trees.FormatScalaType

trait Node

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

case class FormatNode(node: Node, levels: Int = -1) extends ch.epfl.insynth.print.Formatable {
  override def toDocument = toDocument(node, Set.empty, 0)
  
  def toDocument(node: Node, visited: Set[Node], level: Int): scala.text.Document = {
    import ch.epfl.insynth.print.FormatHelpers._
    import scala.text.Document._
    
    val simple = levels == level

    if (!simple)
    node match {
      case sn:SimpleNode =>
        "SimpleNode" :: nestedBrackets(
            "decls: " :/: nestedBrackets(seqToDoc(sn.getDecls, ",", { d:Declaration => strToDoc(d.getSimpleName) :: "[" :: FormatScalaType(d.scalaType).toDocument :: "]" }))
            :/:
            "params: " :: break :: seqToDoc(sn.getParams.toList, ",", 
              { 
            	p:(Type, ContainerNode) => FormatType(p._1).toDocument :: "->" ::
            	nestedBrackets(toDocument(p._2, visited + node, level + 1))
              }
            )
        )
      case cn:ContainerNode =>
        nestedBrackets(seqToDoc(cn.getNodes.toList, ",", 
        { sn:SimpleNode => 
          	if (visited contains sn) 
      		  "already visited (" :: sn.getDecls.head.getSimpleName :: ")"
      		else
      		  toDocument(sn, visited, level + 1)
        }
        ))        
    }
    
    else 
    node match {
      case sn:SimpleNode =>
        "SimpleNode*" :: paren(sn.getDecls.head.getSimpleName)
      case cn:ContainerNode =>
        "Container*" :: paren(cn.getNodes.size)  
    }
  }
}