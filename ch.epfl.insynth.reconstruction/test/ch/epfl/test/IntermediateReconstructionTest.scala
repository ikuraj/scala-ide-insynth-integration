package ch.epfl.test
import ch.epfl.insynth.reconstruction.IntermediateTransformer
import ch.epfl.insynth.combinator.Combinator
import ch.epfl.insynth.env.Node
import ch.epfl.insynth.env.SimpleNode

object IntermediateReconstructionTest {

  def main(args: Array[String]): Unit = {
    val tests =      
      Array(
//        TreeExample.buildSimpleTree, TreeExample.buildComplexTree,
//        TreeExample.buildTreeAbsApplication, TreeExample.buildTreeArrowType,
//        /*TreeExample.buildTreeCycles, */TreeExample.buildTreeOverlapParameterTypeWithReturnType,
//        TreeExample.buildTreeSKombinator, TreeExample.buildTreeWithCurryingFunctions,
//        TreeExample.buildTreeWithVariousFunctions, TreeExample.buildTreeWithoutThis,
        TreeExample.buildTreeIdentityFunction
      )
    
    for (tree <- tests )
      parametrizedTreeTransform(tree) 
  }
  
  def parametrizedTreeTransform(node: SimpleNode) = {    
    node.println
    
    val prunedTree = Combinator(node)
    println("pruned tree")    
    prunedTree.println
    
    val transformedTrees = IntermediateTransformer(prunedTree)
    println("after intermediate transform")
    
    println("simple tree transformed") 
    assert(transformedTrees.size > 0)
    for (term <- transformedTrees) {
	  term.println
	  println(term)
    }    
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