package ch.epfl.insynth.test.reconstructor

import ch.epfl.insynth.reconstruction.combinator.Combinator


import ch.epfl.insynth.reconstruction.combinator.FormatPrNode
import ch.epfl.insynth.env.SimpleNode
import ch.epfl.insynth.env.FormatNode

import ch.epfl.insynth.reconstruction.combinator.{Node => EnvSimpleNode}
//import env.{SimpleNode => EnvSimpleNode}

import org.junit.Assert._
import org.junit.Test

class CombinatorTest {

  val numberOfCombinations = 15
  val maximumTime = 500

  implicit def toFormatNode(sn: SimpleNode) = FormatNode(sn)
  implicit def toPrFormatNode(sn: ch.epfl.insynth.reconstruction.combinator.Node) = FormatPrNode(sn)

  def main(args: Array[String]): Unit = {
    val tests =
      Array(
        TreeExample.buildSimpleTree, TreeExample.buildComplexTree,
        TreeExample.buildTreeAbsApplication, TreeExample.buildTreeArrowType,
        TreeExample.buildTreeOverlapParameterTypeWithReturnType,
        TreeExample.buildTreeSKombinator, TreeExample.buildTreeWithCurryingFunctions,
        TreeExample.buildTreeWithVariousFunctions, TreeExample.buildTreeWithoutThis,
        TreeExample.buildTreeIdentityFunction)

    //    for (tree <- tests )
    //      parametrizedCombine(tree) 
    ////          cycleTreeCombine

  }

  @Test 
  def testEquals() {
    // tree1 = tree2 != tree3
    val tree1 = parametrizedCombine(TreeExample.buildSimpleTree)
    val tree2 = parametrizedCombine(TreeExample.buildSimpleTree)
    val tree3 = parametrizedCombine(TreeExample.buildComplexTree)
    
    assertTrue(equals(tree1, tree1))
    assertTrue(equals(tree2,tree2))
    assertTrue(equals(tree3, tree3))
    
	assertTrue(equals(tree1, tree2))
	assertTrue(equals(tree2, tree1))
	
	assertFalse(equals(tree1, tree3))
	assertFalse(equals(tree3, tree2))
  }
  
//Determine if two SimpleNodes are equal
 def simpleEquals(s1: SimpleNode, s2: SimpleNode): Boolean = { 
    val s1Decls = s1.getDecls
    val s2Decls = s2.getDecls
    //TODO: Compare Decls
    if (s1Decls.size != s2Decls.size) return false
    println(s1Decls)
    println(s2Decls)
    var dEqual = false
    for (d1 <- s1Decls){ 
        dEqual = false
    	for (d2 <- s2Decls) { 
    	  println("Anbout to call equals")
    	  if (d2==d1) dEqual = true
//    	  if (d2.equals(d1)) dEqual = true
    	}
        if (!dEqual) return false  
    }
    
    val s1Params = s1.getParams
    val s2Params = s2.getParams
    
    //Check that params are the same 
    //Check that set of keys are equal
    if (s1Params.keys == s2Params.keys) {     	
    	//Compare children equality
    	for ((k,v1) <- s1Params) { 
    	  val v2 = s2Params(k)
    	  val s1Nodes = v1.getNodes
    	  val s2Nodes = v2.getNodes    	  
    	  for ((n1,n2) <- (s1Nodes zip s2Nodes)){ 
    		 if (!equals(n1,n2)) return false  
    	  }
    	}
    	return true
    }
    false
 } 
  //Determine if two Nodes are equal
  def equals(s1: EnvSimpleNode, s2: EnvSimpleNode): Boolean = {
    println("===Equals?===")
    println(s1)
    println(s2)
    
    s1 match { 
      case _: EnvSimpleNode => // s1 is just Node
        s2 match { 
          case _: EnvSimpleNode => //s2 also just Node, compare types
            //TODO
            return true
          case _: SimpleNode => //Comparing Node to SimpleNode -> false
            return false
        }
        
       
      case s1: SimpleNode =>// s1 is SimpleNode
        s2 match { 
          case _: EnvSimpleNode => //Comparing Node to SimpleNode -> false
            return false
          case s2: SimpleNode => 
            return simpleEquals(s1, s2)
        }
        
    }
    

  }

  @Test
  def test1() {
    val simpleTree = TreeExample.buildSimpleTree
    val combinedSimpleTree = parametrizedCombine(simpleTree)
    
    val combinedSimpleTreeTest = TestTrees.buildCombinedSimpleTree
    println("========================"); 
    FormatNode(combinedSimpleTreeTest).println;

    println("Decals:")
    println(combinedSimpleTreeTest.getDecls)
    println("Params:")
    println(combinedSimpleTreeTest.getParams)
    println("=====Current ^ ===== Solution V =====")
    FormatPrNode(combinedSimpleTree).println
    println("Decals:")
    println(combinedSimpleTree.getDecls)
    println("Params:")
    println(combinedSimpleTree.getParams)
    println(TestTrees.buildCombinedSimpleTree.equals(combinedSimpleTree));
    println(equals(combinedSimpleTree, combinedSimpleTreeTest));
    println("========================"); 

    main(Array.empty)
  }

  def parametrizedCombine(sn: SimpleNode) = {
    println("Parametrized COmbine");
    println("=====original tree=====")
    FormatNode(sn).println
    println("=====combined tree=====")
    FormatPrNode(Combinator(sn, numberOfCombinations, maximumTime).get).println
    assertTrue(true)
    println("END")
    Combinator(sn, numberOfCombinations, maximumTime).get;
  }

  // XXX cannot still be instantiated according to the proof representation!
  def cycleTreeCombine = {
    println("combined cycle tree")
    val cycleTree = TreeExample.buildTreeCycles
    //Combinator(cycleTree, numberOfCombinations, maximumTime).println
  }

  def simpleTreeCombine() = {
    println("simple tree")
    TreeExample.buildSimpleTree.println
    println("combined simple tree")
    //Combinator(TreeExample.buildSimpleTree, 2, maximumTime).println
  }

  def complexTreeCombine() = {
    println("complex tree")
    TreeExample.buildComplexTree.println
    println("combined complex tree")
    //Combinator(TreeExample.buildComplexTree, 2, maximumTime).println
  }

  def arrowTreeCombine() = {
    println("arrow tree")
    TreeExample.buildTreeArrowType.println
    println("combined arrow tree")
    //Combinator(TreeExample.buildTreeArrowType, 6, maximumTime).println
  }

  def overlapTreeCombine() = {
    println("overlap tree")
    TreeExample.buildTreeOverlapParameterTypeWithReturnType.println
    println("combined overlap tree")
    //Combinator(TreeExample.buildTreeOverlapParameterTypeWithReturnType, 6, maximumTime).println
  }

  def sKombinatorTreeReconstruct() = {
    println("s combinator tree")
    TreeExample.buildTreeSKombinator.println
    println("combined tree")
    //Combinator(TreeExample.buildTreeSKombinator, 6, maximumTime).println
  }

}