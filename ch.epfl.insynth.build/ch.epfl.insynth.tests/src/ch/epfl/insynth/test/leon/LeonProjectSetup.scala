package ch.epfl.insynth.test.leon

import scala.tools.eclipse.testsetup.TestProjectSetup
import ch.epfl.insynth.test.completion.CompletionUtility
import org.junit.Assert._
import org.junit.Test
import org.junit.BeforeClass
import org.junit.Ignore
import leon.{ Main => LeonMain }
import scala.tools.nsc.MainGenericRunner
import leon.DefaultReporter

import scala.tools.nsc.{Global,Settings=>NSCSettings,SubComponent,CompilerCommand}

object LeonProjectSetup extends TestProjectSetup("leon", bundleName = "ch.epfl.insynth.tests")

class LeonProjectSetup {
	val testProjectSetup = new CompletionUtility(LeonProjectSetup)
	
	import testProjectSetup._

	val SCALACLASSPATH = "/home/ivcha/git/leon-2.0/unmanaged/z3-64.jar:/home/ivcha/git/leon-2.0/target/scala-2.9.1-1/classes:/home/ivcha/git/leon-2.0/library/target/scala-2.9.1-1/classes:/home/ivcha/.sbt/boot/scala-2.9.1-1/lib/scala-library.jar:/home/ivcha/.sbt/boot/scala-2.9.1-1/lib/scala-compiler.jar"
	  
	  val classpathArray = Array(
      "/home/ivcha/git/leon-2.0/target/scala-2.9.1-1/classes",
      "/home/ivcha/git/leon-2.0/library/target/scala-2.9.1-1/classes",
      "/home/ivcha/git/leon-2.0/unmanaged/z3-64.jar",
      "/home/ivcha/.sbt/boot/scala-2.9.1-1/lib/scala-library.jar",
      "/home/ivcha/.sbt/boot/scala-2.9.1-1/lib/scala-compiler.jar"
		)
	
	@Test
	def run() {
	  val numberOfFiles = withCompletions("list/List.scala")(List("sizeTail(tail, acc+1)"), 0)("ListGenerated_%d.scala")
	  

	  assertTrue(numberOfFiles > 0)
	  
//	   val settings = new NSCSettings
//    settings.classpath.tryToSet(List(SCALACLASSPATH))    
//    println(settings.classpath.value)
//    
//    assertTrue(false)  
	  for (fileIndex <- 0 until numberOfFiles) {
	  	LeonMain.run(Array("ListGenerated_%d.scala" format fileIndex/*, "--timeout=3", "--noLuckyTests"*/), new DefaultReporter, Some(List(SCALACLASSPATH)))
	  	println("=========================================")
	  }
	  
	  //withCompletions("RedBlackTree.scala")(List("Node(Red(),Node(Black(),a,xV,b),yV,Node(Black(),c,zV,d))"), 0)("RedBlackTreeGenerated_%d.scala")
	}

}