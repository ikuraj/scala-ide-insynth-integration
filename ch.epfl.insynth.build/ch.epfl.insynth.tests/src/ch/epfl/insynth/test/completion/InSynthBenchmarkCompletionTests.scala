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

// do not depend on code style

object InSynthBenchmarkCompletionTests extends TestProjectSetup("benchmarks", bundleName = "ch.epfl.insynth.tests")

class InSynthBenchmarkCompletionTests {
	val testProjectSetup = new CompletionUtility(InSynthBenchmarkCompletionTests)
	
	import testProjectSetup._

  @Test
  def testAWTPermissionStringname() {
    val oraclePos11 = List("new AWTPermission\\(\\\"\\S+\\\"\\)")
    
    val exampleCompletions = List(CheckRegexContains(oraclePos11))
    
    checkCompletions("main/scala/generalized/nongenerics/AWTPermissionStringname.scala")(exampleCompletions)
  }
	
  @Test
  def testBoxLayoutContainertargetintaxis() {
    val oraclePos22 = List("new BoxLayout(container, BoxLayout.Y_AXIS)")
    
    val exampleCompletions = List(CheckContains(oraclePos22))
    
    checkCompletions("main/scala/javaapi/nongenerics/BoxLayoutContainertargetintaxis.scala")(exampleCompletions)
  }
	
  @Test
  def testBufferedInputStreamFileInputStream() {
    val oraclePos = List("new BufferedInputStream(fis)")
    
    val exampleCompletions = List(CheckContains(oraclePos))
    
    checkCompletions("main/scala/javaapi/nongenerics/BufferedInputStreamFileInputStream.scala")(exampleCompletions)
  }
  
  @Test
  def testBufferedOutputStream() {
    val oraclePos = List("new BufferedOutputStream(file)")
    
    val exampleCompletions = List(CheckContains(oraclePos))
    
    checkCompletions("main/scala/javaapi/nongenerics/BufferedOutputStream.scala")(exampleCompletions)
  }
  
  @Test
  def testBufferedReaderFileReaderfileReader() {
    val oraclePos = List("new BufferedReader(fr)")
    
    val exampleCompletions = List(CheckContains(oraclePos))
    
    checkCompletions("main/scala/javaapi/nongenerics/BufferedReaderFileReaderfileReader.scala")(exampleCompletions)
  }
  
  @Test
  def testBufferedReaderInputStreamReader() {
    val oraclePos = List("new BufferedReader(isr)")
    
    val exampleCompletions = List(CheckContains(oraclePos))
    
    checkCompletions("main/scala/javaapi/nongenerics/BufferedReaderInputStreamReader.scala")(exampleCompletions)
  }
  
  @Test
  def testBufferedReaderReaderin() {
    val oraclePos = List("new BufferedReader(new InputStreamReader(url.openStream()))")
    
    val exampleCompletions = List(CheckContains(oraclePos))
    
    checkCompletions("main/scala/javaapi/nongenerics/BufferedReaderReaderin.scala")(exampleCompletions)
  }
  
  @Test
  def testByteArrayOutputStreamintsize() {
    val oraclePos = List("new ByteArrayOutputStream\\(\\d+\\)")
    
    val exampleCompletions = List(CheckRegexContains(oraclePos))
    
    checkCompletions("main/scala/javaapi/nongenerics/ByteArrayOutputStreamintsize.scala")(exampleCompletions)
  }
  
  @Test
  def testDatagramSocket() {
    val oraclePos = List("new DatagramSocket(\\(\\))?")
    
    val exampleCompletions = List(CheckRegexContains(oraclePos))
    
    checkCompletions("main/scala/javaapi/nongenerics/DatagramSocket.scala")(exampleCompletions)
  }
  
  @Test
  def testDataInputStreamFileInputStreamfileInputStream() {
    val oraclePos = List("new DataInputStream(fis)")
    
    val exampleCompletions = List(CheckContains(oraclePos))
    
    checkCompletions("main/scala/javaapi/nongenerics/DataInputStreamFileInputStreamfileInputStream.scala")(exampleCompletions)
  }
  
  @Test
  def testDataOutputStreamFileOutputStreamfileOutputStream() {
    val oraclePos = List("new DataOutputStream(fos)")
    
    val exampleCompletions = List(CheckContains(oraclePos))
    
    checkCompletions("main/scala/javaapi/nongenerics/DataOutputStreamFileOutputStreamfileOutputStream.scala")(exampleCompletions)
  }
  
  @Test
  def testDefaultBoundedRangeModel() {
    val oraclePos = List("new DefaultBoundedRangeModel(\\(\\))?")
    
    val exampleCompletions = List(CheckRegexContains(oraclePos))
    
    checkCompletions("main/scala/javaapi/nongenerics/DefaultBoundedRangeModel.scala")(exampleCompletions)
  }
  
  @Test
  def testDisplayModeintwidthintheightintbitDepthintrefreshRate {
    val oraclePos = List("gs.getDisplayMode(\\(\\))?")
    
    val exampleCompletions = List(CheckRegexContains(oraclePos))
    
    checkCompletions("main/scala/javaapi/nongenerics/DisplayModeintwidthintheightintbitDepthintrefreshRate.scala")(exampleCompletions)
  }
  
  @Test
  def testFileInputStreamFileDescriptorfdObj {
    val oraclePos = List("new FileInputStream(aFile)")
    
    val exampleCompletions = List(CheckContains(oraclePos))
    
    checkCompletions("main/scala/javaapi/nongenerics/FileInputStreamFileDescriptorfdObj.scala")(exampleCompletions)
  }

}