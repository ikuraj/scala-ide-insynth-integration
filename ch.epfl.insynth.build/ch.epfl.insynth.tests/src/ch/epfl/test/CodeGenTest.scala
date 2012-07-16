package ch.epfl.test

import ch.epfl.insynth.reconstruction.intermediate.IntermediateTransformer
import ch.epfl.insynth.reconstruction.codegen.CodeGenerator
import ch.epfl.insynth.reconstruction.combinator.Combinator
import ch.epfl.insynth.reconstruction.codegen.Extractor
import ch.epfl.insynth.env.SimpleNode

import ch.epfl.insynth.env.FormatNode

object CodeGenTest {
  
  val numberOfCombinations = 15
  
  implicit def toFormatNode(sn: SimpleNode) = FormatNode(sn)

  def main(args: Array[String]): Unit = {
    val trees =      
      Array(
        TreeExample.buildSimpleTree, TreeExample.buildComplexTree,
        TreeExample.buildTreeAbsApplication,
        TreeExample.buildTreeArrowType,
        TreeExample.buildTreeCycles, 
        TreeExample.buildTreeOverlapParameterTypeWithReturnType,        
        TreeExample.buildTreeSKombinator, 
          TreeExample.buildTreeWithCurryingFunctions,
        TreeExample.buildTreeWithVariousFunctions, TreeExample.buildTreeWithoutThis,
        TreeExample.buildTreeIdentityFunction,
        TreeExample.buildTreeWithConstructors
      )
      
      
    for (tree <- trees )
      parametrizedTreeReconstruct(tree) 
    
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
    for ((tree, weight) <- Extractor(IntermediateTransformer(Combinator(
        givenTree, numberOfCombinations)), numberOfCombinations))
    {
	  for (output <- CodeGenerator(tree)) {
	    println("----------" + weight + "----------")
	    output.println
	  }
    }
  }

}