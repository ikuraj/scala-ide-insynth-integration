package ch.epfl.insynth.combinator

import ch.epfl.insynth.env.{ Declaration => ISDeclaration, Leaf => LeafNode, _ }
import scala.collection.mutable.PriorityQueue
import ch.epfl.insynth.trees._

object Combinator extends ((SimpleNode, Int) => Node) {
  
  def apply(root: SimpleNode, neededCombinations: Int) = {
    
    val WeightForLeafs = 0.5d
    
    var pq = new PriorityQueue[Declaration]()
    
    val rootTree:Tree = new TopTree(neededCombinations)
    val rootDeclaration = Composite(rootTree, root.decls.head, root)
    
    pq += rootDeclaration
    
    while (! pq.isEmpty) {
      val currentDeclaration = pq.dequeue
      
      if (currentDeclaration.isPruned)
        println("Declaration with " + currentDeclaration.getAssociatedNode + " pruned")
      
      if (!currentDeclaration.isPruned) {
      
	      currentDeclaration.getAssociatedTree addDeclaration(currentDeclaration)
	      
	      currentDeclaration match {
	        case c:Composite => {
	          val paramList = c.origDecl.getType match {
	            case Arrow(TSet(list), _) => list
	            case _ => throw new RuntimeException
	          }          
	          for (parameter <- paramList) {
	            val paramTree = new Tree(c)
	            c.addChild(paramTree)
	            for(node <- c.associatedNode.params(parameter).nodes) {
	              node match {
	                case sn@SimpleNode(decls, _, params) if params.isEmpty =>
	                  for (dec <- decls)
	                    pq += Simple(paramTree, dec, sn)
	                case sn@SimpleNode(decls, _, _) =>
	                  for (dec <- decls)
	                    pq += Composite(paramTree, dec, sn)
	                case l:LeafNode =>
	                  pq += Leaf(paramTree, WeightForLeafs, l)
	              }
	            }
	          }
	        }
	        case s:Simple => s.associatedTree.childDone(s)
	        case l:Leaf => l.associatedTree.childDone(l)
	      }
      }
    }
      
    rootDeclaration.toInSynthNode 
  }
  
}