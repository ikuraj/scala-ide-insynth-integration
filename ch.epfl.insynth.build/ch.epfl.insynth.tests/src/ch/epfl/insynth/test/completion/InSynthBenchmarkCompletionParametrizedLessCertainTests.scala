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
import org.junit.BeforeClass
import ch.epfl.insynth.core.Activator
import ch.epfl.insynth.core.preferences.InSynthConstants

/** tests for benchmarks which produce less certain solutions 
 * the ones that have >10 in groundResults.tex */
@RunWith(value = classOf[Parameterized])
class InSynthBenchmarkCompletionParametrizedLessCertainTests(fileName: String, expectedSnippet: String) {
	val testProjectSetup = new CompletionUtility(InSynthBenchmarkCompletionTests)
	
	import testProjectSetup._
	
  @Test
  def test() {
    val oraclePos = List(expectedSnippet)
    
    val exampleCompletions = List(CheckContains(oraclePos))
    
    checkCompletions("main/scala/javaapi/nongenerics/" + fileName + ".scala")(exampleCompletions)
  }

}

object InSynthBenchmarkCompletionParametrizedLessCertainTests {
  
  @BeforeClass
  def setup() {    
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.OfferedSnippetsPropertyString, 50)        
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.MaximumTimePropertyString, 2000)
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.DoSeparateLoggingPropertyString, true)
  }
  
	@Parameters
	def parameters: ju.Collection[Array[jl.String]] = {
	  val list = new ju.ArrayList[Array[jl.String]]
	  list add Array( "BoxLayoutContainertargetintaxis" , "new BoxLayout(container, BoxLayout.Y_AXIS)" )
	  list add Array( "BufferedImageintwidthintheightintimageType" , "new BufferedImage(0, 0, BufferedImage.TYPE_INT_RGB)" )
	  list add Array( "ByteArrayInputStream" , "new DataInputStream(new ByteArrayInputStream(\"?\".getBytes()))" )
	  list add Array( "ByteArrayInputStreambytebufintoffsetintlength" , "new ByteArrayInputStream(b, 0, 0)" )
	  list add Array( "CharArrayReadercharbuf" , "new CharArrayReader(outStream.toCharArray())" )
	  list add Array( "DatagramPacketbytebufferintbufferLength" , "new DatagramPacket(buffer, buffer.length)" )
	  list add Array( "DatagramPacketbytebufintlengthInetAddressaddressintport" , "new DatagramPacket(buffer, buffer.length, ia, 0)" )
	  list add Array( "FileWriterStringfileNamebooleanappend" , "new BufferedWriter(new FileWriter(\"?\", true))" )
	  list add Array( "GridLayoutintrowsintcolsinthgapintvgap" , "new GridLayout(0, 0, 0, 0)" )
	  list add Array( "InputStreamReaderInputStreaminStringcharsetName" , "new BufferedReader(new InputStreamReader(fis, \"?\"))" )
	  list add Array( "JButtonStringtextIconicon" , "new JButton(\"?\",warnIcon)" )
	  list add Array( "JCheckBoxStringtextbooleanselected" , "new JCheckBox(\"?\", true)" )
	  list add Array( "JTextAreaDocumentdocument" , "new JTextArea(document)" )
	  list add Array( "JTextAreaStringtext" , "new JTextArea(\"?\")" )
	  list add Array( "OverlayLayoutContainertarget" , "new OverlayLayout(panel)" )
	  list add Array( "PrintStreamFilefile" , "new PrintStream(file)" )
	  list add Array( "SocketInetAddressaddressintportthrowsIOException" , "InetAddress.getByName(\"?\")" )
	  list add Array( "StreamTokenizerReaderr" , "new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)))" )
	  list
	}

}