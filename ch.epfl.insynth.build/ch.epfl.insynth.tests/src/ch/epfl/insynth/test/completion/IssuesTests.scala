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

object IssuesTests extends TestProjectSetup("issues", bundleName = "ch.epfl.insynth.tests") {
  
	val preferenceStore = Activator.getDefault.getPreferenceStore
	
	import InSynthConstants._
	
  @BeforeClass
  def setup() {    
		preferenceStore.setValue(OfferedSnippetsPropertyString, 10)        
		preferenceStore.setValue(MaximumTimePropertyString, 500)
  }
  
}

class IssuesTests {
  // create a Scala IDE project setup
	val testProjectSetup = new CompletionUtility(IssuesTests)
	
	// import members
	import testProjectSetup._
	
	import IssuesTests._
	import InSynthConstants._

  @Test
  def testGitHubIssueNo2() {
    preferenceStore.setValue(CodeStyleParenthesesPropertyString,
        CodeStyleParenthesesClassic)
        
    {
	    preferenceStore.setValue(CodeStyleSimpleApplicationNameTransformPropertyString,
	        false)
	        
	    val oraclePos8 = List("this.$hash$hash()")    
	    val checkersPos8 = List(CheckContains(oraclePos8))
	    
	    checkCompletions("github/IssueNo2.scala")(checkersPos8)
    }
     
    {
	    preferenceStore.setValue(CodeStyleSimpleApplicationNameTransformPropertyString,
	        true)
	        
	    val oraclePos8 = List("this.##()")    
	    val checkersPos8 = List(CheckContains(oraclePos8))
	        
	    checkCompletions("github/IssueNo2.scala")(checkersPos8)
    }
  }

	// does not work in Scala 2.10 (Expected snippet: new C(0), calculated snippets: new Main().c)
	@Ignore
  @Test
  def testGitHubIssueNo3() {        
    val oraclePos11 = List("new C(0)")  
    val checkersPos11 = List(CheckContains(oraclePos11))
    val checkersPos16 = List(CheckContains(oraclePos11))
    
    checkCompletions("github/IssueNo3.scala")(checkersPos11, checkersPos16)
  }
  
  // TODO failing test
	@Ignore
  @Test
  def testFailingGitHubIssueNo3() {        
	  {
	    preferenceStore.setValue(CodeStyleApplyOmittingPropertyString,
	        false)
	    val oraclePos11 = List("C apply 0")  
	    val checkersPos11 = List(CheckContains(oraclePos11))
	    val checkersPos16 = List(CheckContains(oraclePos11))
	    
	    checkCompletions("github/IssueNo3.scala")(checkersPos11, checkersPos16)
	  }
	  {
	    preferenceStore.setValue(CodeStyleApplyOmittingPropertyString,
	        true)
	    val oraclePos11 = List("C(0)")  
	    val checkersPos11 = List(CheckContains(oraclePos11))
	    val checkersPos16 = List(CheckContains(oraclePos11))
	    
	    checkCompletions("github/IssueNo3.scala")(checkersPos11, checkersPos16)
	  }
  }
	
  // TODO failing test
	@Ignore
  @Test
  def testGitHubIssueNo4() {
	  // TODO re-check when alternative syntax generation is implemented (we want just println) 
    val oraclePos8_classicStyle = List("Predef.println()")    
    val checkersPos8 = List(CheckContains(oraclePos8_classicStyle))
    
    preferenceStore.setValue(CodeStyleParenthesesPropertyString,
        CodeStyleParenthesesClassic)
    
    checkCompletions("github/IssueNo4.scala")(checkersPos8, List.empty)
  }
	
  @Test
  def testGitHubIssueNo6() {
	  // TODO re-check when alternative syntax generation is implemented (we want just println) 
    val oraclePos8 = List("Array(0)")    
    val checkersPos8 = List(CheckContains(oraclePos8))
    
    preferenceStore.setValue(CodeStyleApplyOmittingPropertyString, true)
    
    preferenceStore.setValue(CodeStyleParenthesesPropertyString,
        CodeStyleParenthesesClean)          
    
    checkCompletions("github/IssueNo6.scala")(checkersPos8, List.empty)
              
    preferenceStore.setValue(CodeStyleParenthesesPropertyString,
        CodeStyleParenthesesClassic)
        
    checkCompletions("github/IssueNo6.scala")(checkersPos8, List.empty)
  }

}
