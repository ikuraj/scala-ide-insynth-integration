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
import org.junit.Before

@RunWith(value = classOf[Parameterized])
class InSynthBenchmarkCompletionParametrizedTestsAllLoader(fileName: String, expectedSnippet: String) {
  
	import InSynthBenchmarkCompletionParametrizedTests.testProjectSetup._
	import InSynthBenchmarkCompletionParametrizedTestsAllLoader._
	
  val store = Activator.getDefault.getPreferenceStore
  import InSynthConstants._
				  		
	def innerTestFunction(path: String, index: Int, exampleCompletions: List[Checker]*) = {    
    for (i <- 1 to 5)
    	checkCompletions(path + fileName + ".scala")(exampleCompletions: _*)
        
  	import InSynthStatistics._
  	import ReconstructorStatistics._
    	
  	assertEquals("lastEngineTime should contain 5 elements", 5, lastEngineTime.size)
  	assertEquals("reconstructionTime should contain 5 elements", 5, reconstructionTime.size)
  	
  	assertEquals(1, lastNumberOfDeclarations.distinct.size)
  	
  	tableDeclarations(index) :+= lastNumberOfDeclarations.head
  	tableEngineTimes(index) :+= lastEngineTime.sum.toFloat/lastEngineTime.size
  	tableFilenames(index) :+= currentRun.fileName
  	tableReconstructionTimes(index) :+= reconstructionTime.sum.toFloat/reconstructionTime.size
    	    
  }
  
  case class GivesObject(int: Int) {
  	def unary_! = (int, int) //int : jl.Integer
  }
  implicit def convertIntToGivesObject(int: Int) = GivesObject(int)
  
  @Test
  // generalized tests
  override def testRegular() {
	  val myPosition = generalizedPositionsCurrentList(0)
    val oraclePos = List( (expectedSnippet, ! myPosition) )
    
		// weight loader		
		IConfig.defaultWeightsLoader = RegularWeightsLoader
		
		store.setValue(OfferedSnippetsPropertyString, 10)        
		store.setValue(MaximumTimePropertyString, 8000)
		
    val exampleCompletions = 
      if (myPosition != -1) List(CheckContainsAtPosition(oraclePos))
      else List(CheckDoesNotContain(List(expectedSnippet)))
    
    innerTestFunction("main/scala/generalized/nongenerics/", 1, exampleCompletions)
  }
  
  @Before
  def forwardIterator {
    generalizedPositionsCurrentList = generalizedPositionsIterator.next
  }
  
	@After
	def resetRunStatistics = resetRunStatisticsStatic 
  
} 

// regular, nocorpus, zero, modified

object InSynthBenchmarkCompletionParametrizedTestsAllLoader {
  
  val approved = List(true, false, false, false)
  
	val testProjectSetup = new CompletionUtility(InSynthBenchmarkCompletionTests)
	
//  val statsFileNames = List(
//    "insynth_statistics_generalized_regular.txt",
//    "insynth_statistics_generalized_nocorpus.txt",
//    "insynth_statistics_generalized_zero.txt",
//    "insynth_statistics_generalized_modified.txt"
//  )
  val statsCSVFileNames = List(
    "data_regular.csv",
    "data_nocorpus.csv",
    "data_zero.csv",
    "data_modified.csv"
  )
  for (statsFileName <- statsCSVFileNames) {
	  val file = new File(statsFileName)
	  file.delete
	  file.createNewFile    
  }
		  
  val generalizedPositions = List(
    // regular
    List(
	    0, 0, 0, 0, 0,  0, 0, 0, 3, 0, 1, 3, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 
	    3, 1, 0, 0, 0, 0, 0, 0, 1, 0, 7, 8, 0, 0, 0, 0, 0, 5, 1, 0, 0, 0, 0, 0, 1
	  ) ++ List.fill(18)(-1),
	  // zero
	  List(
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
	  ),
	  // no corpus
	  List(
	    0, 0, 0, 0, 0,
	    0, 0, 1, 7, 1, // 10
	    2, 1, 1, 1, 1,
	    0, 0, 0, 0, 1, // 20
	    0, 4, 5, 3, 1,
	    1, 0, 0, 0, 0, // 30
	    5, 1, 0, 0, -1,
	    0, 0, 0, 1, 1, // 40
	    -1, 1, 0, 0, 0,
	    0, 0, 2
	  ),
	  // modified
	  List(
	    0, 0, 0, 0, 0,
	    0, 0, 1, 7, 1, // 10
	    2, 1, 1, 1, 1,
	    0, 0, 0, 0, 1, // 20
	    0, 4, 5, 3, 1,
	    1, 0, 0, 0, 0, // 30
	    5, 1, 0, 0, -1,
	    0, 0, 0, 1, 1, // 40
	    -1, 1, 0, 0, 0,
	    0, 0, 2
	  )
  )
	var generalizedPositionsIterator = generalizedPositions.iterator
	var generalizedPositionsCurrentList: List[Int] = _
  
	// data for csv
	val firstRowString = "Filename, position, #declarations, Engine (avg), Reconstruction (avg)"
  var tableFilenames: MutableList[MutableList[String]] = MutableList.fill(4)(MutableList.empty)
  var tableDeclarations: MutableList[MutableList[Int]] = MutableList.fill(4)(MutableList.empty)
  var tableEngineTimes: MutableList[MutableList[Float]] = MutableList.fill(4)(MutableList.empty)
  var tableReconstructionTimes: MutableList[MutableList[Float]] = MutableList.fill(4)(MutableList.empty)
	
  def resetRunStatisticsStatic = {
	  import ReconstructorStatistics._
	  import InSynthStatistics._
    
    resetLastRun
    resetStatistics
	}
    
  @BeforeClass
  def setup() {    
		InSynthBenchmarkCompletionParametrizedTests.setup
		
		// run "warming-up" tests
		val fileName = "FileInputStreamStringname"
    testProjectSetup.checkCompletions("main/scala/generalized/nongenerics/" + fileName + ".scala")(Nil)
    testProjectSetup.checkCompletions("main/scala/javaapi/nongenerics/" + fileName + ".scala")(Nil)
    
    resetRunStatisticsStatic
    
    RConfig.numberOfSnippetsForExtractor = 100
  }
	
	@AfterClass
	def writeCSVTable = {
		import Utility._
	
		for (csvFile <- statsCSVFileNames)
			appendToFile(csvFile, firstRowString)
			
		for (list <- List(tableFilenames, tableDeclarations, tableEngineTimes, tableReconstructionTimes, generalizedPositions);
				innerList <- list) {
			assertEquals(parameters.size, innerList.size)
		}
		
		for (ind <- 0 to 3; if (approved(ind))) {
			for( ((((fileName, numberDec), engine), reconstruction), position) <- tableFilenames zip tableDeclarations zip
		    tableEngineTimes zip tableReconstructionTimes zip generalizedPositions) {		  
				appendToFile(statsCSVFileNames(ind), fileName(ind).dropRight(6) + "," + (position(ind) + 1) + ", "
				    + numberDec(ind) + ", " + engine(ind) + ", " + reconstruction(ind))
			}
		}
		
		// weight loader		
		IConfig.defaultWeightsLoader = RegularWeightsLoader
	}
  
	@Parameters
	def parameters: ju.Collection[Array[Object]] = {
	  val list = new ju.ArrayList[Array[Object]]
	  	  
	  list add Array( "FileInputStreamStringname" , "new FileInputStream(\"?\")" ) // 0
	  list add Array( "FileOutputStreamFilefile" , "new FileOutputStream(tempFile)" )
	  list add Array( "FileStringname" , "new File(\"?\")" )
	  list add Array( "FileWriterFilefile" , "new FileWriter(outputFile)" )
	  list add Array( "FileWriterLPT1" , "new FileWriter(\"?\")" )
	  list add Array( "GridBagConstraints" , "new GridBagConstraints" )
	  list add Array( "GroupLayoutContainerhost" , "new GroupLayout(panel)" )
	  list add Array( "ImageIconStringfilename" , "new ImageIcon(\"?\")" )
	  list add Array( "InputStreamReaderInputStreamin" , "new InputStreamReader(System in)" )
	  list add Array( "JButtonStringtext" , "new JButton(\"?\")" )
	  list add Array( "JCheckBoxStringtext" , "new JCheckBox(\"?\")" ) // 10
	  list add Array( "JFormattedTextFieldAbstractFormatterFactoryfactoryObjectcurrentValue" , "new JFormattedTextField(factory)" )
	  list add Array( "JFormattedTextFieldFormatterformatter" , "new MaskFormatter(\"?\")" )
	  list add Array( "JTableObjectnameObjectdata" , "new JTable(rows, columns)" )
	  list add Array( "JToggleButtonStringtext" , "new JFrame(\"?\")" )
	  list add Array( "JTree" , "new JTree" ) // 15
	  list add Array( "JWindow" , "new JWindow" )
	  list add Array( "ObjectInputStreamInputStreamin" , "new ObjectInputStream(fis)" )
	  list add Array( "ObjectOutputStreamOutputStreamout" , "new ObjectOutputStream(fos)" )
	  list add Array( "PipedReaderPipedWritersrc" , "new PipedReader(pw)" )
	  list add Array( "PipedWriter" , "new PipedWriter" ) // 20
	  list add Array( "Pointintxinty" , "new Point(0, 0)" )
	  list add Array( "PrintStreamOutputStreamout" , "new PrintStream(fout)" )
	  list add Array( "PrintWriterBufferedWriterbooleanautoFlush" , "new PrintWriter(bw, false)" )
	  list add Array( "SequenceInputStreamInputStreams1InputStreams2" , "new SequenceInputStream(f1, f2)" )
	  list add Array( "ServerSocketintport" , "new ServerSocket(port)" )
	  list add Array( "StreamTokenizerFileReaderfileReader" , "new StreamTokenizer(br)" )
	  list add Array( "StringReaderStrings" , "new StringReader(\"?\")" )
	  list add Array( "TimerintvalueActionListeneract" , "new Timer(0, actionListener)" )
	  list add Array( "TransferHandlerStringproperty" , "new TransferHandler(s)" )
	  list add Array( "URLStringspecthrowsMalformedURLException" , "new URL(\"?\") openConnection" ) // 30
	  
	  // missing 4 tests
	  list add Array( "FileReaderFilefile" , "new FileReader(inputFile)" )
	  list add Array( "GridBagLayout" , "new GridBagLayout" )
	  list add Array( "JViewport" , "new JViewport" )
	  list add Array( "LineNumberReaderReaderin" , "new LineNumberReader(new InputStreamReader(System in))" )
	  // tests from InSynthBenchmarkCompletionTests
	  list add Array( "AWTPermissionStringname" , "new AWTPermission(\"?\")" ) // 35
	  // cannot find, not even in 100 snippets
		//list add Array( "BoxLayoutContainertargetintaxis" , "new BoxLayout(container, BoxLayout.Y_AXIS)" )
		list add Array( "BufferedInputStreamFileInputStream" , "new BufferedInputStream(fis)" )
		list add Array( "BufferedOutputStream" , "new BufferedOutputStream(file)" )
		list add Array( "BufferedReaderFileReaderfileReader" , "new BufferedReader(fr)" )
		list add Array( "BufferedReaderInputStreamReader" , "new BufferedReader(isr)" ) // 40
		list add Array( "BufferedReaderReaderin" , "new BufferedReader(new InputStreamReader(url openStream))" )
		list add Array( "ByteArrayOutputStreamintsize" , "new ByteArrayOutputStream(0)" )
		list add Array( "DatagramSocket" , "new DatagramSocket" )
		list add Array( "DataInputStreamFileInputStreamfileInputStream" , "new DataInputStream(fis)" )
		list add Array( "DataOutputStreamFileOutputStreamfileOutputStream" , "new DataOutputStream(fos)" ) // 45
		list add Array( "DefaultBoundedRangeModel" , "new DefaultBoundedRangeModel" )
		list add Array( "DisplayModeintwidthintheightintbitDepthintrefreshRate" , "gs getDisplayMode" )
		list add Array( "FileInputStreamFileDescriptorfdObj" , "new FileInputStream(aFile)" )
	  
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
	  // true needed but InSynth constants include only false
	  list add Array( "JCheckBoxStringtextbooleanselected" , "new JCheckBox(\"?\", false)" )
	  list add Array( "JTextAreaDocumentdocument" , "new JTextArea(document)" )
	  list add Array( "JTextAreaStringtext" , "new JTextArea(\"?\")" )
	  list add Array( "OverlayLayoutContainertarget" , "new OverlayLayout(panel)" )
	  list add Array( "PrintStreamFilefile" , "new PrintStream(file)" )
	  list add Array( "SocketInetAddressaddressintportthrowsIOException" , "InetAddress.getByName(\"?\")" )
	  list add Array( "StreamTokenizerReaderr" , "new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)))" )
	  
	  list
	}

}