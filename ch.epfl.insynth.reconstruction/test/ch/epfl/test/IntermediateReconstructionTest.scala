package ch.epfl.test
import ch.epfl.insynth.reconstruction.IntermediateTransformer

object IntermediateReconstructionTest {

  def main(args: Array[String]): Unit = {
    sKombinatorTreeTransform
  }
  
  def simpleTreeTransform() = {
    val simpleTree = TreeExample.buildSimpleTree
    
    println("simple tree")    
    simpleTree.println
    
    val transformedTrees = IntermediateTransformer(simpleTree)
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
    
    val transformedTrees = IntermediateTransformer(complexTree)
    println("after intermediate transform")
    
    for (tree <- transformedTrees)
      tree.println
  }
  
  def arrowTreeTransform() = {
    val arrowTree = TreeExample.buildTreeArrowType
    
    println("arrow tree")
    val transformedTrees = IntermediateTransformer(arrowTree)
    println("after intermediate transform")
    
    for (tree <- transformedTrees)
      tree.println
  }
  
  def overlapTreeTransform() = {
    println("overlap tree")
    TreeExample.buildTreeOverlapParameterTypeWithReturnType.println
    println("after intermediate transform")
    for (tree <- IntermediateTransformer(TreeExample.buildTreeOverlapParameterTypeWithReturnType))
      tree.println
  }
  
  def absApplicationTreeTransform() = {
    println("abs application tree")
    TreeExample.buildTreeAbsApplication.println
    println("after intermediate transform")
    for (tree <- IntermediateTransformer(TreeExample.buildTreeAbsApplication))
      tree.println
  }
  
  
  def sKombinatorTreeTransform() = {
    println("s kombinator tree")
    TreeExample.buildTreeSKombinator.println
    println("after intermediate transform")
    for (tree <- IntermediateTransformer(TreeExample.buildTreeSKombinator))
      tree.println
  }

}