package ch.epfl.insynth.combinator

import ch.epfl.insynth.{ env => InSynth }

trait Combinations {
  def getNumberOfCombinations: Int
}

abstract class Declaration(
    weight: Double, associatedTree: Tree, associatedNode: InSynth.Node
)
extends Combinations with Ordered[Declaration] {
  def compare(that:Declaration) = {
    val thatVal = that.getWeight
      if (getWeight < thatVal) -1
      else if (getWeight > thatVal) 1
      else 0
  }
  
  def getWeight = weight  
  def getAssociatedTree = associatedTree
  def getAssociatedNode = associatedNode
  
  var pruned: Boolean = false
  def isPruned = pruned
  def setPruned(valPruned: Boolean) = { this.pruned = valPruned }
} 

case class TopMostDeclaration(var rootTree: Tree)
extends Declaration(0.0d, rootTree, null) {
  def childDone(decl: Tree):Unit = {
    println("Total combinations: " + getNumberOfCombinations)
  }
  def getNumberOfCombinations = rootTree.getNumberOfCombinations
}

class Tree(parent: Composite, var decls: Set[Declaration] = Set())
extends Combinations
{
  var minWeight = Double.MaxValue
  
  def addDeclaration(dec: Declaration){
    decls += dec
  }
  def getDeclarations:Set[Declaration] = decls
  
  def getNumberOfCombinations =
    (0 /: decls) { (comb, decl) => comb + decl.getNumberOfCombinations }
  
  def childDone(decl: Declaration):Unit = {
    minWeight = Math.min(decl.getWeight, minWeight)
    parent.childDone(this)
  }
  
  override def toString =
    "Tree(" + 
    ("" /: decls ) { 
	  (string, dec) => { string + "," + dec.getAssociatedNode.toString }
  	} + ")"
}

class TopTree(neededCombinations: Int)
extends Tree(null)
{    
  override def childDone(decl: Declaration):Unit = {
    if (neededCombinations <= getNumberOfCombinations)
      println("Yes we got it!")
  }
}

case class Composite(
    associatedTree: Tree, origDecl: InSynth.Declaration, associatedNode: InSynth.SimpleNode
) 
extends Declaration(origDecl.getWeight, associatedTree, associatedNode) {
  var children: Set[Tree] = Set()
  var doneChildren: Set[Tree] = Set()
  
  def addChild(decl: Tree) = {
    println("added child " + decl + " to composite " + origDecl)
    children += decl 
  }
  
  def childDone(decl: Tree):Unit = {
    doneChildren += decl
    if(children.size < doneChildren.size) {
      println(associatedNode)
      println(origDecl)
      println(children)
      println(doneChildren)
    }
    assert(children.size >= doneChildren.size)
    if ((children &~ doneChildren).isEmpty) {
      // this one is done
      associatedTree.childDone(this)
    }
  }
  
  def getNumberOfCombinations: Int =
    if (!(children &~ doneChildren).isEmpty) 0
    else (1 /: children) { (comb, decl) => comb * decl.getNumberOfCombinations }
}

case class Simple(
    associatedTree: Tree, origDecl: InSynth.Declaration,
    associatedNode: InSynth.SimpleNode
)
extends Declaration(origDecl.getWeight, associatedTree, associatedNode) {
  def getNumberOfCombinations: Int = 1
}

case class Leaf(associatedTree: Tree, weight: Double, associatedNode: InSynth.Leaf)
extends Declaration(weight, associatedTree, associatedNode) {
  def getNumberOfCombinations: Int = 1
}