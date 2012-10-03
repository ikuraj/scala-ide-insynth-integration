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
import leon.Globals
import scala.tools.eclipse.javaelements.ScalaCompilationUnit
import ch.epfl.insynth.leon.CodeExtractionForInSynth
import scala.tools.eclipse.ScalaPresentationCompiler
import scala.tools.nsc.interactive.Response

class CodeExtractionTest {
	val testProjectSetup = new CompletionUtility(LeonProjectSetup)
	
	import testProjectSetup._
	import LeonProjectSetup._
		
	@Test
	def run() {
	  
		// get our compilation unit
	  
		val unit = compilationUnit("extraction/List.scala").asInstanceOf[ScalaCompilationUnit]
		
		project.withSourceFile(unit) { (src, compiler) =>
		 	val codeExtraction = new CodeExtractionForInSynth(compiler, null)
       	
		 	// do a compiler reload before checking for problems
		 	val dummy = new Response[Unit]
			compiler.askReload(List(src), dummy)
			dummy.get   
		 	
		 	compiler.getUnitOf(src) match {
		 	  case Some(neededUnit) =>
		      val prog: leon.purescala.Definitions.Program = codeExtraction.extractCode(neededUnit.asInstanceOf[codeExtraction.global.CompilationUnit])
		      
		      println("Extracted program for " + unit + ": ")
		      println(prog)
		      println("Extraction complete. Now terminating the compiler process.")
		    case None => fail("Could not get compilation unit") 
		 	}
		 	
        
		} ()
		
	}

}