package ch.epfl.insynth.combinator

import ch.epfl.insynth.{ env => InSynth }
import ch.epfl.insynth.trees.{ Type, BottomType }
import java.util.logging.Logger
import ch.epfl.insynth.trees.FormatType
import ch.epfl.insynth.reconstruction.trees.NullLeaf
import ch.epfl.insynth.env.FormatNode
import java.util.logging.Level

// TODO set required combinations in each Tree node after we hit the limit in the top
// tree
// TODO shared and private parameters of abstractions (not to explore two trees that
// are the same and shared between abstractions)
// TODO refactor Tree (subclass normal and top tree)

/**
 * dictates some rules about the combination search
 */
object Rules {
  val logger = Logger.getLogger("reconstruction.combination")
  val logStructures = Logger.getLogger("reconstruction.combination.structures")
  
  {
    Logger.getLogger("reconstruction.combination").setLevel(Level.FINEST)
    Logger.getLogger("reconstruction.combination.apply").setLevel(Level.FINEST)
    Logger.getLogger("reconstruction.combination.structures").setLevel(Level.FINEST)
  }
  
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
  
  // is the node ready to transform
  def isDone: Boolean
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
  
  // for set operations
//  override def equals(o: Any) = o match {
//    case that: Expression => that.getAssociatedNode.equals(this.getAssociatedNode)
//    case _ => false
//  }
//  override def hashCode = getAssociatedNode.hashCode
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
  
  def addDeclaration(dec: Expression) {
    decls += dec
  }
  def getDeclarations:Set[Expression] = decls
    
  // this method is called when some associated expression is explored
  def childDone(decl: Expression):Unit = {
    minWeight = Math.min(decl.getWeight, minWeight)
    parent.childDone(this)
  }
  
  override def isDone =
    // just check if minimal weight has changed (is lower than the max double value)
    minWeight != Double.MaxValue && !isPruned
  
  override def getNumberOfCombinations =
    (0 /: decls) { (comb, decl) => comb + decl.getNumberOfCombinations }
      
  override def setPruned(valPruned: Boolean):Unit = {
    super.setPruned(valPruned)
    for (dec <- decls; if !dec.isPruned) {
      dec.setPruned(valPruned)
    }
    // logging
    if (!isDone && parent.isDone) {
      Rules.logStructures.warning("Pruning (" + FormatType(tpe) + ") but its parent is done.")
    }
  }
  	
  def toTreeNode = {
    // logging
    Rules.logStructures.entering(getClass.getName, "toTreeNode")
    Rules.logStructures.info("toTreeNode started on Tree: " + FormatType(tpe))
    // transform only those expressions that are done
    val declsToTransform = decls filter { _.isDone }
    // logging
    if (declsToTransform.isEmpty) {
      Rules.logger.warning("Tree (" + FormatType(tpe) + "): toTree has none done expressions "
	    + "(declsToTransform: " + ("" /: declsToTransform){ (s, t) => s + ", "  + FormatNode(t.getAssociatedNode) } + ")")
    }
    val nodeSet = (Set[Node]() /: declsToTransform) {
      (set, dec) => {
    	if (!dec.isPruned) set + dec.toTreeNode
    	else set
      }
    }
    // logging
    if (nodeSet.isEmpty) {
      Rules.logger.warning("Tree (" + FormatType(tpe) + "): toTree transformed nodes set empty")
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
    Rules.logger.fine("Added child " + decl + " to composite " + origDecl.getSimpleName)
    children += decl 
  }
  
  def childDone(decl: Tree):Unit = {
    doneChildren += decl
    // if my weight is larger or equal then prune my sub-tree
    // NOTE after the point of enough combinations, accepts only better nodes
    // NOTE we can get a call to childDone again but minWeight will be the same as
    // previously set getWeight (we need >)
    if (Rules.doPruning && getWeight > associatedTree.minWeight && !isPruned) {
    	Rules.logger.info("Pruning Composite (" + FormatNode(associatedNode, true) + ")")
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
  
  override def isDone =
    // check if all children are done
    (children &~ doneChildren).isEmpty && !isPruned
  
  def getNumberOfCombinations: Int =
    if (!(children &~ doneChildren).isEmpty) 0
    else (1 /: children) { (comb, decl) => comb * decl.getNumberOfCombinations }
    
  override def setPruned(valPruned: Boolean):Unit = {
    // logging
    if (associatedTree.minWeight >= getWeight && !associatedTree.isPruned) {
      Rules.logStructures.warning("Pruning (" + FormatNode(associatedNode) + ") but it has the min weight at associated tree.")
    }
    super.setPruned(valPruned)
    for (tree <- children; if !tree.isPruned) {
      tree.setPruned(valPruned)
    }
  }
  
  override def getWeight = {
    // return weight as sum of geWeight in super class and weights of all children
    (super.getWeight /: doneChildren) {
      (sum, child) => sum + child.minWeight
    }
  }
  
  override def toTreeNode = {
    // logging
    Rules.logStructures.entering(getClass.getName, "toTreeNode")
    Rules.logStructures.info("toTreeNode started on Composite: " + origDecl.getSimpleName)
    // logging
    if (!(children &~ doneChildren).isEmpty) {
      Rules.logger.warning("Composite " + origDecl.getSimpleName + " toTree has not all children done "
	    + "(children: " + ("" /: children){ (s, t) => s + ", "  + FormatType(t.tpe) } 
      	+ ", doneChildren: " + ("" /: doneChildren){ (s, t) => s + ", "  + FormatType(t.tpe) } + ")")
  	  Rules.logger.warning("The toTreeNode failed composite has " + getNumberOfCombinations + " combinations.")
    }
    // this assert is needed since transform should not be called if node is not done
    assert((children &~ doneChildren).isEmpty)
    // return simple node
    SimpleNode(
      List(origDecl), associatedTree.tpe,
      (Map[Type, ContainerNode]() /: doneChildren) {
        (map, tree) => {
          map + (tree.tpe -> tree.toTreeNode)
        }
      }
    )
  }
  
  override def toString =
    "Composite(" + origDecl.getSimpleName + ")"
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
    // logging
    Rules.logStructures.entering(getClass.getName, "toTreeNode")
    Rules.logStructures.info("toTreeNode started on: " + origDecl.getSimpleName)
    SimpleNode(
      List(origDecl), associatedTree.tpe, Map[Type, ContainerNode]()
    )
  }
  
  override def isDone = !isPruned
  
  override def toString =
    "Simple(" + origDecl.getSimpleName + ")"
}

/**
 * corresponds to a leaf node which is an expression from context
 */
case class LeafExpression(associatedTree: Tree, weight: Double, associatedNode: InSynth.SimpleNode)
extends Expression(weight, associatedTree, associatedNode) {
  def getNumberOfCombinations: Int = 1
  
  override def toTreeNode = {
    // logging
    Rules.logStructures.entering(getClass.getName, "toTreeNode")    
    Rules.logStructures.info("toTreeNode started on: " + FormatNode(associatedNode))
    AbsNode(associatedTree.tpe)
  }
  
  override def isDone = !isPruned
  
  override def toString =
    "LeafExpression"
}

case class FormatCombinations(comb: Combinations) extends ch.epfl.insynth.print.Formatable {
  override def toDocument = toDocument(comb)
  
  def toDocument(comb: Combinations): scala.text.Document = {
    import ch.epfl.insynth.print.FormatHelpers._
    import scala.text.Document._

    comb match {
      case tree:Tree =>
        "Tree" :: paren(FormatType(tree.tpe).toDocument) :/: 
        "[Done?" :: tree.isDone.toString :: "]" :: 
        nestedBrackets(
          seqToDoc(tree.decls.toList, ", ", { e:Expression => toDocument(e) })
        )
        //associatedTree: Tree, origDecl: Declaration, associatedNode: InSynth.SimpleNode
      case composite:Composite =>
        "Composite" :: paren(composite.origDecl.getSimpleName) :/:
        "[Done?" :: composite.isDone.toString :: "]" ::
        nestedBrackets(seqToDoc(composite.children.toList, ", ", { e:Tree => toDocument(e) }))
      case simple:Simple =>
        "Simple" :: paren(simple.origDecl.getSimpleName)
      case leaf:LeafExpression =>
        "Leaf"
    }
  }
}