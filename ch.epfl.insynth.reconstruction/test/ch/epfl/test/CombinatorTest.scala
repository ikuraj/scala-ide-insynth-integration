package ch.epfl.test
import ch.epfl.insynth.combinator.Combinator
import ch.epfl.insynth.env.SimpleNode

object CombinatorTest {

  def main(args: Array[String]): Unit = {
    simpleTreeCombine
    complexTreeCombine
    arrowTreeCombine
    overlapTreeCombine
    sKombinatorTreeReconstruct
    //cycleTreeCombine
  }
  
  // XXX cannot still be instantiated according to the proof representation!
  def cycleTreeCombine = {
    println("combined cycle tree")
    Combinator(TreeExample.buildTreeCycles).println
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