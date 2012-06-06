package ch.epfl.insynth.env

import ch.epfl.insynth.trees._

trait Node extends FormatableIntermediate

case class SimpleNode(decls:List[Declaration], params:Map[Type, ContainerNode]) extends Node {
  def getDecls = decls
  def getParams = params
}

/**
 * container for tree nodes
 */
case class ContainerNode(var nodes:Set[SimpleNode]) extends Node {
  
  def this() = this(Set.empty)
  
  def addNode(node:SimpleNode){
    nodes += node
  }
  
  def getNodes = nodes
}

trait FormatableIntermediate extends ch.epfl.insynth.print.Formatable {
  def toDocument = {
    import ch.epfl.insynth.print.FormatHelpers._

    this match {
      case SimpleNode(decls, map) =>
        "SimpleNode" :: nestedBrackets(
            seqToDoc(decls, ",", { (_:Declaration).toDocument })
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
      case ad@Declaration(fullName, inSynthType, scalaType) if ad.isAbstract =>
        strToDoc(fullName) :/: inSynthType.toDocument
      case Declaration(fullName, inSynthType, scalaType) =>
        strToDoc(fullName) :/: scalaType.toString
    }
  }
}