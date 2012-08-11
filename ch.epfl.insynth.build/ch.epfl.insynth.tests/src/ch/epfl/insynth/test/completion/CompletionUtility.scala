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

class CompletionUtility(projectSetup: TestProjectSetup) {
  import projectSetup._

  import org.eclipse.core.runtime.IProgressMonitor
  import org.eclipse.jface.text.IDocument
  import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext
  
  private def withCompletions(path2source: String): List[List[Output]] = {
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
      val positions = SDTTestUtils.positionsOf(contents, " /*!*/")
      val content = unit.getContents.mkString

      assertTrue("cotent= " + content +  " positions.size=" + positions.size, positions.size > 0)
      
      val completion = new ScalaCompletions
      
      (List[List[Output]]() /: (0 until positions.size) ) {
        (list, i) => {
          val pos = positions(i)

      		val position = new scala.tools.nsc.util.OffsetPosition(src, pos)
          var wordRegion = ScalaWordFinder.findWord(content, position.point)

          val innerFinderResults = InnerFinder(unit, pos).getOrElse( List.empty )
                    
          list :+ innerFinderResults
        }
      }
      
//      for (i <- 0 until positions.size) {
//        val pos = positions(i)
//
//        val position = new scala.tools.nsc.util.OffsetPosition(src, pos)
//        var wordRegion = ScalaWordFinder.findWord(content, position.point)
//
//        import scala.collection.JavaConverters._
//        
//        body(i, position, InnerFinder(unit, pos).asScala.toList : List[ICompletionProposal])
//      }
    } (  )
  }
  
  type Checker = List[Output]=>Unit
  
  def checkCompletions(path2source: String)(expectedProperties: List[Checker]*) {

    for ( 
        (calculatedList, expectedList) <- (withCompletions(path2source) zip expectedProperties);
		expected <- expectedList
        ) {
      expected(calculatedList)
    }
  }
  
  case class CheckContains(expectedCompletions: List[String]) extends Checker {

    def apply(completions: List[Output]) = {
      val calculatedStrings = completions.map { _.getSnippet }
      for (expected <- expectedCompletions) {
        val contains = calculatedStrings contains expected
        assertTrue("Expected snippet: " + expected + ", calculated snippets: " + calculatedStrings.mkString(", "), contains)
      }
    }
  }  
  
  case class CheckNumberOfCompletions(expectedNumber: Int) extends Checker {

    def apply(completions: List[Output]) = {
      assertEquals(expectedNumber, completions.size)
    }
  }  
  
  case class CheckRegexContains(expectedCompletions: List[String]) extends Checker {
    def apply(completions: List[Output]) = {
      val calculatedStrings = completions.map { _.getSnippet }
      for (expected <- expectedCompletions) {
        val contains = (false /: calculatedStrings) {
          (result, string) => result || (string matches expected)
        }
        assertTrue("Expected regex of the snippet: " + expected + ", calculated snippets: " + calculatedStrings.mkString(", "), contains)
      }
    }
  }

}