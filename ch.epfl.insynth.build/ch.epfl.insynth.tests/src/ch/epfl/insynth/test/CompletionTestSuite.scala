package ch.epfl.insynth.test

import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.BeforeClass
import ch.epfl.insynth.core.Activator
import ch.epfl.insynth.core.preferences.InSynthConstants
import ch.epfl.insynth.test.completion.InSynthCompletionTests
import ch.epfl.insynth.test.completion.InSynthCompletionClassicCodeTests
import ch.epfl.insynth.test.completion.FeaturesTests
import ch.epfl.insynth.test.completion.IssuesTests
import ch.epfl.insynth.test.completion.OtherIssuesTests

@RunWith(classOf[Suite])
@Suite.SuiteClasses(
  Array(
    classOf[InSynthCompletionTests],
    classOf[FeaturesTests],
    classOf[InSynthCompletionClassicCodeTests],
    classOf[IssuesTests],
    classOf[OtherIssuesTests]
  )
)
class CompletionTestSuite 

object CompletionTestSuite {
  @BeforeClass
  def setup() {    
    // set appropriate preference values
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.OfferedSnippetsPropertyString, 5)        
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.MaximumTimePropertyString, 500)     
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.CodeStyleParenthesesPropertyString, InSynthConstants.CodeStyleParenthesesClean)
  }
}
