package ch.epfl.insynth.test.reconstructor

import java.{ util => ju, lang => jl }
import ch.epfl.insynth.reconstruction.intermediate.IntermediateTransformer
import ch.epfl.insynth.reconstruction.codegen.CodeGenerator
import ch.epfl.insynth.reconstruction.combinator.Combinator
import ch.epfl.insynth.env.SimpleNode
import ch.epfl.insynth.reconstruction.Reconstructor
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.junit.Test
import org.junit.BeforeClass
import org.junit.Assert._
import ch.epfl.insynth.core.Activator
import ch.epfl.insynth.core.preferences.InSynthConstants

@RunWith(value = classOf[Parameterized])
class ReconstructorTest(givenTree: SimpleNode, expected: List[String]) {
  
  @Test
  def test() {
    val reconstructorOutput = Reconstructor(givenTree)
    for (expectedString <- expected) {
      assertTrue(
        "Expected string (" + expectedString + ") could not be found, reconstructor output: "
      		+ ( reconstructorOutput map { _.getSnippet } mkString ( "," ) ),        
        (false /: reconstructorOutput) {
          (result, output) => {
            val matchExpected = expectedString
            output.getSnippet match {
              case `matchExpected` => true
              case _ => result
            }
          }
        }
      )
    }
  }

}

object ReconstructorTest {
  
  val numberOfCombinations = 15
  val maximumTime = 500
  
  @BeforeClass
  def setup() {
    println("Before class called")
    
    // set appropriate preference values
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.OfferedSnippetsPropertyString, 15)        
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.MaximumTimePropertyString, 500)
  }
  
	@Parameters
	def parameters: ju.Collection[Array[Object]] = {
	  import scala.collection.JavaConversions._
	  import TreeExample._
	  
		val trees = List(
//      (buildSimpleTree)
//      buildComplexTree,
//      buildTreeAbsApplication,
//      buildTreeArrowType,
//      buildTreeCycles,
//      buildTreeOverlapParameterTypeWithReturnType,
//      buildTreeSKombinator,
//      buildTreeWithCurryingFunctions,
//      buildTreeWithVariousFunctions,
//      buildTreeWithoutThis,
//      buildTreeIdentityFunction,
				Array(buildTreeWithConstructors, List("f(new package().intVal, new package )")),
        Array(buildSameInSynthDifferentWeight, List("f1(intVal)", "f2(intVal, intVal)"))
    )
	  
	  trees
	}

}