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
import ch.epfl.insynth.reconstruction.Output
import ch.epfl.insynth.reconstruction.Output
import ch.epfl.insynth.core.Activator
import ch.epfl.insynth.core.preferences.InSynthConstants
import java.io.FileWriter
import java.io.PrintWriter

class CompletionUtility(projectSetup: TestProjectSetup) {
  import projectSetup._

  val mark = " /*!*/"

  import org.eclipse.core.runtime.IProgressMonitor
  import org.eclipse.jface.text.IDocument
  import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext
  	
  def withCompletions(path2source: String)(inserts: List[String], index: Int)
  	(filename: String): Int = {
    val unit = compilationUnit(path2source).asInstanceOf[ScalaCompilationUnit]
    
    // first, 'open' the file by telling the compiler to load it
    project.withSourceFile(unit) { (src, compiler) =>
      val dummy = new Response[Unit]
      compiler.askReload(List(src), dummy)
      dummy.get

      val tree = new Response[compiler.Tree]
      compiler.askType(src, true, tree)
      tree.get

      val contents = unit.getContents
      // mind that the space in the marker is very important (the presentation compiler 
      // seems to get lost when the position where completion is asked 
      val positions = SDTTestUtils.positionsOf(contents, mark)
      val contentString = unit.getContents.mkString

      assertTrue("positions.size=" + positions.size, positions.size > 0)
           
      val pos = positions(index)
    	val incPos = pos + 1

      val innerFinderResults = InnerFinder(unit, pos).getOrElse( List.empty )
            
      println( innerFinderResults map { _.getSnippet } mkString "," )
      
      assertTrue("innerFinderResults.length > 0", innerFinderResults.length > 0)
      
      val content = contentString.toCharArray
      
      // NOTE temporary
      //for ((result, ind) <- innerFinderResults filterNot { _.getSnippet.contains("this") } zip (0 until innerFinderResults.size)) {
      for ((result, ind) <- innerFinderResults zip (0 until innerFinderResults.size)) {
                
	      var copyPosition = 0
	      var copyToPosition = 0
	      println("old content is" + content)
	      
		    var newContent = Array.ofDim[Char](content.length +
		        ( (0 /: (inserts diff List(inserts(index))) ) { (sum, insert) => sum + insert.length } + result.getSnippet.length 
		        - positions.length * mark.length )
	        )
        println("newContent.length: " + newContent.length)
        println("content.length: " + content.length)
		        
        assertEquals(positions.length, inserts.length)	        
      
		    for ((position, insert) <- positions map { _ + 1 } zip inserts) {
		    	println("current content copy for position: " + position)
		    	
		    	val difference = position - copyPosition
		    		    	
//		      println("copyposition[]: " + content(copyPosition) + content(copyPosition+1) + content(copyPosition+2))
//		      println("copyToposition[]: " + newContent(copyToPosition) + newContent(copyToPosition+1) + newContent(copyToPosition+2))
		    	
        	System.arraycopy(content, copyPosition, newContent, copyToPosition, difference)
        	copyPosition += difference
        	copyToPosition += difference
        	
        	println("copyPosition: " + copyPosition + " copyToPosition: " + copyToPosition )
	      
        	
		      position match {
		        case `incPos` => 
		        	System.arraycopy(result.getSnippet.toCharArray, 0, newContent, copyToPosition, result.getSnippet.length)
		        	copyPosition += mark.length
		        	copyToPosition += result.getSnippet.length
		        case _ =>
		        	System.arraycopy(insert.toCharArray, 0, newContent, copyToPosition, insert.length)
		        	copyPosition += mark.length
		        	copyToPosition += insert.length		        			        	
		      }
		    	println("copyPosition: " + copyPosition + " copyToPosition: " + copyToPosition )
//		      println("copyposition[]: " + content(copyPosition) + content(copyPosition+1) + content(copyPosition+2))
//		      println("copyToposition[]: " + newContent(copyToPosition) + newContent(copyToPosition+1) + newContent(copyToPosition+2))
		    }    
	      
        println("final copyPosition: " + copyPosition + " copyToPosition: " + copyToPosition + "newContent.length - copyToPosition: " + (newContent.length - copyToPosition) )
	      
      	System.arraycopy(content, copyPosition, newContent, copyToPosition, newContent.length - copyToPosition)
      	      
	      import Utility._
	      
	      println("new content is" + newContent)
	      
	      writeToFile(filename format ind, newContent)
      }
      
      innerFinderResults.size
    } ( 0 )
  }

  object Utility {

    def using[A <: { def close(): Unit }, B](param: A)(f: A => B): B =
      try { f(param) } finally { param.close() }

    def writeToFile(fileName: String, data: Array[Char]) =
      using(new FileWriter(fileName)) {
        fileWriter => fileWriter.write(data)
      }
    
    def writeToFile(fileName: String, data: String) =
      using(new FileWriter(fileName)) {
        fileWriter => fileWriter.write(data)
      }

    def appendToFile(fileName: String, textData: String) =
      using(new FileWriter(fileName, true)) {
        fileWriter =>
          using(new PrintWriter(fileWriter)) {
            printWriter => printWriter.println(textData)
          }
      }

  }
}