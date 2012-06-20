package ch.epfl.insynth.combinator

import ch.epfl.insynth.{ env => InSynth }
import ch.epfl.insynth.trees.{ Type, BottomType }
import java.util.logging.Logger
import ch.epfl.insynth.trees.FormatType
import ch.epfl.insynth.reconstruction.trees.NullLeaf
import ch.epfl.insynth.env.FormatNode
import java.util.logging.Level
import java.util.logging.ConsoleHandler

// TODO set required combinations in each Tree node after we hit the limit in the top
// tree
// TODO shared and private parameters of abstractions (not to explore two trees that
// are the same and shared between abstractions)
// TODO refactor Tree (subclass normal and top tree)
// TODO isDone and isPruned sometimes redundant

/**
 * dictates some rules about the combination search and declares loggers
 */
object Rules {
  // defines loggers
  val logger = Logger.getLogger("reconstruction.combination")
  val logStructures = Logger.getLogger("reconstruction.combination.structures")
  val logApply = Logger.getLogger("reconstruction.combination.apply")
  
  val isLogging = false
  
  // static code for loggers setup
  {    
    for (handler <- Logger.getLogger("reconstruction.combination").getHandlers)
      Logger.getLogger("reconstruction.combination").removeHandler(handler)
    for (handler <- Logger.getLogger("reconstruction.combination.apply").getHandlers)
      Logger.getLogger("reconstruction.combination.apply").removeHandler(handler)
    for (handler <- Logger.getLogger("reconstruction.combination.structures").getHandlers)
      Logger.getLogger("reconstruction.combination.structures").removeHandler(handler)
      
    Logger.getLogger("reconstruction.combination").setLevel(Level.FINER)
    Logger.getLogger("reconstruction.combination.apply").setLevel(Level.FINER)
    Logger.getLogger("reconstruction.combination.structures").setLevel(Level.FINER)
      
    val handler = new ConsoleHandler();
    // PUBLISH this level
    handler.setLevel(Level.FINER);
    //Logger.getLogger("reconstruction.combination").addHandler(handler);
  }
  
  // if the pruning started
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
  
  // get weight value with respect to the traversal
  def getTraversalWeight: Double
}

/**
 * represents a node in the proof tree
 * this is the entity that is put in the priority queue to perform the search
 */
abstract class Expression(
    associatedTree: Tree, associatedNode: InSynth.Node
)
extends Combinations with Ordered[Expression] {
  // comparison of expression is done by their weights
  def compare(that:Expression) = {
    val thatVal = that.getTraversalWeight
      if (getTraversalWeight < thatVal) -1
      else if (getTraversalWeight > thatVal) 1
      else 0
  }
    
  // getters
  def getAssociatedTree = associatedTree
  def getAssociatedNode = associatedNode
  
  // reconstruct the output tree node
  def toTreeNode:Node
  
  // traversal weight returns the weight of associated tree
  override def getTraversalWeight = {
    associatedTree.getTraversalWeight
  }
  
  // for set operations
//  override def equals(o: Any) = o match {
//    case that: Expression => that.getAssociatedNode.equals(this.getAssociatedNode)
//    case _ => false
//  }
//  override def hashCode = getAssociatedNode.hashCode    
} 

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
  
  // add declaration to this tree (when it is explored)
  def addDeclaration(dec: Expression) {
    // logging
    if (Rules.isLogging && isPruned) {
      Rules.logStructures.warning("Adding declaration " + dec + " to pruned tree " + this)
    }
    decls += dec
  }
  
  // getters
  def getDeclarations:Set[Expression] = decls
  def getParent = parent
    
  // this method is called when some associated expression is explored
  def childDone(decl: Expression):Unit = {
    minWeight = Math.min(decl.getTraversalWeight, minWeight)
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
    if (Rules.isLogging && !isDone && parent.isDone) {
      Rules.logStructures.warning("Pruning (" + FormatType(tpe) + ") but its parent is done.")
    }
  }
  	
  def toTreeNode = {
    // logging
    if (Rules.isLogging) {
	    Rules.logStructures.entering(getClass.getName, "toTreeNode")
	    Rules.logStructures.info("toTreeNode started on Tree: " + FormatType(tpe))
    }
    
    // transform only those expressions that are done
    val declsToTransform = decls filter { _.isDone }
    
    // logging
    if (Rules.isLogging && declsToTransform.isEmpty) {
      Rules.logger.warning("Tree (" + FormatType(tpe) + "): toTree has none done expressions "
	    + "(declsToTransform: " + ("" /: declsToTransform){ (s, t) => s + ", "  + FormatNode(t.getAssociatedNode) } + ")")
    }
    
    // transform declarations associated with this node
    val nodeSet = (Set[Node]() /: declsToTransform) {
      (set, dec) => {
        // transform declaration only if it is not pruned
    	if (!dec.isPruned) set + dec.toTreeNode
    	else set
      }
    }
    
    // logging
    if (Rules.isLogging && nodeSet.isEmpty) {
      Rules.logger.warning("Tree (" + FormatType(tpe) + "): toTree transformed nodes set empty")
    }
    
    // set of transformed nodes should not be empty
    assert(!nodeSet.isEmpty)
    //return a container node with the set of transformed nodes
  	ContainerNode(nodeSet)
  }
  
  override def toString =
    "Tree(" + 
    ("" /: decls ) { 
	  (string, dec) => { string + "," + dec.getAssociatedNode.toString }
  	} + ")"
  	
  // traversal weight corresponds to the one of the parent (composite)	
  override def getTraversalWeight = parent.getTraversalWeight
  	
  // check if the weight is pruned at this tree
  // a method that can speed up pruning (if weight at the start is greater than minWeight
  // of some parent tree up the hierarchy)
  def checkIfPruned(weight: Double): Boolean = {
    if (weight > minWeight) true
  	else parent.associatedTree.checkIfPruned(weight)
  }
}

/**
 * tree that is at the top of the hierarchy (if its child is done, the tree is
 * has some expressions explored) 
 */
class TopTree(neededCombinations: Int)
extends Tree(null, BottomType)
{    
  override def childDone(decl: Expression):Unit = {
    Rules.logStructures.info("Child done at top tree called.")
    if (neededCombinations <= getNumberOfCombinations) {
      Rules.logger.info("Yes we found enough combinations, will start pruning.")
      Rules.doPruning = true
    }
  }
  
  override def checkIfPruned(weight: Double): Boolean = {
	false
  }
  
  override def getTraversalWeight = 0
}

/**
 * corresponds to a declaration
 */
case class Composite(
    associatedTree: Tree, origDecl: Declaration, associatedNode: InSynth.SimpleNode
) 
extends Expression(associatedTree, associatedNode) {
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
    if (Rules.doPruning && getTraversalWeight > associatedTree.minWeight && !isPruned) {
      if (Rules.isLogging)	
    	Rules.logger.info("Pruning Composite (" + FormatNode(associatedNode, true) + ")")
    	
      // mark the node as pruned
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
    if (Rules.isLogging && associatedTree.minWeight >= getTraversalWeight && !associatedTree.isPruned) {
      Rules.logStructures.warning("Pruning (" + FormatNode(associatedNode, true) + ") but it has the min weight at associated tree.")
    }
    super.setPruned(valPruned)
    for (tree <- children; if !tree.isPruned) {
      tree.setPruned(valPruned)
    }
  }
  
  override def toTreeNode = {
    // logging
    if (Rules.isLogging) {
		Rules.logStructures.entering(getClass.getName, "toTreeNode")
		Rules.logStructures.info("toTreeNode started on Composite: " + origDecl.getSimpleName)
		if (!(children &~ doneChildren).isEmpty) {
		  Rules.logger.warning("Composite " + origDecl.getSimpleName + " toTree has not all children done "
		    + "(children: " + ("" /: children){ (s, t) => s + ", "  + FormatType(t.tpe) } 
		  	+ ", doneChildren: " + ("" /: doneChildren){ (s, t) => s + ", "  + FormatType(t.tpe) } + ")")
		  Rules.logger.warning("The toTreeNode failed composite has " + getNumberOfCombinations + " combinations.")
		}
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
    
  override def getTraversalWeight = {
    // return weight as sum of geWeight in super class and weights of all children
//    (super.getWeight /: doneChildren) {
//      (sum, child) => sum + child.minWeight
//    }
    super.getTraversalWeight + origDecl.getWeight
  }
  
  override def toString =
    "Composite(" + origDecl.getSimpleName + ":" + getTraversalWeight + ")"
}

/**
 * corresponds to a leaf node of a declared identifier
 */
case class Simple(
    associatedTree: Tree, origDecl: Declaration,
    associatedNode: InSynth.SimpleNode
)
extends Expression(associatedTree, associatedNode) {
  def getNumberOfCombinations: Int = 1
  
  override def toTreeNode = {
    // logging
    if (Rules.isLogging) {
	    Rules.logStructures.entering(getClass.getName, "toTreeNode")
	    Rules.logStructures.info("toTreeNode started on: " + origDecl.getSimpleName)
    }
    SimpleNode(
      List(origDecl), associatedTree.tpe, Map[Type, ContainerNode]()
    )
  }
  
  override def isDone = !isPruned
  
  override def getTraversalWeight = {
    super.getTraversalWeight + origDecl.getWeight
  }
  
  override def toString =
    "Simple(" + origDecl.getSimpleName + ":" + getTraversalWeight + ")"
}

/**
 * corresponds to a leaf node which is an expression from context
 */
case class LeafExpression(associatedTree: Tree, weight: Double, associatedNode: InSynth.SimpleNode)
extends Expression(associatedTree, associatedNode) {
  def getNumberOfCombinations: Int = 1
  
  override def toTreeNode = {
    // logging
    if (Rules.isLogging) {
	    Rules.logStructures.entering(getClass.getName, "toTreeNode")    
	    Rules.logStructures.info("toTreeNode started on: " + FormatNode(associatedNode, true))
    }
    AbsNode(associatedTree.tpe)
  }
  
  override def isDone = !isPruned
  
  override def toString =
    "LeafExpression(" + getTraversalWeight + ")"
    
  override def getTraversalWeight = {
    super.getTraversalWeight + weight
  }
}

case class FormatCombinations(comb: Combinations) extends ch.epfl.insynth.print.Formatable {
  override def toDocument = toDocument(comb)
  
  def toDocument(comb: Combinations): scala.text.Document = {
    import ch.epfl.insynth.print.FormatHelpers._
    import scala.text.Document._

    comb match {
      case tree:Tree =>
        "Tree" :: paren(FormatType(tree.tpe).toDocument) ::
        brackets( tree.getTraversalWeight.toString ) :/: 
        "[Done?" :: tree.isDone.toString :: "]" :: 
        nestedBrackets(
          seqToDoc(tree.decls.toList, ", ", { e:Expression => toDocument(e) })
        )
        //associatedTree: Tree, origDecl: Declaration, associatedNode: InSynth.SimpleNode
      case composite:Composite =>
        "Composite" :: paren(composite.origDecl.getSimpleName) ::
        brackets( composite.getTraversalWeight.toString ) :/:
        "[Done?" :: composite.isDone.toString :: "]" ::
        nestedBrackets(seqToDoc(composite.children.toList, ", ", { e:Tree => toDocument(e) }))
      case simple:Simple =>
        "Simple"  ::
        brackets( comb.getTraversalWeight.toString ) :: paren(simple.origDecl.getSimpleName)
      case leaf:LeafExpression =>
        "Leaf" :: brackets( comb.getTraversalWeight.toString )
    }
  }
}