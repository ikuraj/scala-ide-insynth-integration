package ch.epfl.test
import ch.epfl.insynth.reconstruction.IntermediateTransformer
import ch.epfl.insynth.combinator.Combinator

object IntermediateReconstructionTest {

  def main(args: Array[String]): Unit = {
    simpleTreeTransform
    complexTreeTransform
    arrowTreeTransform
    overlapTreeTransform
    absApplicationTreeTransform
    sKombinatorTreeTransform
  }
  
  def simpleTreeTransform() = {
    val simpleTree = TreeExample.buildSimpleTree
    
    println("simple tree")    
    simpleTree.println
    
    val prunedTree = Combinator(simpleTree, 1)
    println("pruned tree")    
    prunedTree.println
    
    val transformedTrees = IntermediateTransformer(prunedTree)
    println("after intermediate transform")
    
    println("simple tree transformed")    
    for (term <- transformedTrees){
	  term.println
    }
  }
  
  def complexTreeTransform() = {
    val complexTree = TreeExample.buildComplexTree
    
    println("complex tree")
    complexTree.println
    
    val prunedTree = Combinator(complexTree, 100)
    println("pruned tree")    
    prunedTree.println
    
    val transformedTrees = IntermediateTransformer(prunedTree)
    println("after intermediate transform")
    
    for (tree <- transformedTrees)
      tree.println
  }
  
  def arrowTreeTransform() = {
    val arrowTree = TreeExample.buildTreeArrowType
    
    println("arrow tree")
    arrowTree.println
    
    val prunedTree = Combinator(arrowTree, 100)
    println("pruned tree")    
    prunedTree.println    
    
    val transformedTrees = IntermediateTransformer(prunedTree)
    println("after intermediate transform")
    
    for (tree <- transformedTrees)
      tree.println
  }
  
  def overlapTreeTransform() = {
    println("overlap tree")
    TreeExample.buildTreeOverlapParameterTypeWithReturnType.println
    println("after intermediate transform")
    for (tree <- IntermediateTransformer(Combinator(TreeExample.buildTreeOverlapParameterTypeWithReturnType)))
      tree.println
  }
  
  def absApplicationTreeTransform() = {
    println("abs application tree")
    TreeExample.buildTreeAbsApplication.println
    println("after intermediate transform")
    for (tree <- IntermediateTransformer(Combinator(TreeExample.buildTreeAbsApplication)))
      tree.println
  }
  
  
  def sKombinatorTreeTransform() = {
    println("s kombinator tree")
    TreeExample.buildTreeSKombinator.println
    println("after intermediate transform")
    for (tree <- IntermediateTransformer(Combinator(TreeExample.buildTreeSKombinator)))
      tree.println
  }

}