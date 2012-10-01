package ch.epfl.insynth.test.leon

import scala.tools.nsc.{Global,Settings=>NSCSettings,SubComponent,CompilerCommand}
import leon.{ Main => LeonMain }
import scala.tools.nsc.MainGenericRunner
import leon.DefaultReporter

object Main {

	val SCALACLASSPATH = "/home/ivcha/git/leon-2.0/unmanaged/z3-64.jar:/home/ivcha/git/leon-2.0/target/scala-2.9.1-1/classes:/home/ivcha/git/leon-2.0/library/target/scala-2.9.1-1/classes:/home/ivcha/.sbt/boot/scala-2.9.1-1/lib/scala-library.jar:/home/ivcha/.sbt/boot/scala-2.9.1-1/lib/scala-compiler.jar"
	  
  def main(args: Array[String]): Unit = {
    
	   val settings = new NSCSettings
    settings.classpath.tryToSet(List(SCALACLASSPATH))
    
    println(settings.classpath.value)
    
    //assertTrue(false)
//	  
	  LeonMain.run(Array("ListGenerated_0.scala"), new DefaultReporter, Some(List(SCALACLASSPATH)))
  }

}