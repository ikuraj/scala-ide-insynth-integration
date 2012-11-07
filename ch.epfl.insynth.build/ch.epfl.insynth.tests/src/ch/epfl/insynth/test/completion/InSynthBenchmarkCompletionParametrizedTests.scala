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

@RunWith(value = classOf[Parameterized])
class InSynthBenchmarkCompletionParametrizedTests(fileName: String, expectedSnippet: String,
    expectedPositionJavaAPI: (Int, Int), expectedPositionGeneralized: (Int, Int)) {
	val testProjectSetup = new CompletionUtility(InSynthBenchmarkCompletionTests)
	
	import testProjectSetup._
	
  @Test
  // non generalized tests (individual import.clazz used)
  def testJavaAPI() {
    val oraclePos = List( (expectedSnippet, expectedPositionJavaAPI) )
    
    val exampleCompletions = List(CheckContainsAtPosition(oraclePos))
    
    checkCompletions("main/scala/javaapi/nongenerics/" + fileName + ".scala")(exampleCompletions)
  }
	
  @Test
  // generalized tests
  def testGeneralized() {
    val oraclePos = List( (expectedSnippet, expectedPositionGeneralized) )
    
    val exampleCompletions = List(CheckContainsAtPosition(oraclePos))
    
    checkCompletions("main/scala/generalized/nongenerics/" + fileName + ".scala")(exampleCompletions)
  }

}

object InSynthBenchmarkCompletionParametrizedTests {
      
  @BeforeClass
  def setup() {    
		// tests are made according to the clean code style
		Activator.getDefault.getPreferenceStore.
			setValue(InSynthConstants.CodeStyleParenthesesPropertyString, InSynthConstants.CodeStyleParenthesesClean)
  }
  
	@Parameters
	def parameters: ju.Collection[Array[Object]] = {
	  val list = new ju.ArrayList[Array[Object]]
	  
	  case class GivesObject(int: Int) {
	  	def unary_! = (int, int) //int : jl.Integer
	  }
	  implicit def convertIntToGivesObject(int: Int) = GivesObject(int)
	  
	  list add Array( "FileInputStreamStringname" , "new FileInputStream(\"?\")", ! 0, ! 0 ) // 0
	  list add Array( "FileOutputStreamFilefile" , "new FileOutputStream(tempFile)", ! 0, ! 0 )
	  list add Array( "FileStringname" , "new File(\"?\")", ! 0, ! 0 )
	  list add Array( "FileWriterFilefile" , "new FileWriter(outputFile)", ! 0, ! 0 )
	  list add Array( "FileWriterLPT1" , "new FileWriter(\"?\")", ! 0, ! 0 )
	  list add Array( "GridBagConstraints" , "new GridBagConstraints", ! 0, ! 0 )
	  list add Array( "GroupLayoutContainerhost" , "new GroupLayout(panel)", ! 0, ! 0 )
	  list add Array( "ImageIconStringfilename" , "new ImageIcon(\"?\")", ! 0, ! 0 )
	  list add Array( "InputStreamReaderInputStreamin" , "new InputStreamReader(System in)", ! 0, ! 3 )
	  list add Array( "JButtonStringtext" , "new JButton(\"?\")", ! 0, ! 0 )
	  list add Array( "JCheckBoxStringtext" , "new JCheckBox(\"?\")", ! 1, ! 1 ) // 10
	  list add Array( "JFormattedTextFieldAbstractFormatterFactoryfactoryObjectcurrentValue" , "new JFormattedTextField(factory)", ! 3, ! 3 )
	  list add Array( "JFormattedTextFieldFormatterformatter" , "new MaskFormatter(\"?\")", ! 1, ! 1 )
	  list add Array( "JTableObjectnameObjectdata" , "new JTable(rows, columns)", ! 1, ! 1 )
	  list add Array( "JToggleButtonStringtext" , "new JFrame(\"?\")", ! 1, ! 1 )
	  list add Array( "JTree" , "new JTree", ! 0, ! 0 ) // 15
	  list add Array( "JWindow" , "new JWindow", ! 0, ! 0 )
	  list add Array( "ObjectInputStreamInputStreamin" , "new ObjectInputStream(fis)", ! 0, ! 0 )
	  list add Array( "ObjectOutputStreamOutputStreamout" , "new ObjectOutputStream(fos)", ! 0, ! 0 )
	  list add Array( "PipedReaderPipedWritersrc" , "new PipedReader(pw)", ! 1, ! 1 )
	  list add Array( "PipedWriter" , "new PipedWriter", ! 0, ! 0 ) // 20
	  list add Array( "Pointintxinty" , "new Point(0, 0)", ! 1, ! 1 )
	  list add Array( "PrintStreamOutputStreamout" , "new PrintStream(fout)", ! 0, ! 0 )
	  list add Array( "PrintWriterBufferedWriterbooleanautoFlush" , "new PrintWriter(bw, false)", ! 2, ! 3 )
	  list add Array( "SequenceInputStreamInputStreams1InputStreams2" , "new SequenceInputStream(f1, f2)", ! 2, ! 1 )
	  list add Array( "ServerSocketintport" , "new ServerSocket(port)", ! 0, ! 0 )
	  list add Array( "StreamTokenizerFileReaderfileReader" , "new StreamTokenizer(br)", ! 0, ! 0 )
	  list add Array( "StringReaderStrings" , "new StringReader(\"?\")", ! 0, ! 0 )
	  list add Array( "TimerintvalueActionListeneract" , "new Timer(0, actionListener)", ! 0, ! 0 )
	  list add Array( "TransferHandlerStringproperty" , "new TransferHandler(s)", ! 0, ! 0 )
	  list add Array( "URLStringspecthrowsMalformedURLException" , "new URL(\"?\") openConnection", ! 0, ! 0 ) // 30
	  
	  // missing 4 tests
	  list add Array( "FileReaderFilefile" , "new FileReader(inputFile)", ! 1, ! 1 )
	  list add Array( "GridBagLayout" , "new GridBagLayout", ! 0, ! 0 )
	  list add Array( "JViewport" , "new JViewport", !7, ! 7 )
	  list add Array( "LineNumberReaderReaderin" , "new LineNumberReader(new InputStreamReader(System in))", ! 0, ! 8 )
	  // tests from InSynthBenchmarkCompletionTests
	  list add Array( "AWTPermissionStringname" , "new AWTPermission(\"?\")", ! 0, ! 0 ) // 35
		list add Array( "BoxLayoutContainertargetintaxis" , "new BoxLayout(container, BoxLayout.Y_AXIS)", ! 0, ! 0 )
		list add Array( "BufferedInputStreamFileInputStream" , "new BufferedInputStream(fis)", ! 0, ! 0 )
		list add Array( "BufferedOutputStream" , "new BufferedOutputStream(file)", ! 0, ! 0 )
		list add Array( "BufferedReaderFileReaderfileReader" , "new BufferedReader(fr)", ! 0, ! 0 )
		list add Array( "BufferedReaderInputStreamReader" , "new BufferedReader(isr)", ! 0, ! 0 ) // 40
		list add Array( "BufferedReaderReaderin" , "new BufferedReader(new InputStreamReader(url openStream))", ! 0, ! 5 )
		list add Array( "ByteArrayOutputStreamintsize" , "new ByteArrayOutputStream(0)", ! 1, ! 1 )
		list add Array( "DatagramSocket" , "new DatagramSocket", ! 0, ! 0 )
		list add Array( "DataInputStreamFileInputStreamfileInputStream" , "new DataInputStream(fis)", ! 0, ! 0 )
		list add Array( "DataOutputStreamFileOutputStreamfileOutputStream" , "new DataOutputStream(fos)", ! 0, ! 0 ) // 45
		list add Array( "DefaultBoundedRangeModel" , "new DefaultBoundedRangeModel", ! 0, ! 0 )
		list add Array( "DisplayModeintwidthintheightintbitDepthintrefreshRate" , "gs getDisplayMode", ! 0, ! 0 )
		list add Array( "FileInputStreamFileDescriptorfdObj" , "new FileInputStream(aFile)", ! 1, ! 1 )
	  
	  list
	}

}