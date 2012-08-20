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
import ch.epfl.insynth.core.completion.InnerFinder

@RunWith(value = classOf[Parameterized])
class ReconstructorTest(givenTree: SimpleNode, expectedList: List[String]) {
  
  @Test
  def test() {
    // get the reconstruction result
    val reconstructorOutput = Reconstructor(givenTree)
    
    assertEquals("Number of completions did not match. ", expectedList.size, reconstructorOutput.size)
    
    // for each expected string
    for (expected <- expectedList) {
      // assert true with an error message containing reconstructed snippets
      assertTrue(
        "Expected string (" + expected + ") could not be found, reconstructor output:\n"
      		+ ( reconstructorOutput map { _.getSnippet } mkString ( "\n" ) ), 
    		// go though all reconstruction outputs and try to match with expected string
        (false /: reconstructorOutput) {
          (result, output) => {
            val matchExpected = expected
            if (!result)
              // match string snippet
	            output.getSnippet match {
              	// in case of string
	              case `matchExpected` => true
	              // in case of string representing a regex object
	              case outputString if outputString matches expected => true
	              case _ => result
	            }
            else result
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
    // set appropriate preference values
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.OfferedSnippetsPropertyString, 50)        
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.MaximumTimePropertyString, 1000)
		
		// wait for builder thread to finish (we want to start next tests without interfering)
		if (InnerFinder.predefBuildLoader.isAlive())
			InnerFinder.predefBuildLoader.join();
  }
  
	@Parameters
	def parameters: ju.Collection[Array[ScalaObject]] = {
	  import scala.collection.JavaConversions._
	  import TreeExample._
	  
		val trees = List(
      Array(buildSimpleTree, List("A.m4()")),
      Array(buildComplexTree, List("m1\\((\\S+) => m6\\(\\), m4\\(\\)\\)", "m1\\((\\S+) => m2\\(\\1\\), m4\\(\\)\\)",
        "m1\\((\\S+) => m3\\(m5\\(\\1\\)\\), m4\\(\\)\\)")),
      // NOTE this one can pass without specifying types
      // e.g. val testVal: (Int=>Char, Int) => Char = (x, y) => x(y)   (OK)
      Array(buildTreeAbsApplication, List("\\((\\S+), (\\S+)\\) => \\1\\(\\2\\)")),
      Array(buildTreeArrowType, 
        List(
          "\\((\\S+), (\\S+)\\) => A.m3\\(\\)",
          "\\((\\S+), (\\S+)\\) => outside\\(\\1, \\1\\)",
          "\\((\\S+), (\\S+)\\) => outside\\(\\1, \\2\\)",
          "\\((\\S+), (\\S+)\\) => outside\\(\\2, \\1\\)",
          "\\((\\S+), (\\S+)\\) => outside\\(\\2, \\2\\)",
          "\\((\\S+), (\\S+)\\) => outside\\(intVal, intVal\\)",
          "\\((\\S+), (\\S+)\\) => outside\\(intVal, \\1\\)",
          "\\((\\S+), (\\S+)\\) => outside\\(\\1, intVal\\)",
          "\\((\\S+), (\\S+)\\) => outside\\(intVal, \\2\\)",
          "\\((\\S+), (\\S+)\\) => outside\\(\\2, intVal\\)",
          "\\((\\S+), (\\S+)\\) => A.m1\\(\\)\\(\\1, \\1\\)",
          "\\((\\S+), (\\S+)\\) => A.m1\\(\\)\\(\\1, \\2\\)",
          "\\((\\S+), (\\S+)\\) => A.m1\\(\\)\\(\\2, \\1\\)",
          "\\((\\S+), (\\S+)\\) => A.m1\\(\\)\\(\\2, \\2\\)",
          "\\((\\S+), (\\S+)\\) => A.m1\\(\\)\\(intVal, intVal\\)",
          "\\((\\S+), (\\S+)\\) => A.m1\\(\\)\\(intVal, \\1\\)",
          "\\((\\S+), (\\S+)\\) => A.m1\\(\\)\\(\\1, intVal\\)",
          "\\((\\S+), (\\S+)\\) => A.m1\\(\\)\\(intVal, \\2\\)",
          "\\((\\S+), (\\S+)\\) => A.m1\\(\\)\\(\\2, intVal\\)",
          "\\((\\S+), (\\S+)\\) => A.m2\\(intVal, intVal\\)",
          "\\((\\S+), (\\S+)\\) => A.m2\\(intVal, intVal\\)",
          "\\((\\S+), (\\S+)\\) => A.m2\\(intVal, intVal\\)",
          "\\((\\S+), (\\S+)\\) => A.m2\\(intVal, intVal\\)",
          "\\((\\S+), (\\S+)\\) => A.m2\\(intVal, intVal\\)",
          "\\((\\S+), (\\S+)\\) => A.m2\\(intVal, intVal\\)",
          "\\((\\S+), (\\S+)\\) => A.m2\\(intVal, intVal\\)",
          "\\((\\S+), (\\S+)\\) => A.m2\\(intVal, intVal\\)",
          "\\((\\S+), (\\S+)\\) => A.m2\\(intVal, intVal\\)"
        )),
//      Array(buildTreeCycles, List()),
//      Array(buildTreeOverlapParameterTypeWithReturnType, List()),
//      Array(buildTreeSKombinator, List()),
//      Array(buildTreeWithCurryingFunctions, List()),
//      Array(buildTreeWithVariousFunctions, List()),
//      Array(buildTreeWithoutThis, List()),
//			Array(buildTreeIdentityFunction, List()),
			Array(buildTreeWithConstructors, List("f(new package().intVal, new package )")),
      Array(buildSameInSynthDifferentWeight, List("f1(intVal)", "f2(intVal, intVal)"))
    )
	  
	  trees
	}

}