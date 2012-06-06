package ch.epfl.test
import ch.epfl.insynth.reconstruction.IntermediateTransformer
import ch.epfl.insynth.reconstruction.Reconstructor
import ch.epfl.insynth.combinator.Combinator

object ReconstructorTest {

  def main(args: Array[String]): Unit = {
    simpleTreeReconstruct
    complexTreeReconstruct
    arrowTreeReconstruct
    overlapTreeReconstruct
    absApplicationTreeReconstruct
    sKombinatorTreeReconstruct
    
//    val S:(Int=>(Char=>String))=>(Int=>Char)=>Int=>String = ({
//	 (var_1: ((Int) => ((Char) => String)))
//	 =>
//	 {
//	  {
//	   (var_2: (Int) => Char)
//	   =>
//	   { { (var_3: Int) => { (var_1:((Int) => (Char) => String)) (var_3: Int) ((var_2: ((Int) => Char)) (var_3: Int)) } } }
//	  }
//	 }
//	})
  }
  
  def simpleTreeReconstruct() = {
    val simpleTree = TreeExample.buildSimpleTree
    
    println("simple tree")    
    simpleTree.println
    
    val transformedTrees = IntermediateTransformer(Combinator(simpleTree))
    println("after intermediate transform")
    
    println("simple tree transformed")    
    for (tree <- transformedTrees){
	  for (output <- Reconstructor(tree)) {
	    output.println
	  }
    }
  }
  
  def complexTreeReconstruct() = {
    val complexTree = TreeExample.buildComplexTree
    
    println("complex tree")
    complexTree.println
    
    val transformedTrees = IntermediateTransformer(Combinator(complexTree))
    println("after intermediate transform")
    
    for (tree <- transformedTrees){
	  for (output <- Reconstructor(tree)) {
	    println("----------------")
	    output.println
	  }
    }
  }
  
  def arrowTreeReconstruct() = {
    val arrowTree = TreeExample.buildTreeArrowType
    
    println("arrow tree")
    val transformedTrees = IntermediateTransformer(Combinator(arrowTree))
    println("after intermediate transform")
    
    for (tree <- transformedTrees){
	  for (output <- Reconstructor(tree)) {
	    println("----------------")
	    output.println
	  }
    }
  }
  
  def overlapTreeReconstruct() = {
    println("overlap tree")
    TreeExample.buildTreeOverlapParameterTypeWithReturnType.println
    println("after intermediate transform")
    for (tree <- IntermediateTransformer(Combinator(TreeExample.buildTreeOverlapParameterTypeWithReturnType)))
    {
	  for (output <- Reconstructor(tree)) {
	    println("----------------")
	    output.println
	  }
    }
  }
  
  def absApplicationTreeReconstruct() = {
    println("abs application tree")
    TreeExample.buildTreeAbsApplication.println
    println("after intermediate transform")
    for (tree <- IntermediateTransformer(Combinator(TreeExample.buildTreeAbsApplication)))
    {
	  for (output <- Reconstructor(tree)) {
	    println("----------------")
	    output.println
	  }
    }
  }
  
  def sKombinatorTreeReconstruct() = {
    println("s kombinator tree")
    TreeExample.buildTreeSKombinator.println
    println("after intermediate transform")
    for (tree <- IntermediateTransformer(Combinator(TreeExample.buildTreeSKombinator)))
    {
	  for (output <- Reconstructor(tree)) {
	    println("----------------")
	    output.println
	  }
    }
  }

}