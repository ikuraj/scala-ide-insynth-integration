package ch.epfl.insynth.test.reconstructor
import ch.epfl.insynth.reconstruction.intermediate.IntermediateTransformer
import ch.epfl.insynth.reconstruction.codegen.CodeGenerator
import ch.epfl.insynth.reconstruction.combinator.Combinator
import ch.epfl.insynth.env.SimpleNode
import ch.epfl.insynth.reconstruction.Reconstructor

object ReconstructorTest {

  def main(args: Array[String]): Unit = {
    val tests =      
      Array(
//        TreeExample.buildSimpleTree
//        , TreeExample.buildComplexTree,
//        TreeExample.buildTreeAbsApplication,
//        TreeExample.buildTreeArrowType,
//        TreeExample.buildTreeCycles, 
//        TreeExample.buildTreeOverlapParameterTypeWithReturnType,        
//        TreeExample.buildTreeSKombinator, 
//          TreeExample.buildTreeWithCurryingFunctions,
//        TreeExample.buildTreeWithVariousFunctions, TreeExample.buildTreeWithoutThis,
//        TreeExample.buildTreeIdentityFunction,
//        TreeExample.buildTreeWithConstructors
          TreeExample.buildSameInSynthDifferentWeight
      )
    
    for (tree <- tests )
      parametrizedTreeReconstruct(tree)      
  }
  
  def parametrizedTreeReconstruct(givenTree: SimpleNode) = {
    for (output <- Reconstructor(givenTree))
    {
	  println(output.snippet)
    }
  }

}