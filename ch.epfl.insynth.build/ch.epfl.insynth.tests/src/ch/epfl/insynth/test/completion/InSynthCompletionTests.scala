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
import scala.tools.eclipse.completion.{ CompletionProposal, ScalaCompletions }
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext
import org.eclipse.jdt.core.ICompilationUnit
import org.eclipse.core.runtime.NullProgressMonitor
import java.util.logging.Logger
import org.eclipse.jface.text.contentassist.ICompletionProposal

import ch.epfl.insynth.core.completion.InsynthCompletionProposalComputer

object InSynthCompletionTests extends TestProjectSetup("insynth"/*, bundleName= "ch.epfl.insynth.tests"*/)

class InSynthCompletionTests {
  import InSynthCompletionTests._

  import org.eclipse.core.runtime.IProgressMonitor
  import org.eclipse.jface.text.IDocument
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext


  private def bla(path2source: String) {
    val unit = compilationUnit(path2source)//.asInstanceOf[ScalaCompilationUnit]
    
    assertTrue(false)
    // first, 'open' the file by telling the compiler to load it
//    project.withSourceFile(unit) { (src, compiler) =>
//      val dummy = new Response[Unit]
//      compiler.askReload(List(src), dummy)
//      dummy.get
//
//      val tree = new Response[compiler.Tree]
//      compiler.askType(src, true, tree)
//      tree.get
//
//      val contents = unit.getContents
//      // mind that the space in the marker is very important (the presentation compiler 
//      // seems to get lost when the position where completion is asked 
//      val positions = SDTTestUtils.positionsOf(contents, " /*!*/")
//      val content = unit.getContents.mkString
//
//      val completion = new ScalaCompletions
//      for (i <- 0 until positions.size) {
//        val pos = positions(i)
//
//        val position = new scala.tools.nsc.util.OffsetPosition(src, pos)
//        var wordRegion = ScalaWordFinder.findWord(content, position.point)
//
//        val insynthPropComp = new InsynthCompletionProposalComputer
//        //        val selection = mock(classOf[ISelectionProvider])
//
//        /* FIXME:
//         * I would really love to call `completion.computeCompletionProposals`, but for some unclear 
//         * reason that call is not working. Some debugging shows that the position is not right (off by one), 
//         * however, increasing the position makes the computed `wordRegion` wrong... hard to understand where 
//         * the bug is!
//        val textViewer = mock(classOf[ITextViewer])
//        when(textViewer.getSelectionProvider()).thenReturn(selection)
//        val document = mock(classOf[IDocument])
//        when(document.get()).thenReturn(content)
//        when(textViewer.getDocument()).thenReturn(document)
//        val monitor = mock(classOf[IProgressMonitor])
//        val context = new ContentAssistInvocationContext(textViewer, position.offset.get)
//        import collection.JavaConversions._
//        val completions: List[ICompletionProposal] = completion.computeCompletionProposals(context, monitor).map(_.asInstanceOf[ICompletionProposal]).toList
//        */
//
//        //body(i, position, completion.findCompletions(wordRegion)(pos + 1, unit)(src, compiler))
//        val context = new JavaContentAssistInvocationContext(unit.asInstanceOf[ICompilationUnit])
//        import scala.collection.JavaConverters._
//        
//        val list:List[ICompletionProposal] = insynthPropComp.computeCompletionProposals(context, new NullProgressMonitor).asScala.toList
//        
//        assertTrue("size" + list.size, false)
//      }
//    }()
  }

  private def withCompletions(path2source: String)(body: (Int, OffsetPosition, List[ICompletionProposal]) => Unit) {
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

      val completion = new ScalaCompletions
      for (i <- 0 until positions.size) {
        val pos = positions(i)

        val position = new scala.tools.nsc.util.OffsetPosition(src, pos)
        var wordRegion = ScalaWordFinder.findWord(content, position.point)

        //val insynthPropComp = new InsynthCompletionProposalComputer
        //        val selection = mock(classOf[ISelectionProvider])

        /* FIXME:
         * I would really love to call `completion.computeCompletionProposals`, but for some unclear 
         * reason that call is not working. Some debugging shows that the position is not right (off by one), 
         * however, increasing the position makes the computed `wordRegion` wrong... hard to understand where 
         * the bug is!
        val textViewer = mock(classOf[ITextViewer])
        when(textViewer.getSelectionProvider()).thenReturn(selection)
        val document = mock(classOf[IDocument])
        when(document.get()).thenReturn(content)
        when(textViewer.getDocument()).thenReturn(document)
        val monitor = mock(classOf[IProgressMonitor])
        val context = new ContentAssistInvocationContext(textViewer, position.offset.get)
        import collection.JavaConversions._
        val completions: List[ICompletionProposal] = completion.computeCompletionProposals(context, monitor).map(_.asInstanceOf[ICompletionProposal]).toList
        */

        //body(i, position, completion.findCompletions(wordRegion)(pos + 1, unit)(src, compiler))
        //val context = new JavaContentAssistInvocationContext(unit.asInstanceOf[ICompilationUnit])
        //import scala.collection.JavaConverters._
        
        //val list:List[ICompletionProposal] = insynthPropComp.computeCompletionProposals(context, new NullProgressMonitor).asScala.toList
        //body(i, position, list)
      }
    }()
  }

  /**
   * @param withImportProposal take in account proposal for types not imported yet
   */
  private def runTest(path2source: String)(expectedCompletions: List[String]*) {

    withCompletions(path2source) { (i, position, compl) =>

      var completions = compl
      
      assertEquals(5, completions.size)
      assertTrue(completions.mkString, false)

      // remove parens as the compiler trees' printer has been slightly modified in 2.10 
      // (and we need the test to pass for 2.9.0/-1 and 2.8.x as well).
//      val completionsNoParens: List[String] = completions.map(c => normalizeCompletion(c.display)).sorted
//      val expectedNoParens: List[String] = expectedCompletions(i).map(normalizeCompletion).sorted
//
//      println("Found following completions @ position (%d,%d):".format(position.line, position.column))
//      completionsNoParens.foreach(e => println("\t" + e))
//      println()
//
//      println("Expected completions:")
//      expectedNoParens.foreach(e => println("\t" + e))
//      println()
//
//      assertTrue("Found %d completions @ position (%d,%d), Expected %d"
//        .format(completionsNoParens.size, position.line, position.column, expectedNoParens.size),
//        completionsNoParens.size == expectedNoParens.size) // <-- checked condition
//
//      completionsNoParens.zip(expectedNoParens).foreach {
//        case (found, expected) =>
//          assertTrue("Found `%s`, expected `%s`".format(found, expected), found == expected)
//      }
    }
  }

  /**
   * Transform the given completion proposal into a string that is (hopefully)
   *  compiler-version independent.
   *
   *  Transformations are:
   *    - remove parenthesis
   *    - java.lang.String => String
   */
  private def normalizeCompletion(str: String): String = {
    str.replace("(", "").replace(")", "").replace("java.lang.String", "String")
  }

  @Test
  def test1() {
    val oraclePos10 = List("A sdfm")

    //runTest("examplepkg1/Example1.scala")(oraclePos10)
    
    //bla("examplepkg1/Example1.scala")
    bla("asdasdas")
  }
  
  @Test
  @Ignore("aaa")
  def test2() {
    
  }
}