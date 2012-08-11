package ch.epfl.insynth.test.completion

import scala.tools.eclipse.testsetup.SDTTestUtils
import scala.tools.eclipse.javaelements.ScalaCompilationUnit
import scala.tools.nsc.interactive.Response
import scala.tools.eclipse.ScalaWordFinder
import scala.tools.nsc.util.SourceFile
import scala.tools.eclipse.ScalaPresentationCompiler
import org.eclipse.jface.text.contentassist.ICompletionProposal
import org.junit.Assert._
import org.junit.Test
import scala.tools.eclipse.testsetup.TestProjectSetup
import org.eclipse.jdt.core.search.{ SearchEngine, IJavaSearchConstants, IJavaSearchScope, SearchPattern, TypeNameRequestor }
import org.eclipse.jdt.core.IJavaElement
import org.junit.Ignore
import scala.tools.nsc.util.OffsetPosition
import scala.tools.eclipse.completion.ScalaCompletions
import scala.tools.eclipse.completion.CompletionProposal
import ch.epfl.insynth.core.completion.InsynthCompletionProposalComputer
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext
import org.eclipse.jdt.core.ICompilationUnit
import org.eclipse.core.runtime.NullProgressMonitor
import ch.epfl.insynth.core.completion.InnerFinder
import scala.collection.JavaConversions
import scala.collection.JavaConverters
import scala.tools.eclipse.testsetup.TestProjectSetup
import ch.epfl.insynth.core.Activator
import ch.epfl.insynth.core.preferences.InSynthConstants
import ch.epfl.insynth.reconstruction.Output

//object InSynthPreferencesTests extends TestProjectSetup("insynth", bundleName = "ch.epfl.insynth.tests")

class InSynthPreferencesTests {
	val testProjectSetup = new CompletionUtility(InSynthCompletionTests)
	
	import testProjectSetup._

  @Test
  def testOfferedNumberOfSnippets() {
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.OfferedSnippetsPropertyString, 5)
    
    checkCompletions("examplepkg4/Example4.scala")(List( CheckNumberOfCompletions(5) ))
    
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.OfferedSnippetsPropertyString, 15)
    
    checkCompletions("examplepkg4/Example4.scala")(List( CheckNumberOfCompletions(15) ))
    
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.OfferedSnippetsPropertyString, 0)
    
    checkCompletions("examplepkg4/Example4.scala")(List( CheckNumberOfCompletions(0) ))
  }
	
  @Test
  def testMaximumTime() {
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.OfferedSnippetsPropertyString, 500)
        
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.MaximumTimePropertyString, 0)
    
    checkCompletions("examplepkg4/Example4.scala")(List( CheckNumberOfCompletions(0) ))
    
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.MaximumTimePropertyString, 10)
    
		var numberOfCompletionsFor10ms = 0;
		
    checkCompletions("examplepkg4/Example4.scala")(List(
      (proposals: List[Output]) => {
        numberOfCompletionsFor10ms = proposals.size
      }
    ))
    
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.MaximumTimePropertyString, 100)
    
    checkCompletions("examplepkg4/Example4.scala")(List(
      (proposals: List[Output]) => {
        // we expect more completions for more time
        assertTrue(numberOfCompletionsFor10ms < proposals.size)
      }
    ))
  }

}