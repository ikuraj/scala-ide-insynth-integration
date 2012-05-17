package ch.epfl.insynth.env

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
abstract class Node(tpe:Type) extends Typable {
  def getType = tpe
}

case class Leaf(tpe:Type) extends Node(tpe)

case class SimpleNode(decls:List[Declaration], tpe:Type, params:Map[Type, ContainerNode]) extends Node(tpe) {
  def getDecls = decls
  def getParams = params
}

/**
 * container for tree nodes
 */
case class ContainerNode(tpe:Type, var nodes:Set[Node]) extends Typable {
  def getType = tpe
  
  def addNode(node:Node){
    nodes += node
  }  
  def getNodes = nodes
}
