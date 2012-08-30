package ch.epfl.insynth.test.completion

import scala.collection.JavaConversions
import scala.collection.JavaConverters
import org.junit.Assert._
import org.junit.Test
import org.junit.BeforeClass
import org.junit.Ignore
import scala.tools.eclipse.testsetup.SDTTestUtils
import scala.tools.eclipse.javaelements.ScalaCompilationUnit
import scala.tools.nsc.interactive.Response
import scala.tools.eclipse.ScalaWordFinder
import scala.tools.nsc.util.SourceFile
import scala.tools.eclipse.ScalaPresentationCompiler
import org.eclipse.jface.text.contentassist.ICompletionProposal
import scala.tools.eclipse.testsetup.TestProjectSetup
import org.eclipse.jdt.core.search.{ SearchEngine, IJavaSearchConstants, IJavaSearchScope, SearchPattern, TypeNameRequestor }
import org.eclipse.jdt.core.IJavaElement
import scala.tools.nsc.util.OffsetPosition
import scala.tools.eclipse.completion.ScalaCompletions
import scala.tools.eclipse.completion.CompletionProposal
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext
import org.eclipse.jdt.core.ICompilationUnit
import org.eclipse.core.runtime.NullProgressMonitor
import scala.tools.eclipse.testsetup.TestProjectSetup
import ch.epfl.insynth.core.completion.InsynthCompletionProposalComputer
import ch.epfl.insynth.core.completion.InnerFinder
import ch.epfl.insynth.core.Activator
import ch.epfl.insynth.core.preferences.InSynthConstants
import ch.epfl.insynth.Config

object IssuesTests extends TestProjectSetup("issues", bundleName = "ch.epfl.insynth.tests") {
  
  @BeforeClass
  def setup() {    
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.OfferedSnippetsPropertyString, 10)        
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.MaximumTimePropertyString, 500)
  }
  
}

class IssuesTests {
	val testProjectSetup = new CompletionUtility(IssuesTests)
	
	import testProjectSetup._

  @Test
  def testGitHubIssueNo4() {
	  // TODO re-check when alternative syntax generation is implemented (we want just println) 
    val oraclePos8_classicStyle = List("Predef.println()")    
    val checkersPos8 = List(CheckContains(oraclePos8_classicStyle))
    
    Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.CodeStyleParenthesesPropertyString,
        InSynthConstants.CodeStyleParenthesesClassic)
    
    checkCompletions("github/IssueNo4.scala")(checkersPos8, List.empty)
  }

}