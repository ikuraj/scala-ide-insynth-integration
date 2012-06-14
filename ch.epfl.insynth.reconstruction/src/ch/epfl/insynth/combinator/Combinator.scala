package ch.epfl.insynth.combinator

import scala.collection.mutable.PriorityQueue

import ch.epfl.insynth.{ env => InSynth }
import ch.epfl.insynth.trees._

/**
 * object which application transforms an InSynth representation input
 * to the pruned tree representation
 */
object Combinator extends ((InSynth.SimpleNode, Int) => Node) {
  
  // predefined default number of combinations
  val predefinedNeededCombinations = 5
  
  // apply without specifying the number of combinations
  def apply(root: InSynth.SimpleNode):SimpleNode = {
    apply(root, predefinedNeededCombinations)
  }
  
  // apply with number of combinatinos needed
  def apply(root: InSynth.SimpleNode, neededCombinations: Int) = {
    // import transformer from InSynth to intermediate declaration
    import DeclarationTransformer.fromInSynthDeclaration
    
    // TODO change this temporary weight for leaves
    val WeightForLeafs = 0.5d
      
    // pair type that will be put in the priority queue
    // ( expression to be explored, a set of expressions visited on the path )
    type ExpressionPair = (Expression, Set[Expression])
    // priority queue
    var pq = new PriorityQueue[ExpressionPair]() (
      // ordering defined on the expressions  
      new Ordering[ExpressionPair] {                                                                    
	    def compare(a : ExpressionPair, b : ExpressionPair) = a._1.compare(b._1)                                           
      } 
    )    
    // declare root Tree which starts the hierarchy 
    val rootTree:Tree = new TopTree(neededCombinations)
    // a single declaration of the root Tree is the one corresponding to the root node
    val rootDeclaration = Composite(rootTree, fromInSynthDeclaration(root.decls.head), root)
    
    /* start the traversal */
    
    // add the root declaration and an empty set
    val startTuple = (rootDeclaration, Set[Expression]())
    pq += startTuple
    
    // while there is something in the queue
    while (! pq.isEmpty) {
      // dequeue a pair
      val (currentDeclaration, visited) = pq.dequeue
      
      // if current declaration is pruned or already visited on this path, log
      if (currentDeclaration.isPruned)
        Rules.logger.fine("Declaration with " + currentDeclaration.getAssociatedNode + " pruned")
      if (visited.contains(currentDeclaration))
        Rules.logger.info("Stumbled upon a cycle: discarding the node.")
        
      // if current declaration is pruned or already visited on this path, ignore it
      if (!visited.contains(currentDeclaration) && !currentDeclaration.isPruned) {
      
    	  // add explored declaration to its associated tree as explored
	      currentDeclaration.getAssociatedTree addDeclaration(currentDeclaration)
	      // check the type of the current declaration
	      currentDeclaration match {
	        // Composite represents a declaration with children 
	        case c:Composite => {
	          // get all needed parameters
	          val paramList = c.origDecl.getType match {
	            case Arrow(TSet(list), _) => list
	            case _ => throw new RuntimeException
	          }          
	          // for each its parameter add child to the queue for later traversing
	          for (parameter <- paramList) {
	            // create a tree for a parameter and associate it with the current composite
	            val paramTree = new Tree(c, parameter)
	            c.addChild(paramTree)
	            
	            // for each child node in associated InSynth nodes for the parameter
	            for(node <- c.associatedNode.params(parameter).nodes) {
	              // according to the type of node
	              // create a declaration and insert the pair into the queue
	              node match {
	                // for a simple node insert simple expressions 
	                case sn@InSynth.SimpleNode(decls, params) if params.isEmpty =>
	                  // for each of its declarations
	                  for (dec <- decls) {
	                    
	                    // NOTE we are not dealing with "Leaf expressions" anymore
//	                    if (dec.isAbstract)
//	                      pq += LeafExpression(paramTree, WeightForLeafs, sn)
//	                    else
	                    
	                    // new pair of simple expression and extended path
	                    val newPair = 
	                      (Simple(paramTree, fromInSynthDeclaration(dec), sn), visited + c)
                        // add new pair to the queue 
                    	pq += newPair
	                  }
	                // for a composite node insert composite expressions 
	                case sn@InSynth.SimpleNode(decls, _) =>
	                  for (dec <- decls) {
	                    // new pair of simple expression and extended path
	                    val newPair = 
	                      (Composite(paramTree, fromInSynthDeclaration(dec), sn), visited + c)
                        // add new pair to the queue 
	                    pq += newPair
	                  }
	              }
	            }
	          }
	        }
	        // Simple has no children 
	        case s:Simple =>
	          // mark it as done
	          s.associatedTree.childDone(s)
            // should not happen we do not deal with these anymore
	        case l:LeafExpression => throw new RuntimeException
//	        case l:LeafExpression => l.associatedTree.childDone(l)
	      }
      }
    }
      
    // return transformed pruned tree as a result
    rootDeclaration.toTreeNode 
  }
  
}