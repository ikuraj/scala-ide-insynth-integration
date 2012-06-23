package ch.epfl.test
import ch.epfl.insynth.reconstruction.IntermediateTransformer
import ch.epfl.insynth.reconstruction.CodeGenerator
import ch.epfl.insynth.combinator.Combinator
import ch.epfl.insynth.env.SimpleNode
import ch.epfl.insynth.env.FormatNode

object CodeGenTest {
  
  val numberOfCombinations = 15
  
  implicit def toFormatNode(sn: SimpleNode) = FormatNode(sn)

  def main(args: Array[String]): Unit = {
    parametrizedTreeReconstruct(TreeExample.buildTreeWithCurryingFunctions)
    simpleTreeReconstruct
    complexTreeReconstruct
    arrowTreeReconstruct
    overlapTreeReconstruct
    absApplicationTreeReconstruct
    sKombinatorTreeReconstruct
    
//    val S:(Int=>(Char=>String))=>(Int=>Char)=>Int=>String = 
//    {
//	 (var_1: ((Int) => ((Char) => String)))
//	 =>
//	 {
//	  {
//	   (var_2: (Int) => Char)
//	   =>
//	   { { (var_3: Int) => { var_1 (var_3) (var_2 (var_3)) } } }
//	  }
//	 }
//	}
    
    
    
    
  }
  
  def parametrizedTreeReconstruct(givenTree: SimpleNode) = {
    println("intial tree")
    givenTree.println
    println("after intermediate transform")
    for (tree <- IntermediateTransformer(Combinator(givenTree, numberOfCombinations)))
    {
	  for (output <- CodeGenerator(tree)) {
	    println("----------------")
	    output.println
	  }
    }
  }
  
  def simpleTreeReconstruct() = {
    val simpleTree = TreeExample.buildSimpleTree
    
    println("simple tree")    
    simpleTree.println
    
    val transformedTrees = IntermediateTransformer(Combinator(simpleTree, numberOfCombinations))
    println("after intermediate transform")
    
    println("simple tree transformed")    
    for (tree <- transformedTrees){
	  for (output <- CodeGenerator(tree)) {
	    output.println
	  }
    }
  }
  
  def complexTreeReconstruct() = {
    val complexTree = TreeExample.buildComplexTree
    
    println("complex tree")
    complexTree.println
    
    val transformedTrees = IntermediateTransformer(Combinator(complexTree, numberOfCombinations))
    println("after intermediate transform")
    
    for (tree <- transformedTrees){
	  for (output <- CodeGenerator(tree)) {
	    println("----------------")
	    output.println
	  }
    }
  }
  
  def arrowTreeReconstruct() = {
    val arrowTree = TreeExample.buildTreeArrowType
    
    println("arrow tree")
    val transformedTrees = IntermediateTransformer(Combinator(arrowTree, numberOfCombinations))
    println("after intermediate transform")
    
    for (tree <- transformedTrees){
	  for (output <- CodeGenerator(tree)) {
	    println("----------------")
	    output.println
	  }
    }
  }
  
  def overlapTreeReconstruct() = {
    println("overlap tree")
    TreeExample.buildTreeOverlapParameterTypeWithReturnType.println
    println("after intermediate transform")
    for (tree <- IntermediateTransformer(Combinator(TreeExample.buildTreeOverlapParameterTypeWithReturnType, numberOfCombinations)))
    {
	  for (output <- CodeGenerator(tree)) {
	    println("----------------")
	    output.println
	  }
    }
  }
  
  def absApplicationTreeReconstruct() = {
    println("abs application tree")
    TreeExample.buildTreeAbsApplication.println
    println("after intermediate transform")
    for (tree <- IntermediateTransformer(Combinator(TreeExample.buildTreeAbsApplication, numberOfCombinations)))
    {
	  for (output <- CodeGenerator(tree)) {
	    println("----------------")
	    output.println
	  }
    }
  }
  
  def sKombinatorTreeReconstruct() = {
    println("s kombinator tree")
    TreeExample.buildTreeSKombinator.println
    println("after intermediate transform")
    for (tree <- IntermediateTransformer(Combinator(TreeExample.buildTreeSKombinator, numberOfCombinations)))
    {
	  for (output <- CodeGenerator(tree)) {
	    println("----------------")
	    output.println
	  }
    }
  }

}