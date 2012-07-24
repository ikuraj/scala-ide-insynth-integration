package ch.epfl.insynth.test.completion

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
import ch.epfl.insynth.core.completion.InsynthCompletionProposalComputer
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext
import org.eclipse.jdt.core.ICompilationUnit
import org.eclipse.core.runtime.NullProgressMonitor
import ch.epfl.insynth.core.completion.InnerFinder
import scala.collection.JavaConversions
import scala.collection.JavaConverters
import scala.tools.eclipse.testsetup.TestProjectSetup
import java.{ util => ju, lang => jl }
import org.junit.Assert._
import org.junit.Test
import org.junit.Ignore
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.junit.runner.RunWith
import java.util.ArrayList

@RunWith(value = classOf[Parameterized])
class InSynthBenchmarkCompletionParametrizedTests(fileName: String, expectedSnippet: String) {
	val testProjectSetup = new CompletionUtility(InSynthBenchmarkCompletionTests)
	
	import testProjectSetup._
	
  @Test
  def test() {
    val oraclePos = List(expectedSnippet)
    
    val exampleCompletions = List(CheckContains(oraclePos))
    
    checkCompletions("main/scala/javaapi/nongenerics/" + fileName + ".scala")(exampleCompletions)
  }

}

object InSynthBenchmarkCompletionParametrizedTests {
  
	@Parameters
	def parameters: ju.Collection[Array[jl.String]] = {
	  val list = new ju.ArrayList[Array[jl.String]]
	  list add Array( "FileInputStreamStringname" , "new FileInputStream(\"?\")" )
	  list add Array( "FileOutputStreamFilefile" , "new FileOutputStream(tempFile)" )
	  list add Array( "FileStringname" , "new File(\"?\")" )
	  list add Array( "FileWriterFilefile" , "new FileWriter(outputFile)" )
	  list add Array( "FileWriterLPT1" , "new FileWriter(\"?\")" )
	  list add Array( "GridBagConstraints" , "new GridBagConstraints" )
	  list
	} 

}