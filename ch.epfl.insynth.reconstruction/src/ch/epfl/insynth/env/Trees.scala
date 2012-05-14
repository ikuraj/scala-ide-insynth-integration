package ch.epfl.insynth.env

import ch.epfl.insynth.trees.Type

abstract class Node(tpe:Type) {
  def getType = tpe
}

case class Leaf(tpe:Type) extends Node(tpe)

case class SimpleNode(decls:List[Declaration], tpe:Type, params:Map[Type, ContainerNode]) extends Node(tpe) {
  def getDecls = decls
  def getParams = params
}

case class ContainerNode(tpe:Type, var nodes:Set[SimpleNode]) extends Node(tpe) {
  def addNode(node:SimpleNode){
    nodes += node
  }
  
  def getNodes = nodes
}
