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

object TypesHandlingTests extends TestProjectSetup("features", bundleName = "ch.epfl.insynth.tests") {
  
  @BeforeClass
  def setup() {    
    // set appropriate preference values (expect 5 completions)
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.OfferedSnippetsPropertyString, 5)        
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.MaximumTimePropertyString, 500)
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.DoSeparateLoggingPropertyString, true)
		//Config.proofTreeLevelToLog = 3
  }
  
}

class TypesHandlingTests {
	val testProjectSetup = new CompletionUtility(TypesHandlingTests)
	
	import testProjectSetup._

  @Test
  def testExample1() {
	  // instantiated type constructor
    val oraclePos14 = List("A m1")    
    val checkersPos14 = List(CheckContains(oraclePos14))
    
    // generic types
    val checkersPos18 = List(CheckNumberOfCompletions(0))
    
    checkCompletions("generics/Example1.scala")(checkersPos14, checkersPos18)
  }

}