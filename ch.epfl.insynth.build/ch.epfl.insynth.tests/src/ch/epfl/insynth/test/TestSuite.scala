package ch.epfl.insynth.test

import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.BeforeClass
import ch.epfl.insynth.core.Activator
import ch.epfl.insynth.core.preferences.InSynthConstants
import ch.epfl.insynth.test.leon.LeonProjectSetup

@RunWith(classOf[Suite])
@Suite.SuiteClasses(
  Array(
    classOf[LeonProjectSetup]
  )
)
class TestSuite 

object TestSuite {
  @BeforeClass
  def setup() {    
    import InSynthConstants._
    
    // set appropriate preference values
		Activator.getDefault.getPreferenceStore.setValue(OfferedSnippetsPropertyString, 5)        
		Activator.getDefault.getPreferenceStore.setValue(MaximumTimePropertyString, 500)      
		Activator.getDefault.getPreferenceStore.setValue(DoSeparateLoggingPropertyString, true)
  }
}
