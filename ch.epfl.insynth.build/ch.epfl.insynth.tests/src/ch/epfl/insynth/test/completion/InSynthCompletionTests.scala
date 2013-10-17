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

object InSynthCompletionTests extends TestProjectSetup("insynth", bundleName = "ch.epfl.insynth.tests")

class InSynthCompletionTests {
	val testProjectSetup = new CompletionUtility(InSynthCompletionTests)
	
	import testProjectSetup._

  @Test
  def testExample1() {
    val oraclePos11 = ( List("A m", "0"), List("A.m()", "0") )
    
    val exampleCompletions = ( List(CheckContains(oraclePos11._1), CheckNumberOfCompletions(5)),
      List(CheckContains(oraclePos11._2), CheckNumberOfCompletions(5)) )
    
    checkCompletionsDual("examplepkg1/Example1.scala")(exampleCompletions)
  }
  
	@Ignore
  @Test
  def testExample2() {
    val oraclePos14 = ( List("new A() a", "new A() m b"), List("new A().a", "new A().m(b)") )
    
    val exampleCompletions = ( List(CheckContains(oraclePos14._1)), List(CheckContains(oraclePos14._2)) )
    
    checkCompletionsDual("examplepkg2/Example2.scala")(exampleCompletions)
  }
  
	@Ignore
  @Test
  def testExample3() {
    val oraclePos12regex = ( List("new A\\(\\) m1 (\\S+) => new A\\(\\) m2 \\1",
        "new A\\(\\) m1 (\\S+) => new A\\(\\) m2 l1"), List("new A\\(\\)\\.m1\\((\\S+) => new A\\(\\)\\.m2\\(\\1\\)\\)",
        "new A\\(\\)\\.m1\\((\\S+) => new A\\(\\)\\.m2\\(l1\\)\\)") )
    val oraclePos12strings = List("\"?\"")
    
    val exampleCompletions = (
      List(CheckRegexContains(oraclePos12regex._1), CheckContains(oraclePos12strings)),
      List(CheckRegexContains(oraclePos12regex._2), CheckContains(oraclePos12strings))
    )
    
    checkCompletionsDual("examplepkg3/Example3.scala")(exampleCompletions)
  }

}