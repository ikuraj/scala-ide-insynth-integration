package ch.epfl.test
import ch.epfl.insynth.reconstruction.IntermediateTransformer
import ch.epfl.insynth.reconstruction.CodeGenerator
import ch.epfl.insynth.combinator.Combinator
import ch.epfl.insynth.env.SimpleNode
import ch.epfl.insynth.env.FormatNode
import ch.epfl.insynth.reconstruction.Extractor

object ExtractorTest {
  
  val numberOfCombinations = 15
  
  implicit def toFormatNode(sn: SimpleNode) = FormatNode(sn)

  def main(args: Array[String]): Unit = {
    val trees =      
      Array(
//        TreeExample.buildSimpleTree, TreeExample.buildComplexTree,
//        TreeExample.buildTreeAbsApplication,
//        TreeExample.buildTreeArrowType,
//        TreeExample.buildTreeCycles, 
//        TreeExample.buildTreeOverlapParameterTypeWithReturnType,        
//        TreeExample.buildTreeSKombinator, 
//          TreeExample.buildTreeWithCurryingFunctions,
//        TreeExample.buildTreeWithVariousFunctions, TreeExample.buildTreeWithoutThis,
//        TreeExample.buildTreeIdentityFunction,
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
    println("with extractor")
    for ((tree, weight) <- Extractor(IntermediateTransformer(Combinator(
        givenTree, numberOfCombinations)), numberOfCombinations))
    {
	  for (output <- CodeGenerator(tree)) {
	    println("----------" + weight + "----------")
	    output.println
	  }
    }
    println("without extractor")
    for (output <- CodeGenerator(IntermediateTransformer(Combinator(
        givenTree, numberOfCombinations)))) {
	    output.println
	  }
  }

}