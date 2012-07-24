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

object InSynthCompletionTests extends TestProjectSetup("insynth", bundleName = "ch.epfl.insynth.tests")
//object InSynthCompletionTests extends TestProjectSetup("completion")

class InSynthCompletionTests {
	val testProjectSetup = new CompletionUtility(InSynthCompletionTests)
	
	import testProjectSetup._

  /**
   * Test that completion shows only accessible members.
   */
  @Test
  def testExample1() {
    val oraclePos11 = List("A m", "0")
    
    val exampleCompletions = List(CheckContains(oraclePos11), CheckNumberOfCompletions(5))
    
    checkCompletions("examplepkg1/Example1.scala")(exampleCompletions)
  }
  
  @Test
  def testExample2() {
    val oraclePos14 = List("new A().a()", "new A() m b")
    
    val exampleCompletions = List(CheckContains(oraclePos14))
    
    checkCompletions("examplepkg2/Example2.scala")(exampleCompletions)
  }
  
  @Test
  def testExample3() {
    val oraclePos12regex = List("new A\\(\\) m1 \\{ (\\S+) => new A\\(\\) m2 \\1 \\}",
        "new A\\(\\) m1 \\{ (\\S+) => new A\\(\\) m2 l1 \\}")
    val oraclePos12strings = List("\"?\"")
    
    val exampleCompletions = List(CheckRegexContains(oraclePos12regex), CheckContains(oraclePos12strings))
    
    checkCompletions("examplepkg3/Example3.scala")(exampleCompletions)
  }

}