package ch.epfl.insynth.test.reconstructor

import java.{ util => ju, lang => jl }

import ch.epfl.insynth.reconstruction.intermediate.IntermediateTransformer
import ch.epfl.insynth.reconstruction.codegen.CodeGenerator
import ch.epfl.insynth.reconstruction.combinator.Combinator
import ch.epfl.insynth.reconstruction.codegen.Extractor
import ch.epfl.insynth.env.SimpleNode
import ch.epfl.insynth.env.FormatNode

import org.junit.Test
import org.junit.Assert._
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(value = classOf[Parameterized])
class CodeGenTest {
  
  val numberOfCombinations = 15
  val maximumTime = 500

  implicit def toFormatNode(sn: SimpleNode) = FormatNode(sn)

  def parametrizedTreeReconstruct(givenTree: SimpleNode) = {
    println("intial tree")
    givenTree.println
    println("after intermediate transform")

    Combinator(givenTree, numberOfCombinations, maximumTime) match {
      case Some(combinedTree) =>
        for ((tree, weight) <- Extractor(IntermediateTransformer(combinedTree), numberOfCombinations)) {
          for (output <- CodeGenerator(tree)) {
            println("----------" + weight + "----------")
            output.println
          }
        }
      case None => fail("Combinator could not find a solution for tree: " + givenTree)
    }

  }
}

object CodeGenTest {
  
	@Parameters
	def parameters: ju.Collection[SimpleNode] = {
	  import scala.collection.JavaConversions._
	  import TreeExample._
	  
		val trees = List(
      buildSimpleTree,
      buildComplexTree,
      buildTreeAbsApplication,
      buildTreeArrowType,
      buildTreeCycles,
      buildTreeOverlapParameterTypeWithReturnType,
      buildTreeSKombinator,
      buildTreeWithCurryingFunctions,
      buildTreeWithVariousFunctions,
      buildTreeWithoutThis,
      buildTreeIdentityFunction,
      buildTreeWithConstructors
    )
	  
	  trees
	}

}