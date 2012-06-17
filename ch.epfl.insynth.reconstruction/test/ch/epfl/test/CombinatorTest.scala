package ch.epfl.test
import ch.epfl.insynth.combinator.Combinator
import ch.epfl.insynth.combinator.FormatPrNode
import ch.epfl.insynth.env.SimpleNode
import ch.epfl.insynth.env.FormatNode

object CombinatorTest {
  
  implicit def toFormatNode(sn: SimpleNode) = FormatNode(sn)
  implicit def toPrFormatNode(sn: ch.epfl.insynth.combinator.Node) = FormatPrNode(sn)

  def main(args: Array[String]): Unit = {
//    val tests =      
//      Array(
//        TreeExample.buildSimpleTree, TreeExample.buildComplexTree,
//        TreeExample.buildTreeAbsApplication, TreeExample.buildTreeArrowType,
//          TreeExample.buildTreeOverlapParameterTypeWithReturnType,
//        TreeExample.buildTreeSKombinator, TreeExample.buildTreeWithCurryingFunctions,
//        TreeExample.buildTreeWithVariousFunctions, TreeExample.buildTreeWithoutThis,
//        TreeExample.buildTreeIdentityFunction
//      )
//    
//    for (tree <- tests )
//      parametrizedCombine(tree) 
          cycleTreeCombine
  }
  
  def parametrizedCombine(sn: SimpleNode) = {
    println("original tree")
    FormatNode(sn).println
    println("combined tree")
    FormatPrNode(Combinator(sn)).println
  } 
  
  // XXX cannot still be instantiated according to the proof representation!
  def cycleTreeCombine = {
    println("combined cycle tree")
    val cycleTree = TreeExample.buildTreeCycles
    Combinator(cycleTree).println
  } 
  
  def simpleTreeCombine() = {
    println("simple tree")
    TreeExample.buildSimpleTree.println
    println("combined simple tree")
    Combinator(TreeExample.buildSimpleTree, 2).println
  }
  
  def complexTreeCombine() = {
    println("complex tree")
    TreeExample.buildComplexTree.println
    println("combined complex tree")
    Combinator(TreeExample.buildComplexTree, 2).println
  }
  
  def arrowTreeCombine() = {
    println("arrow tree")
    TreeExample.buildTreeArrowType.println
    println("combined arrow tree")
    Combinator(TreeExample.buildTreeArrowType, 6).println
  }
  
  def overlapTreeCombine() = {
    println("overlap tree")
    TreeExample.buildTreeOverlapParameterTypeWithReturnType.println
    println("combined overlap tree")
    Combinator(TreeExample.buildTreeOverlapParameterTypeWithReturnType, 6).println
  }
  
  
  def sKombinatorTreeReconstruct() = {
    println("s combinator tree")
    TreeExample.buildTreeSKombinator.println
    println("combined tree")
    Combinator(TreeExample.buildTreeSKombinator, 6).println
  }

}