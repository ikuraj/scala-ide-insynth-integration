package ch.epfl.insynth.reconstruction

import org.junit.Assert._
import org.junit.Test
import ch.epfl.insynth.core.Activator
import ch.epfl.insynth.core.preferences.InSynthConstants

import ch.epfl.insynth.reconstruction.codegen.CleanCodeGenerator

class FeaturesTest {
  
  @Test
  def testExample2() {
    val node = TreeExample.buildSameInSynthDifferentWeight
    
    // set appropriate preference values
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.OfferedSnippetsPropertyString, 15)        
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.MaximumTimePropertyString, 500)
  
		val reconstructorOutput = Reconstructor(node, new CleanCodeGenerator)
		
		// query node has 1.0d weight
		for ((expectedString, expectedWeight) <- List(("f1(intVal)", 3.0d), ("f2(intVal, intVal)", 4.0d))) {
      assertTrue(
        (false /: reconstructorOutput) {
          (result, output) => {
            val matchExpected = expectedString
            output.getSnippet match {
              case `matchExpected` =>
                assertEquals(expectedWeight, output.getWieght, 0d)
                true
              case _ => result
            }
          }
        }  
      )
    }
    
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.OfferedSnippetsPropertyString, 1)
		
		val reconstructorOutput1Solution = Reconstructor(node, new CleanCodeGenerator)
		assertTrue(!reconstructorOutput1Solution.isEmpty)
		assertEquals("f1(intVal)", reconstructorOutput1Solution.head.getSnippet)
		
  }

}