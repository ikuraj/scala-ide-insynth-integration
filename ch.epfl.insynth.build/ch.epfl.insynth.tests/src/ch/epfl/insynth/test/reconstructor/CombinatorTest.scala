package ch.epfl.insynth.test.reconstructor

import ch.epfl.insynth.reconstruction.combinator.Combinator

import ch.epfl.insynth.reconstruction.combinator.FormatPrNode
import ch.epfl.insynth.env

import ch.epfl.insynth.reconstruction.combinator._
import ch.epfl.insynth.reconstruction.combinator.Combinator._
import env.{SimpleNode => EnvSimpleNode}

import org.junit.Assert._
import org.junit.Test

class CombinatorTest {

  val numberOfCombinations = 15
  val maximumTime = 500

//  implicit def toFormatNode(sn: SimpleNode) = FormatNode(sn)
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
    val tree1 = TestTrees.buildCombinedSimpleTree
    val tree2 = TestTrees.buildCombinedSimpleTree
    val tree3 = TestTrees.buildCombinedComplexTree
    

    assertTrue(equals(tree1, tree1))
    assertTrue(equals(tree2, tree2))
    assertTrue(equals(tree3, tree3))

    assertTrue(equals(tree1, tree2))
    assertTrue(equals(tree2, tree1))
    
    println(tree1.getType); 

    assertFalse(equals(tree1, tree3))
    assertFalse(equals(tree3, tree2))
  }

  //Determine if two SimpleNodes are equal
  def equals(s1: Node, s2: Node): Boolean = {
    println("Checking if two nodes are equal"); 
    (s1, s2) match {
      case (SimpleNode(decls1, tp1, params1), SimpleNode(decls2, tp2, params2)) =>
        println("Comparing two simple nodes."); 

        if (tp1 != tp2) { 
          println("Types did not match"); 
          println(tp1); 
          println(tp2); 
          return false
        }

        // Compare Decls
        val declsSame = 
          (decls1 zip decls2).foldLeft(true) {
            case (res, (d1, d2)) =>
              res && d1 == d2
          }
        if (!declsSame) return false
        
        println("Params are equal, checking other stuff"); 
        for ((key, n1) <- params1) {
          // note that map might not contain key
          if (!params2.contains(key)) return false
          equalsForContainerNodes(n1, params2(key))
        }
        
        return true
        
      case (_, _) =>
        println("Unimplemented equals case"); 
//       TODO
        return false
    }

  }
  
  def equalsForContainerNodes(cn1: ContainerNode, cn2: ContainerNode): Boolean = { 
    println(cn1)
    println(cn2)
    return false
    
  }
  
  //Determine if two Nodes are equal
//  def equalsNotSure(s1: EnvSimpleNode, s2: EnvSimpleNode): Boolean = {
//    println("===Equals?===")
//    println(s1)
//    println(s2)
//
//    s1 match {
//      case _: EnvSimpleNode => // s1 is just Node
//        s2 match {
//          case _: EnvSimpleNode => //s2 also just Node, compare types
//            //TODO
//            return true
//          case _: SimpleNode => //Comparing Node to SimpleNode -> false
//            return false
//        }
//
//      case s1: SimpleNode => // s1 is SimpleNode
//        s2 match {
//          case _: EnvSimpleNode => //Comparing Node to SimpleNode -> false
//            return false
//          case s2: SimpleNode =>
//            return simpleEquals(s1, s2)
//        }
//
//    }
//
//  }

  @Test
  def testSimpleTree() {
    
    val simpleTree = TreeExample.buildSimpleTree
    val combinedSimpleTreeTest = TestTrees.buildCombinedSimpleTree
    val combinedSimpleTree = parametrizedCombine(simpleTree)
    
    assertTrue(equals(combinedSimpleTree, combinedSimpleTreeTest))

    main(Array.empty)
  }
  
  @Test
  def testComplexTree() {
    
    val complexTree = TreeExample.buildComplexTree
    val combinedComplexTreeTest = TestTrees.buildCombinedComplexTree

    val combinedComplexTree = parametrizedCombine(complexTree)

    assertTrue(equals(combinedComplexTree, combinedComplexTreeTest));

    main(Array.empty)
  }

  @Test
  def testTreeArrowType() {
    val tree = TreeExample.buildTreeArrowType
    val combinedTreeTest = TestTrees.buildCombinedTreeArrowType

    val combinedTree = parametrizedCombine(tree)

    assertTrue(equals(combinedTree, combinedTreeTest));

    main(Array.empty)
  }
  
  
  def parametrizedCombine(sn: EnvSimpleNode) = {
    println("Parametrized COmbine");
    println("=====original tree=====")
//    FormatNode(sn).println
    println("=====combined tree=====")
    println(Combinator(sn, numberOfCombinations, maximumTime)) 
    assertTrue(true);
    println("END"); 
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
//    TreeExample.buildSimpleTree.println
    println("combined simple tree")
    //Combinator(TreeExample.buildSimpleTree, 2, maximumTime).println
  }

  def complexTreeCombine() = {
    println("complex tree")
//    TreeExample.buildComplexTree.println
    println("combined complex tree")
    //Combinator(TreeExample.buildComplexTree, 2, maximumTime).println
  }

  def arrowTreeCombine() = {
    println("arrow tree")
//    TreeExample.buildTreeArrowType.println
    println("combined arrow tree")
    //Combinator(TreeExample.buildTreeArrowType, 6, maximumTime).println
  }

  def overlapTreeCombine() = {
    println("overlap tree")
//    TreeExample.buildTreeOverlapParameterTypeWithReturnType.println
    println("combined overlap tree")
    //Combinator(TreeExample.buildTreeOverlapParameterTypeWithReturnType, 6, maximumTime).println
  }

  def sKombinatorTreeReconstruct() = {
    println("s combinator tree")
//    TreeExample.buildTreeSKombinator.println
    println("combined tree")
    //Combinator(TreeExample.buildTreeSKombinator, 6, maximumTime).println
  }

}