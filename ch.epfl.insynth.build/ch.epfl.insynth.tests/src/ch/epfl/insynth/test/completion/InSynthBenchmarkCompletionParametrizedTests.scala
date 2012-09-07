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
    
  @BeforeClass
  def setup() {    
		// tests are made according to the clean code style
		Activator.getDefault.getPreferenceStore.
			setValue(InSynthConstants.CodeStyleParenthesesPropertyString, InSynthConstants.CodeStyleParenthesesClean)
  }
  
	@Parameters
	def parameters: ju.Collection[Array[jl.String]] = {
	  val list = new ju.ArrayList[Array[jl.String]]
	  list add Array( "FileInputStreamStringname" , "new FileInputStream(\"?\")" )
	  list add Array( "FileOutputStreamFilefile" , "new FileOutputStream(tempFile)" )
	  list add Array( "FileStringname" , "new File(\"?\")" )
	  list add Array( "FileWriterFilefile" , "new FileWriter(outputFile)" )
	  list add Array( "FileWriterLPT1" , "new FileWriter(\"?\")" )
	  list add Array( "GridBagConstraints" , "new GridBagConstraints" )
	  list add Array( "GroupLayoutContainerhost" , "new GroupLayout(panel)" )
	  list add Array( "ImageIconStringfilename" , "new ImageIcon(\"?\")" )
	  list add Array( "GridBagConstraints" , "new GridBagConstraints" )
//	  list add Array( "InputStreamReaderInputStreamin" , "new InputStreamReader(System.in)" )
	  list add Array( "JButtonStringtext" , "new JButton(\"?\")" )
	  list add Array( "JCheckBoxStringtext" , "new JCheckBox(\"?\")" )
	  list add Array( "JFormattedTextFieldAbstractFormatterFactoryfactoryObjectcurrentValue" , "new JFormattedTextField(factory)" )
	  list add Array( "JFormattedTextFieldFormatterformatter" , "new MaskFormatter(\"?\")" )
	  list add Array( "JTableObjectnameObjectdata" , "new JTable(rows, columns)" )
	  list add Array( "JToggleButtonStringtext" , "new JFrame(\"?\")" )
	  list add Array( "JTree" , "new JTree" )
	  list add Array( "JWindow" , "new JWindow" )
	  list add Array( "ObjectInputStreamInputStreamin" , "new ObjectInputStream(fis)" )
	  list add Array( "ObjectOutputStreamOutputStreamout" , "new ObjectOutputStream(fos)" )
	  list add Array( "PipedReaderPipedWritersrc" , "new PipedReader(pw)" )
	  list add Array( "PipedWriter" , "new PipedWriter" )
	  list add Array( "Pointintxinty" , "new Point(0, 0)" )
	  list add Array( "PrintStreamOutputStreamout" , "new PrintStream(fout)" )
	  list add Array( "PrintWriterBufferedWriterbooleanautoFlush" , "new PrintWriter(bw, false)" )
	  list add Array( "SequenceInputStreamInputStreams1InputStreams2" , "new SequenceInputStream(f1, f2)" )
	  list add Array( "ServerSocketintport" , "new ServerSocket(port)" )
	  list add Array( "StreamTokenizerFileReaderfileReader" , "new StreamTokenizer(br)" )
	  list add Array( "StringReaderStrings" , "new StringReader(\"?\")" )
	  list add Array( "TimerintvalueActionListeneract" , "new Timer(0, actionListener)" )
	  list add Array( "TransferHandlerStringproperty" , "new TransferHandler(s)" )
	  list add Array( "URLStringspecthrowsMalformedURLException" , "new URL(\"?\").openConnection()" )
	  list
	}

}