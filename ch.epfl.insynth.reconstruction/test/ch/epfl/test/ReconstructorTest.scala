package ch.epfl.test
import ch.epfl.insynth.reconstruction.IntermediateTransformer
import ch.epfl.insynth.reconstruction.CodeGenerator
import ch.epfl.insynth.combinator.Combinator
import ch.epfl.insynth.env.SimpleNode
import ch.epfl.insynth.reconstruction.Reconstructor

object ReconstructorTest {

  def main(args: Array[String]): Unit = {
    val tests =      
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