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
import org.junit.After
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.junit.runner.RunWith
import java.util.ArrayList
import org.junit.BeforeClass
import ch.epfl.insynth.core.Activator
import ch.epfl.insynth.core.preferences.InSynthConstants
import ch.epfl.insynth.statistics.format.Utility
import ch.epfl.insynth.statistics.format.XMLable
import ch.epfl.insynth.statistics.ReconstructorStatistics
import ch.epfl.insynth.statistics.InSynthStatistics
import java.io.File
import scala.collection.mutable.{ LinkedList => MutableList }
import org.junit.AfterClass

import ch.epfl.insynth.{ Config => IConfig }
import ch.epfl.insynth.reconstruction.{ Config => RConfig }
import ch.epfl.insynth.loader.{ ZeroWeightsLoader, RegularWeightsLoader, NoCorpusWeightsLoader }

@RunWith(value = classOf[Parameterized])
class InSynthBenchmarkCompletionParametrizedTestsZeroLoader(fileName: String, expectedSnippet: String,
    expectedPositionJavaAPI: (Int, Int), expectedPositionGeneralized: (Int, Int))
  extends InSynthBenchmarkCompletionParametrizedTests(fileName, expectedSnippet, expectedPositionJavaAPI, expectedPositionGeneralized) {
  
	import InSynthBenchmarkCompletionParametrizedTests.testProjectSetup._
	
	@Ignore
  @Test
  // non generalized tests (individual import.clazz used)
  override def testJavaAPI() {
	  val myPosition = expectedPositionJavaAPI
    val oraclePos = List( (expectedSnippet, myPosition) )
    
    val exampleCompletions = List(CheckContainsAtPosition(oraclePos))
    
    innerTestFunction("main/scala/javaapi/nongenerics/", 0, exampleCompletions)
  }
	
  @Test
  // generalized tests
  override def testGeneralized() {
	  val myPosition = expectedPositionGeneralized
    val oraclePos = List( (expectedSnippet, myPosition) )
    
    val exampleCompletions = 
      if (myPosition != (-1, -1)) List(CheckContainsAtPosition(oraclePos))
      else Nil
    
    innerTestFunction("main/scala/generalized/nongenerics/", 1, exampleCompletions)
  }
  
} 


object InSynthBenchmarkCompletionParametrizedTestsZeroLoader {
	val testProjectSetup = new CompletionUtility(InSynthBenchmarkCompletionTests)
	
  val statsFileNames = List("insynth_statistics_javaapi_zero.txt", "insynth_statistics_generalized_zero.txt")
  val statsCSVFileNames = List("insynth_statistics_javaapi_zero.txt", "insynth_statistics_generalized_zero.txt")
  val csvFile = "data_zero.csv"
  for (statsFileName <- List(csvFile) ++ statsFileNames ++ statsCSVFileNames) {
	  val file = new File(statsFileName)
	  file.delete
	  file.createNewFile    
  }
	  
  val generalizedPositions = List(
    0, 0, 0, -1, 0,  
    0, -1, 0, 1, 0, // 10 
    -1, -1, 1, -1, 1,
    0, 0, 0, 0, -1, // 20
    0, -1, -1, -1, -1,
    0, -1, 0, 0, 0, // 30
    -1, 1, 0, 4, -1, 
    -1, 0, 0, -1, 0, // 40
    -1, 1, 0, 0, 0, 
    0, 0, -1
  )
//  val generalizedPositions = List(
//    0, 0, 0, 0, 0,
//    0, 0, 1, 7, 1, // 10
//    2, 1, 1, 1, 1,
//    0, 0, 0, 0, 1, // 20
//    0, 4, 5, 3, 1,
//    1, 0, 0, 0, 0, // 30
//    5, 1, 0, 0, -1,
//    0, 0, 0, 1, 1, // 40
//    -1, 1, 0, 0, 0,
//    0, 0, 2
//  )
  
//  val generalizedPositions = List(
//    0, 0, 0, 0, 0,  0, 0, 0, 3, 0, 1, 3, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 
//    3, 1, 0, 0, 0, 0, 0, 0, 1, 0, 7, 8, 0, 0, 0, 0, 0, 5, 1, 0, 0, 0, 0, 0, 1
//  )
	
  def resetRunStatisticsStatic = InSynthBenchmarkCompletionParametrizedTests.resetRunStatisticsStatic
  
  var storedWeightForLeaves: Double = _
  var storedDeclarationWeight: Double = _
  
  val store = Activator.getDefault.getPreferenceStore
  import InSynthConstants._
  
  @BeforeClass
  def setup() {    
		InSynthBenchmarkCompletionParametrizedTests.setup
		
		resetRunStatisticsStatic
		
		// weight loader		
		IConfig.defaultWeightsLoader = ZeroWeightsLoader
		
		store.setValue(OfferedSnippetsPropertyString, 10)        
		store.setValue(MaximumTimePropertyString, 8000)
  }
	
	@AfterClass
	def writeCSVTable = {
		import Utility._
	
		import InSynthBenchmarkCompletionParametrizedTests.{ csvFile => _, parameters => _, generalizedPositions => _, _ }
		
	  appendToFile(csvFile, firstRowString)
//		assertEquals(parameters.size, tableFilenames.size)
//		assertEquals(parameters.size, tableDeclarations.size)
//		assertEquals(parameters.size, tableEngineTimes.size)
//		assertEquals(parameters.size, tableReconstructionTime.size)
//		assertEquals(parameters.size, generalizedPositions.size)
	  val paramssize = tableFilenames.size
		assertEquals(paramssize, tableFilenames.size)
		assertEquals(paramssize, tableDeclarations.size)
		assertEquals(paramssize, tableEngineTimes.size)
		assertEquals(paramssize, tableReconstructionTime.size)
		assertEquals(paramssize, generalizedPositions.size)
		for( ((((fileName, numberDec), engine), reconstruction), position) <- tableFilenames zip tableDeclarations zip
	    tableEngineTimes zip tableReconstructionTime zip generalizedPositions) {		  
			appendToFile(csvFile, fileName.dropRight(6) + "," + (position + 1) + ", " + numberDec + ", " + engine + ", " + reconstruction)
		}
		
		// weight loader		
		IConfig.defaultWeightsLoader = RegularWeightsLoader
	}
  
	@Parameters
	def parameters: ju.Collection[Array[Object]] = {
	  val list = new ju.ArrayList[Array[Object]]
	  
	  case class GivesObject(int: Int) {
	  	def unary_! = (int, int) //int : jl.Integer
	  }
	  implicit def convertIntToGivesObject(int: Int) = GivesObject(int)
	  
	  var generalizedPositionIterator = generalizedPositions.iterator
	  def gg = ! generalizedPositionIterator.next
	  
	  list add Array( "FileInputStreamStringname" , "new FileInputStream(\"?\")", ! 0, gg ) // 0
	  list add Array( "FileOutputStreamFilefile" , "new FileOutputStream(tempFile)", ! 0, gg )
	  list add Array( "FileStringname" , "new File(\"?\")", ! 0, gg )
	  list add Array( "FileWriterFilefile" , "new FileWriter(outputFile)", ! 0, gg )
	  list add Array( "FileWriterLPT1" , "new FileWriter(\"?\")", ! 0, gg )
	  list add Array( "GridBagConstraints" , "new GridBagConstraints", ! 0, gg )
	  list add Array( "GroupLayoutContainerhost" , "new GroupLayout(panel)", ! 0, gg )
	  list add Array( "ImageIconStringfilename" , "new ImageIcon(\"?\")", ! 0, gg )
	  list add Array( "InputStreamReaderInputStreamin" , "new InputStreamReader(System in)", ! 0, gg )
	  list add Array( "JButtonStringtext" , "new JButton(\"?\")", ! 0, gg )
	  list add Array( "JCheckBoxStringtext" , "new JCheckBox(\"?\")", ! 1, gg ) // 10
	  list add Array( "JFormattedTextFieldAbstractFormatterFactoryfactoryObjectcurrentValue" , "new JFormattedTextField(factory)", ! 3, gg )
	  list add Array( "JFormattedTextFieldFormatterformatter" , "new MaskFormatter(\"?\")", ! 1, gg )
	  list add Array( "JTableObjectnameObjectdata" , "new JTable(rows, columns)", ! 1, gg )
	  list add Array( "JToggleButtonStringtext" , "new JFrame(\"?\")", ! 1, gg )
	  list add Array( "JTree" , "new JTree", ! 0, gg ) // 15
	  list add Array( "JWindow" , "new JWindow", ! 0, gg )
	  list add Array( "ObjectInputStreamInputStreamin" , "new ObjectInputStream(fis)", ! 0, gg )
	  list add Array( "ObjectOutputStreamOutputStreamout" , "new ObjectOutputStream(fos)", ! 0, gg )
	  list add Array( "PipedReaderPipedWritersrc" , "new PipedReader(pw)", ! 1, gg )
	  list add Array( "PipedWriter" , "new PipedWriter", ! 0, gg ) // 20
	  list add Array( "Pointintxinty" , "new Point(0, 0)", ! 1, gg )
	  list add Array( "PrintStreamOutputStreamout" , "new PrintStream(fout)", ! 0, gg )
	  list add Array( "PrintWriterBufferedWriterbooleanautoFlush" , "new PrintWriter(bw, false)", ! 2, gg )
	  list add Array( "SequenceInputStreamInputStreams1InputStreams2" , "new SequenceInputStream(f1, f2)", ! 2, gg )
	  list add Array( "ServerSocketintport" , "new ServerSocket(port)", ! 0, gg )
	  list add Array( "StreamTokenizerFileReaderfileReader" , "new StreamTokenizer(br)", ! 0, gg )
	  list add Array( "StringReaderStrings" , "new StringReader(\"?\")", ! 0, gg )
	  list add Array( "TimerintvalueActionListeneract" , "new Timer(0, actionListener)", ! 0, gg )
	  list add Array( "TransferHandlerStringproperty" , "new TransferHandler(s)", ! 0, gg )
	  list add Array( "URLStringspecthrowsMalformedURLException" , "new URL(\"?\") openConnection", ! 0, gg ) // 30
	  
	  // missing 4 tests
	  list add Array( "FileReaderFilefile" , "new FileReader(inputFile)", ! 1, gg )
	  list add Array( "GridBagLayout" , "new GridBagLayout", ! 0, gg )
	  list add Array( "JViewport" , "new JViewport", !7, gg )
	  list add Array( "LineNumberReaderReaderin" , "new LineNumberReader(new InputStreamReader(System in))", ! 0, gg )
	  // tests from InSynthBenchmarkCompletionTests
	  list add Array( "AWTPermissionStringname" , "new AWTPermission(\"?\")", ! 0, gg ) // 35
	  // cannot find, not even in 100 snippets
		//list add Array( "BoxLayoutContainertargetintaxis" , "new BoxLayout(container, BoxLayout.Y_AXIS)", ! 0, gg )
		list add Array( "BufferedInputStreamFileInputStream" , "new BufferedInputStream(fis)", ! 0, gg )
		list add Array( "BufferedOutputStream" , "new BufferedOutputStream(file)", ! 0, gg )
		list add Array( "BufferedReaderFileReaderfileReader" , "new BufferedReader(fr)", ! 0, gg )
		list add Array( "BufferedReaderInputStreamReader" , "new BufferedReader(isr)", ! 0, gg ) // 40
		list add Array( "BufferedReaderReaderin" , "new BufferedReader(new InputStreamReader(url openStream))", ! 0, gg )
		list add Array( "ByteArrayOutputStreamintsize" , "new ByteArrayOutputStream(0)", ! 1, gg )
		list add Array( "DatagramSocket" , "new DatagramSocket", ! 0, gg )
		list add Array( "DataInputStreamFileInputStreamfileInputStream" , "new DataInputStream(fis)", ! 0, gg )
		list add Array( "DataOutputStreamFileOutputStreamfileOutputStream" , "new DataOutputStream(fos)", ! 0, gg ) // 45
		list add Array( "DefaultBoundedRangeModel" , "new DefaultBoundedRangeModel", ! 0, gg )
		list add Array( "DisplayModeintwidthintheightintbitDepthintrefreshRate" , "gs getDisplayMode", ! 0, gg )
		list add Array( "FileInputStreamFileDescriptorfdObj" , "new FileInputStream(aFile)", ! 1, gg )
	  
	  list
	}

}