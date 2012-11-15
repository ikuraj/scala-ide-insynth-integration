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
import scala.collection.mutable.{ LinkedList => MutableList, Map => MutableMap }
import org.junit.AfterClass
import ch.epfl.insynth.{ Config => IConfig }
import ch.epfl.insynth.reconstruction.{ Config => RConfig }
import ch.epfl.insynth.loader.{ ZeroWeightsLoader, RegularWeightsLoader, NoCorpusWeightsLoader }
import org.junit.Before
import ch.epfl.insynth.loader.RegularWeightsLoaderModified
import ch.epfl.insynth.env.Declaration

import scala.text.Document
import scala.text.Document.empty
import ch.epfl.scala.{ trees => Scala }
import ch.epfl.insynth.print._

@RunWith(value = classOf[Parameterized])
class TransformForImogen(fileName: String, expectedSnippet: String) {
    
	import InSynthBenchmarkCompletionParametrizedTests.testProjectSetup._
	import InSynthBenchmarkCompletionParametrizedTestsAllLoader._
					  		
  val store = Activator.getDefault.getPreferenceStore
  import InSynthConstants._
	  
  @Test
	def innerTestFunction = {    
    
    checkCompletions("main/scala/generalized/nongenerics/" + fileName + ".scala")(
        List(CheckDoesNotContain(List("asdasdazxsd")))
    )
            
  	import InSynthStatistics._
  	import ReconstructorStatistics._
		import Utility._
		
    println("lastAllDeclarations.size: " + lastAllDeclarations.size)
  	
  	val formula = processDeclarations(lastAllDeclarations, savedDesiredType)
    
  	val imFileName = "imogen/im_" + fileName + ".txt"
	  val file = new File(imFileName)
	  file.delete
	  file.createNewFile
	  
	  appendToFile(imFileName, formula)
	}   
	
	def processDeclarations( declarations: List[Declaration], desiredType: Scala.ScalaType ) = {
	  import FormatHelpers._
	  import Document._
  
    var cache: MutableMap[Scala.ScalaType, Document] = MutableMap.empty
//		    
//    def transform(scalaType: Scala.ScalaType): Document =
//	    scalaType match {
//	    	case Scala.Method(null, paramss, returnType) =>
//	    	  transform(Scala.Function( paramss.flatten, returnType ))
//	    	case Scala.Method(receiver, paramss, returnType) =>
//	    	  transform(Scala.Function( receiver +: paramss.flatten, returnType ))
//			  case Scala.Function(params, returnType) =>
//			    paren(seqToDoc(params, empty :/: "=>" :/: empty, { param: Scala.ScalaType => transform(param) } )) :/:
//			    "=>" :/: transform(returnType)	    		
//			  case Scala.Const(name) => name	    		
//			  case Scala.Instance(name, list) if cache.contains(scalaType) =>
//			  	cache(scalaType)
//			  case Scala.Instance(name, list) =>
//			    val resDoc = name :: sqBrackets( seqToDoc(list, ",", transform(_:Scala.ScalaType) ) )
//			    cache += (scalaType -> resDoc)
//			    resDoc
//		  	case Scala.Inheritance(subtype, supertype) =>
//		  	  transform(subtype) :/: "=>" :/: transform(supertype)
////				  	case Scala.Variable(name) => name 
////				  	case null => "null"
//			  case _ =>
//			    println("Type not recognized: " + scalaType)
//			    throw new RuntimeException
//  		}
    
    class VariableGenerator {
	    private var counter = 0
		// returns a string for a fresh variable name
  		def getFreshVariableName = "o" + { counter+=1; counter }
	  }
	  
	  val vg = new VariableGenerator
	  
    var nameCache: MutableMap[String, Document] = MutableMap.empty
    
    def transform(scalaType: Scala.ScalaType): Document =
	    scalaType match {
	    	case Scala.Method(null, paramss, returnType) =>
	    	  transform(Scala.Function( paramss.flatten, returnType ))
	    	case Scala.Method(receiver, paramss, returnType) =>
	    	  transform(Scala.Function( receiver +: paramss.flatten, returnType ))
			  case Scala.Function(Nil, returnType) =>
			    transform(returnType)
			  case Scala.Function(params, returnType) =>
			    paren(seqToDoc(params, " => ", { param: Scala.ScalaType => transform(param) } )) :/:
			    "=>" :/: transform(returnType)	    		
			  case Scala.Const(name) => {
			    if (nameCache.contains(name))
		    		nameCache(name)
	    		else {
	    		  val newName: Document = vg.getFreshVariableName
	    		  nameCache += ( name -> newName )
	    		  newName
	    		}	    		  
			  }
			  case Scala.Instance(name, list) if cache.contains(scalaType) =>
			  	cache(scalaType)
			  case Scala.Instance(name, list) =>
			    val resDoc : Document = vg.getFreshVariableName
			    cache += (scalaType -> resDoc)
			    resDoc
		  	case Scala.Inheritance(subtype, supertype) =>
		  	  transform(subtype) :/: "=>" :/: transform(supertype)
//				  	case Scala.Variable(name) => name 
//				  	case null => "null"
			  case _ =>
			    println("Type not recognized: " + scalaType)
			    throw new RuntimeException
  		}
	  
	  val decDocuments = 
		  for (declaration <- declarations) yield {
		    val scalaType = declaration.scalaType
		    
//		    println("going to print")
	    		Scala.FormatScalaType(scalaType).toString
//		    println("printed")
		    
		    transform(scalaType)
		  }
	    	
	  
	  (
      Formatable(seqToDoc(decDocuments :+ transform(desiredType), "\n=>\n", { x: Document => x } ))
	  ).toString	  
	}
  
} 

// regular, nocorpus, zero, modified

object TransformForImogen {   
  
	val testProjectSetup = new CompletionUtility(InSynthBenchmarkCompletionTests)
	    
  def resetRunStatisticsStatic = {
	  import ReconstructorStatistics._
	  import InSynthStatistics._
    
    resetLastRun
    resetStatistics
	}
    
  @BeforeClass
  def setup() {		
		// tests are made according to the clean code style
		Activator.getDefault.getPreferenceStore.
			setValue(InSynthConstants.CodeStyleParenthesesPropertyString, InSynthConstants.CodeStyleParenthesesClean)
			   			    		
	  val store = Activator.getDefault.getPreferenceStore
	  import InSynthConstants._
    store.setValue(OfferedSnippetsPropertyString, 10)
    RConfig.numberOfSnippetsForExtractor = 10
		store.setValue(MaximumTimePropertyString, 500) 
  			
		// run "warming-up" tests
		val fileName = "FileInputStreamStringname"
    testProjectSetup.checkCompletions("main/scala/generalized/nongenerics/" + fileName + ".scala")(Nil)
    testProjectSetup.checkCompletions("main/scala/javaapi/nongenerics/" + fileName + ".scala")(Nil)
    
    resetRunStatisticsStatic
  }
	
	@AfterClass
	def writeCSVTable = {
		import Utility._
			
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
	  list add Array( "ByteArrayInputStream" , "new DataInputStream(new ByteArrayInputStream(\"?\".getBytes()))" ) // 50
	  list add Array( "ByteArrayInputStreambytebufintoffsetintlength" , "new ByteArrayInputStream(b, 0, 0)" )
	  list add Array( "CharArrayReadercharbuf" , "new CharArrayReader(outStream.toCharArray())" )
	  list add Array( "DatagramPacketbytebufferintbufferLength" , "new DatagramPacket(buffer, buffer.length)" )
	  list add Array( "DatagramPacketbytebufintlengthInetAddressaddressintport" , "new DatagramPacket(buffer, buffer.length, ia, 0)" )
	  list add Array( "FileWriterStringfileNamebooleanappend" , "new BufferedWriter(new FileWriter(\"?\", true))" )  // 55
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