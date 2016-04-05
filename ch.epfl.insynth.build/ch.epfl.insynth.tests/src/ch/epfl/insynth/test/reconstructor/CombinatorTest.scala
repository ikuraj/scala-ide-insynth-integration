package ch.epfl.insynth.test.reconstructor

import ch.epfl.insynth.reconstruction.combinator.Combinator
import ch.epfl.insynth.reconstruction.combinator.FormatPrNode
import ch.epfl.insynth.env.SimpleNode
import ch.epfl.insynth.env.FormatNode

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
        TreeExample.buildTreeIdentityFunction
      )
    
    for (tree <- tests )
      parametrizedCombine(tree) 
          cycleTreeCombine
  }

  @Test
  def test1() {
    main(Array.empty)    
  }
  
  def parametrizedCombine(sn: SimpleNode) = {
    println("original tree")
    FormatNode(sn).println
    println("combined tree")
    FormatPrNode(Combinator(sn, numberOfCombinations, maximumTime).get).println
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