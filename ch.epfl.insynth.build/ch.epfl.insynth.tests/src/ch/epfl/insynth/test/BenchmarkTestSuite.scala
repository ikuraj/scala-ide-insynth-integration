package ch.epfl.insynth.test

import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.BeforeClass

import ch.epfl.insynth.test.completion.InSynthBenchmarkCompletionTests
import ch.epfl.insynth.test.completion.InSynthBenchmarkCompletionParametrizedTests
import ch.epfl.insynth.test.completion.InSynthBenchmarkCompletionParametrizedLessCertainTests
import ch.epfl.insynth.core.Activator
import ch.epfl.insynth.core.preferences.InSynthConstants

@RunWith(classOf[Suite])
@Suite.SuiteClasses(
  Array(
    //classOf[InSynthBenchmarkCompletionTests],
    classOf[InSynthBenchmarkCompletionParametrizedTests]//,
    //classOf[InSynthBenchmarkCompletionParametrizedLessCertainTests]
  )
)
class BenchmarkTestSuite 

object BenchmarkTestSuite {
  @BeforeClass
  def setup() {    
    import InSynthConstants._
    
    // set appropriate preference values
		Activator.getDefault.getPreferenceStore.setValue(OfferedSnippetsPropertyString, 10)        
		Activator.getDefault.getPreferenceStore.setValue(MaximumTimePropertyString, 500)       
		Activator.getDefault.getPreferenceStore.setValue(CodeStyleParenthesesPropertyString, CodeStyleParenthesesClassic)
  }
}