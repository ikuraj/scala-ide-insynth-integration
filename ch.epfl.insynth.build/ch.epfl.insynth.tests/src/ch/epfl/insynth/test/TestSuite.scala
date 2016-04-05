package ch.epfl.insynth.test

import org.junit.runner.RunWith
import org.junit.runners.Suite
import ch.epfl.insynth.test.completion.InSynthPreferencesTests
import org.junit.BeforeClass
import ch.epfl.insynth.core.Activator
import ch.epfl.insynth.core.preferences.InSynthConstants

import ch.epfl.insynth.Config


@RunWith(classOf[Suite])
@Suite.SuiteClasses(
  Array(
    classOf[InSynthLibraryTestSuite],
    classOf[CodeGenerationTestSuite],
    classOf[CompletionTestSuite],
    classOf[BenchmarkTestSuite],
    classOf[InSynthPreferencesTests]
  )
)
class TestSuite 

object TestSuite {
  @BeforeClass
  def setup() {    
    import InSynthConstants._
    
    // set appropriate preference values
		Activator.getDefault.getPreferenceStore.setValue(OfferedSnippetsPropertyString, 15)        
		Activator.getDefault.getPreferenceStore.setValue(MaximumTimePropertyString, 500)      
		Activator.getDefault.getPreferenceStore.setValue(DoSeparateLoggingPropertyString, true)
		
		val handler = new java.util.logging.FileHandler("tempOutFile.txt", false)
		handler.setFormatter(new java.util.logging.SimpleFormatter)	
		Config.inSynthLogger.setLevel(java.util.logging.Level.FINEST)
		Config.inSynthLogger.addHandler(handler)
  }
}
