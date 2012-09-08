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

class OtherIssuesTests {
  // create a Scala IDE project setup
	val testProjectSetup = new CompletionUtility(IssuesTests)
	
	val preferenceStore = Activator.getDefault.getPreferenceStore
	
	// import members
	import testProjectSetup._	
	import InSynthConstants._

  @Test
  def test1 {
    preferenceStore.setValue(CodeStyleParenthesesPropertyString, CodeStyleParenthesesClean)
        
    {	        
	    val oraclePos5 = List("\\((\\S+), (\\S+)\\) => \\1\\(\\2\\)")    
	    val checkersPos5 = List(CheckRegexContains(oraclePos5))
	    
	    checkCompletions("other/ParenthesesIssue.scala")(checkersPos5)
    }
    
    preferenceStore.setValue(CodeStyleParenthesesPropertyString, CodeStyleParenthesesClassic)
    
    {	        
	    val oraclePos5 = List("\\((\\S+), (\\S+)\\) => \\1\\(\\2\\)")    
	    val checkersPos5 = List(CheckRegexContains(oraclePos5))
	    
	    checkCompletions("other/ParenthesesIssue.scala")(checkersPos5)
    }
  }
}
