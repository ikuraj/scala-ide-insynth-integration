package ch.epfl.insynth.test

import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit._

import ch.epfl.insynth.test.completion.InSynthBenchmarkCompletionTests
import ch.epfl.insynth.test.completion.InSynthBenchmarkCompletionParametrizedTests
import ch.epfl.insynth.test.completion.InSynthBenchmarkCompletionParametrizedLessCertainTests
import ch.epfl.insynth.core.Activator
import ch.epfl.insynth.core.preferences.InSynthConstants

import ch.epfl.insynth.core.completion.InnerFinder

import java.io._

@RunWith(classOf[Suite])
@Suite.SuiteClasses(
  Array(
    classOf[InSynthBenchmarkCompletionTests],
    classOf[InSynthBenchmarkCompletionParametrizedTests],
    classOf[InSynthBenchmarkCompletionParametrizedLessCertainTests]
  )
)
class BenchmarkTestSuite 

object BenchmarkTestSuite {
  @BeforeClass
  def setup() {    
    // set appropriate preference values
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.OfferedSnippetsPropertyString, 15)        
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.MaximumTimePropertyString, 500)
  }
  
  @AfterClass
  def postAction() {    
    val map = InnerFinder.mapFromFileToDeclarations
    println("InnerFinder has " + InnerFinder.mapFromFileToDeclarations.size)
    
//    println("declarations examples: " + map.values.head)
    
    for (((fileName, pos), declList) <- map.take(1)) {
      val path = java.nio.file.Paths.get(fileName)
//      val ext = org.apache.commons.io.FilenameUtils.getExtension(fileName)
//      val base = org.apache.commons.io.FilenameUtils.getExtension(fileName)
      val split = path.getFileName.toString.split("\\.")
      println(split.head + pos + "." + split(1))
      val oos = new PrintWriter(new FileOutputStream("decls/" + split.head + pos + "." + split(1)))
      for (dec <- declList)
        oos.println(dec.toString)
//      oos.writeInt(map.size)
//      oos.writeUTF(fileName)
//      oos.writeInt(pos)
//      oos.writeInt(declList.size)
//      for (dec <- declList)
//        oos.writeObject(dec)
      oos.close
    }
  }
  
}