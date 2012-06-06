package ch.epfl.insynth.combinator

import ch.epfl.insynth.{ env => InSynth }
import ch.epfl.insynth.trees.{ Type, BottomType }
import java.util.logging.Logger

// TODO set required combinations in each Tree node after we hit the limit in the top
// tree
// TODO shared and private parameters of abstractions (not to explore two trees that
// are the same and shared between abstractions)
// TODO refactor Tree (subclass normal and top tree)

/**
 * dictates some rules about the combination search
 */
object Rules {
  val logger = Logger.getLogger(Rules.getClass.getName)
  
  var doPruning = false
}

/**
 * represents a node in a combinator tree that knows how many tree
 * combinations it can yield
 */
trait Combinations {
  def getNumberOfCombinations: Int
  
  // pruned tells us if the combination search should neglect the search down this node
  var pruned: Boolean = false
  def isPruned = pruned
  def setPruned(valPruned: Boolean):Unit = { this.pruned = valPruned }  
  
}

/**
 * represents a node in the proof tree
 * this is the entity that is put in the priority queue to perform the search
 */
abstract class Expression(
    weight: Double, associatedTree: Tree, associatedNode: InSynth.Node
)
extends Combinations with Ordered[Expression] {
  // comparison of expression is done by their weights
  def compare(that:Expression) = {
    val thatVal = that.getWeight
      if (getWeight < thatVal) -1
      else if (getWeight > thatVal) 1
      else 0
  }
  
  def getWeight = weight  
  def getAssociatedTree = associatedTree
  def getAssociatedNode = associatedNode
  
  // reconstruct the output tree node
  def toTreeNode:Node
} 

//case class TopMostDeclaration(var rootTree: Tree)
//extends Declaration(0.0d, rootTree, null) {
//  def childDone(decl: Tree):Unit = {
//    println("Total combinations: " + getNumberOfCombinations)
//  }
//  def getNumberOfCombinations = rootTree.getNumberOfCombinations
//}

/**
 * represents some parameter in the synthesized expression
 * it can have multiple alternatives to synthesize 
 */
class Tree(parent: Composite, val tpe: Type, var decls: Set[Expression] = Set())
extends Combinations
{
  // minimal weight of synthesized expression (used for pruning expressions with
  // larger weight)
  var minWeight = Double.MaxValue
  
  def addDeclaration(dec: Expression){
    decls += dec
  }
  def getDeclarations:Set[Expression] = decls
    
  // this method is called when some associated expression is explored
  def childDone(decl: Expression):Unit = {
    minWeight = Math.min(decl.getWeight, minWeight)
    parent.childDone(this)
  }
  
  override def getNumberOfCombinations =
    (0 /: decls) { (comb, decl) => comb + decl.getNumberOfCombinations }
      
  override def setPruned(valPruned: Boolean):Unit = {
    super.setPruned(valPruned)
    for (dec <- decls) {
      dec.setPruned(valPruned)
    }
  }
  	
  def toTreeNode = {
    val nodeSet = (Set[Node]() /: decls) {
      (set, dec) => {
    	if (!dec.isPruned) set + dec.toTreeNode
    	else set
      }
    }
    assert(!nodeSet.isEmpty)
  	ContainerNode(nodeSet)
  }
  
  override def toString =
    "Tree(" + 
    ("" /: decls ) { 
	  (string, dec) => { string + "," + dec.getAssociatedNode.toString }
  	} + ")"
}

/**
 * tree that is at the top of the hierarchy (if its child is done, the tree is
 * has some expressions explored) 
 */
class TopTree(neededCombinations: Int)
extends Tree(null, BottomType)
{    
  override def childDone(decl: Expression):Unit = {
    if (neededCombinations <= getNumberOfCombinations) {
      Rules.logger.fine("Yes we found enough combinations, will start pruning.")
      Rules.doPruning = true
    }
  }
}

/**
 * corresponds to a declaration
 */
case class Composite(
    associatedTree: Tree, origDecl: Declaration, associatedNode: InSynth.SimpleNode
) 
extends Expression(origDecl.getWeight, associatedTree, associatedNode) {
  var children: Set[Tree] = Set()
  var doneChildren: Set[Tree] = Set()
  
  def addChild(decl: Tree) = {
    Rules.logger.fine("added child " + decl + " to composite " + origDecl)
    children += decl 
  }
  
  def childDone(decl: Tree):Unit = {
    doneChildren += decl
    // if my weight is larger or equal then prune my sub-tree
    // NOTE after the point of enough combinations, accepts only better nodes
    if (Rules.doPruning && getWeight >= associatedTree.minWeight) {
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
  
  override def toTreeNode = {
    assert((children &~ doneChildren).isEmpty)
    SimpleNode(
      List(origDecl), associatedTree.tpe,
      (Map[Type, ContainerNode]() /: doneChildren) {
        (map, tree) => {
          map + (tree.tpe -> tree.toTreeNode)
        }
      }
    )
  }
}

/**
 * corresponds to a leaf node of a declared identifier
 */
case class Simple(
    associatedTree: Tree, origDecl: Declaration,
    associatedNode: InSynth.SimpleNode
)
extends Expression(origDecl.getWeight, associatedTree, associatedNode) {
  def getNumberOfCombinations: Int = 1
  
  override def toTreeNode = {
    SimpleNode(
      List(origDecl), associatedTree.tpe, Map[Type, ContainerNode]()
    )
  }
}

/**
 * corresponds to a leaf node which is an expression from context
 */
case class LeafExpression(associatedTree: Tree, weight: Double, associatedNode: InSynth.SimpleNode)
extends Expression(weight, associatedTree, associatedNode) {
  def getNumberOfCombinations: Int = 1
  
  override def toTreeNode = AbsNode(associatedTree.tpe)
}