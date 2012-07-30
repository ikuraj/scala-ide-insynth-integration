package ch.epfl.insynth.reconstruction.combinator

import scala.collection.mutable.PriorityQueue
import ch.epfl.insynth.{ env => InSynth }
import ch.epfl.insynth.trees._
import java.util.logging.Logger
import java.util.logging.ConsoleHandler
import ch.epfl.insynth.env.FormatNode
import java.util.logging.Level
import ch.epfl.insynth.Config

/**
 * object which application transforms an InSynth representation input
 * to the pruned tree representation
 */
object Combinator extends ((InSynth.SimpleNode, Int, Int) => Node) {
  
  // general logger for the combinator step
  val logger = Rules.logger
  // logging actions inside the application
  val logApply = Rules.logApply//(logger.getName + ".apply")
  // logging interactions with the priority queue
  val logPQAdding = Config.logPQAdding
      
  /**
   * perform the transformation - apply with number of combinations needed, the resulting
   * pruned tree will encode at least this number of combinations
   * @param root root of the input proof tree
   * @param neededCombinations number of combinations needed
   * @param maximumTime maximal number of milliseconds the combinator should take
   * @return root node of the proof tree
   */
  def apply(root: InSynth.SimpleNode, neededCombinations: Int, maximumTime: Int): SimpleNode = {
    // logging
    if (Rules.isLogging) {
	    logApply.entering(getClass.getName, "apply")
	    logApply.finest("Entering combinator step (root: "+ FormatNode(root, Config.logCombinatorInputProofTreeLevel) + ", combinations: " + neededCombinations)
    }
    
    // get time at the beginning of the combination step
    val startTime = System.currentTimeMillis
        
    // import transformer from InSynth to intermediate declaration
    import DeclarationTransformer.fromInSynthDeclaration
    
    // reset so that pruning is not done
    Rules.doPruning = false
      
    // pair type that will be put in the priority queue
    // ( expression to be explored, a set of expressions visited on the path )
    type ExpressionPair = (Expression, Set[InSynth.Node])
    // priority queue
    var pq = new PriorityQueue[ExpressionPair]() (
      // ordering defined on the expressions  
      new Ordering[ExpressionPair] {                                                                    
	    def compare(a : ExpressionPair, b : ExpressionPair) = b._1.compare(a._1)                                           
      } 
    )    
    // declare root Tree which starts the hierarchy 
    val rootTree:Tree = new TopTree(neededCombinations)
    // a single declaration of the root Tree is the one corresponding to the root node
    val rootDeclaration = Composite(rootTree, fromInSynthDeclaration(root.getDecls.head), root)
        
    // add the root declaration and an empty set
    val startTuple = (rootDeclaration, Set[InSynth.Node]())
    pq += startTuple
    
    /* start the traversal */
    
    // while there is something in the queue
    while (!pq.isEmpty && (System.currentTimeMillis - startTime < maximumTime)) {
      
      // dequeue a pair
      val (currentDeclaration, visited) = pq.dequeue
              
      // logging
      if (Rules.isLogging) {
	      logApply.finer("Current declaration processed " + currentDeclaration)
	            
	      // if current declaration is pruned or already visited on this path, log
	      if (currentDeclaration.isPruned) {
	        logApply.fine("Declaration with " + FormatNode(currentDeclaration.getAssociatedNode, 0) + " pruned")
	      }
	      if (visited.contains(currentDeclaration.getAssociatedNode)) {
	        logApply.fine("Stumbled upon a cycle (discarding the node: " + FormatNode(currentDeclaration.getAssociatedNode, 0) + ")")
	      }
	      
	      // additional pruning conditions
	      if (currentDeclaration.getAssociatedTree.isPruned) {
	        logApply.fine("!Declaration " + currentDeclaration + " pruned")
	      }
	      if (Rules.doPruning && currentDeclaration.getAssociatedTree.checkIfPruned(currentDeclaration.getTraversalWeight)) {
	        logApply.fine("!!Declaration " + currentDeclaration + " pruned")
	      }
	      
	      if (Rules.doPruning)
	        logApply.fine("Pruning is on, pq.size=" + pq.size)
      }
        
      // if current declaration is pruned or already visited on this path, ignore it
      if (
          !visited.contains(currentDeclaration.getAssociatedNode) && !currentDeclaration.isPruned &&
          !currentDeclaration.getAssociatedTree.isPruned &&
          !(Rules.doPruning && currentDeclaration.getAssociatedTree.checkIfPruned(currentDeclaration.getTraversalWeight))
          ) {
      
    	  // add explored declaration to its associated tree as explored
	      currentDeclaration.getAssociatedTree addDeclaration(currentDeclaration)
	      
          // logging
	      if (Rules.isLogging)
          logApply.finer("Adding expression " + currentDeclaration.toString + " to its tree")
	      
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
	            for(node <- c.associatedNode.getParams(parameter).nodes) {
	              // according to the type of node
	              // create a declaration and insert the pair into the queue
	              
	              // if there are no parameters insert simple nodes
	              if (node.getParams.isEmpty){
	                  // for each of its declarations
	                  for (dec <- node.getDecls;
	                  // optimization - only consider those nodes that have less weight than
	                  // all trees up to the tree
	                  val notConsider = Rules.doPruning && paramTree.checkIfPruned(dec.getWeight.getValue
                		  + paramTree.getTraversalWeight);
            		  // logging
            		  val dummy =
            		    if (Config.isLogging && notConsider) {
            		      logPQAdding.info("Decl " + dec.getSimpleName + " is not considered")
            		    }
	                  if !notConsider
            		  ) // for
	                  {
	                    	                    
	                    // new pair of simple expression and extended path
	                    val newPair = 
	                      (
                            Simple(paramTree, fromInSynthDeclaration(dec), node),
                            visited + c.getAssociatedNode
                		  )
	                      
                        // logging
                		if (Rules.isLogging)
	                    logPQAdding.finer("Adding " + newPair._1 + " to the queue")
                        // add new pair to the queue 
	                    
                    	pq += newPair
	                  }	                
                  // if there are parameters insert composite nodes
	              } else {
	                  // for each of its declarations
	                  for (dec <- node.getDecls;
	                  // optimization - only consider those nodes that have less weight than
	                  // all trees up to the tree
	                  val notConsider = Rules.doPruning && paramTree.checkIfPruned(dec.getWeight.getValue
                		  + paramTree.getTraversalWeight);
            		  // logging
            		  val dummy =
            		    if (Config.isLogging && notConsider) {
            		      logPQAdding.info("Decl " + dec.getSimpleName + " is not considered")
            		    }
	                  if !(notConsider)
            		  ) // for 
	                  {
	                    // new pair of simple expression and extended path
	                    val newPair = 
	                      (
                            Composite(paramTree, fromInSynthDeclaration(dec), node), 
	                        visited + c.getAssociatedNode
                		  )
	                      	                      
                        // logging
                		if (Rules.isLogging)
	                    logPQAdding.finer("Adding " + newPair._1 + " to the queue")
	                    
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
        
    // logging
    if (Rules.isLogging) {
	    if (rootDeclaration.isPruned) {
	      logger.severe("Root declaration is pruned!")
	    }
	    if (!rootDeclaration.isDone) {
	      logger.severe("Root declaration is not done!")
	    }     
	//    logger.finest("End of apply, reconstruction structures are: " + FormatCombinations(rootDeclaration) )
	    logger.info("Number of combinations found: " + rootDeclaration.getNumberOfCombinations )
	    
	    logApply.exiting(getClass.getName, "apply")
    }
    
    // return transformed pruned tree as a result
    val result = rootDeclaration.toTreeNode
    
    // logging
    if (Rules.isLogging) {
    	logger.fine("Returning from apply with result: " + FormatPrNode(result) )
    }
    
    // return the root node of the pruned tree
    result
  }
  
}