package ch.epfl.insynth.combinator

import ch.epfl.insynth.{ env => InSynth }
import ch.epfl.insynth.trees.Type

// TODO set required combinations in each Tree node after we hit the limit in the top
// tree

trait Combinations {
  def getNumberOfCombinations: Int
  
  var pruned: Boolean = false
  def isPruned = pruned
  def setPruned(valPruned: Boolean):Unit = { this.pruned = valPruned }  
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
  def toInSynthNode:InSynth.Node
} 

//case class TopMostDeclaration(var rootTree: Tree)
//extends Declaration(0.0d, rootTree, null) {
//  def childDone(decl: Tree):Unit = {
//    println("Total combinations: " + getNumberOfCombinations)
//  }
//  def getNumberOfCombinations = rootTree.getNumberOfCombinations
//}

object ExpansionRules {
  var doPruning = false
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
    
  override def setPruned(valPruned: Boolean):Unit = {
    super.setPruned(valPruned)
    for (dec <- decls) {
      dec.setPruned(valPruned)
    }
  }
  	
  def toInSynthNode = {
    val nodeSet = (Set[InSynth.Node]() /: decls) {
      (set, dec) => {
    	if (!dec.isPruned) set + dec.toInSynthNode
    	else set
      }
    }
    assert(!nodeSet.isEmpty)
  	InSynth.ContainerNode(nodeSet.head.getType, nodeSet)
  }
}

class TopTree(neededCombinations: Int)
extends Tree(null)
{    
  override def childDone(decl: Declaration):Unit = {
    if (neededCombinations <= getNumberOfCombinations) {
      println("Yes we found enough combinations, will start pruning.")
      ExpansionRules.doPruning = true
    }
  }
}

case class Composite(
    associatedTree: Tree, origDecl: InSynth.Declaration, associatedNode: InSynth.SimpleNode
) 
extends Declaration(origDecl.getWeight, associatedTree, associatedNode) {
  var children: Set[Tree] = Set()
  var doneChildren: Set[Tree] = Set()
  
  def addChild(decl: Tree) = {
    //println("added child " + decl + " to composite " + origDecl)
    children += decl 
  }
  
  def childDone(decl: Tree):Unit = {
    doneChildren += decl
//    if(children.size < doneChildren.size) {
//      println(associatedNode)
//      println(origDecl)
//      println(children)
//      println(doneChildren)
//    }
    // if my weight is larger or equal then prune my sub-tree
    // NOTE after the point of enough combinations, accepts only better nodes
    if (ExpansionRules.doPruning && getWeight >= associatedTree.minWeight) {
    	setPruned(true)
    	// do not add done child up the hierarchy
    }
    else {
      assert(children.size >= doneChildren.size)    
      if ((children &~ doneChildren).isEmpty) {
	    // this one is done
    	associatedTree.childDone(this)
      }
    }
  }
  
  def getNumberOfCombinations: Int =
    if (!(children &~ doneChildren).isEmpty) 0
    else (1 /: children) { (comb, decl) => comb * decl.getNumberOfCombinations }
    
  override def setPruned(valPruned: Boolean):Unit = {
    super.setPruned(valPruned)
    for (tree <- children) {
      tree.setPruned(valPruned)
    }
  }
  
  override def getWeight = {
    (super.getWeight /: doneChildren) {
      (sum, child) => sum + child.minWeight
    }
  }
  
  override def toInSynthNode = {
    assert((children &~ doneChildren).isEmpty)
    InSynth.SimpleNode(
      List(origDecl), associatedNode.getType,
      (Map[Type, InSynth.ContainerNode]() /: doneChildren) {
        (map, tree) => {
          map + (tree.decls.head.getAssociatedNode.getType -> tree.toInSynthNode)
        }
      }
    )
  }
}

case class Simple(
    associatedTree: Tree, origDecl: InSynth.Declaration,
    associatedNode: InSynth.SimpleNode
)
extends Declaration(origDecl.getWeight, associatedTree, associatedNode) {
  def getNumberOfCombinations: Int = 1
  
  override def toInSynthNode = {
    InSynth.SimpleNode(
      List(origDecl), associatedNode.getType,
      Map[Type, InSynth.ContainerNode]()
    )
  }
}

case class Leaf(associatedTree: Tree, weight: Double, associatedNode: InSynth.Leaf)
extends Declaration(weight, associatedTree, associatedNode) {
  def getNumberOfCombinations: Int = 1
  
  override def toInSynthNode = InSynth.Leaf(associatedNode.getType)
}