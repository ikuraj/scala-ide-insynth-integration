package ch.epfl.insynth.combinator

import scala.collection.mutable.PriorityQueue

import ch.epfl.insynth.{ env => InSynth }
import ch.epfl.insynth.trees._

/**
 * object which application transforms an InSynth representation input
 * to the pruned tree representation
 */
object Combinator extends ((InSynth.SimpleNode, Int) => Node) {
  
  val predefinedNeededCombinations = 5
  
  def apply(root: InSynth.SimpleNode):SimpleNode = {
    apply(root, predefinedNeededCombinations)
  }
  
  def apply(root: InSynth.SimpleNode, neededCombinations: Int) = {
    import DeclarationTransformer.fromInSynthDeclaration
    
    // TODO change this temporary weight for leaves
    val WeightForLeafs = 0.5d
    
    var pq = new PriorityQueue[Expression]()
    var visited = Set[Expression]()
    
    val rootTree:Tree = new TopTree(neededCombinations)
    val rootDeclaration = Composite(rootTree, fromInSynthDeclaration(root.decls.head), root)
    
    pq += rootDeclaration
    
    while (! pq.isEmpty) {
      val currentDeclaration = pq.dequeue
      
      if (currentDeclaration.isPruned)
        Rules.logger.fine("Declaration with " + currentDeclaration.getAssociatedNode + " pruned")
      
      if (visited.contains(currentDeclaration))
        Rules.logger.info("Stumbled upon a cycle: discarding the node.")
        
      if (!visited.contains(currentDeclaration) &&
          !currentDeclaration.isPruned) {
      
    	  visited += currentDeclaration
        
	      currentDeclaration.getAssociatedTree addDeclaration(currentDeclaration)
	      
	      currentDeclaration match {
	        case c:Composite => {
	          val paramList = c.origDecl.getType match {
	            case Arrow(TSet(list), _) => list
	            case _ => throw new RuntimeException
	          }          
	          for (parameter <- paramList) {
	            val paramTree = new Tree(c, parameter)
	            c.addChild(paramTree)
	            for(node <- c.associatedNode.params(parameter).nodes) {
	              node match {
	                case sn@InSynth.SimpleNode(decls, params) if params.isEmpty =>
	                  for (dec <- decls)
	                    if (dec.isAbstract)
	                      pq += LeafExpression(paramTree, WeightForLeafs, sn)
	                    else
                    	  pq += Simple(paramTree, fromInSynthDeclaration(dec), sn)
	                case sn@InSynth.SimpleNode(decls, _) =>
	                  for (dec <- decls)
	                    pq += Composite(paramTree, fromInSynthDeclaration(dec), sn)
	              }
	            }
	          }
	        }
	        case s:Simple => s.associatedTree.childDone(s)
	        case l:LeafExpression => l.associatedTree.childDone(l)
	      }
      }
    }
      
    rootDeclaration.toTreeNode 
  }
  
}