package ch.epfl.insynth.core.completion

import scala.collection.JavaConverters._
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.jface.text.contentassist.ICompletionProposal
import org.eclipse.jface.text.contentassist.IContextInformation
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext
import scala.tools.eclipse.javaelements.ScalaCompilationUnit
import org.eclipse.jface.text.contentassist.CompletionProposal
import java.io.FileWriter
import java.io.BufferedWriter
import scala.tools.nsc.interactive.Global
import scala.tools.nsc.util.SourceFile
import java.io.BufferedReader
import java.io.InputStreamReader
import scala.tools.nsc.util.Position
import java.io.OutputStreamWriter
import ch.epfl.insynth.InSynth
import ch.epfl.insynth.util.TreePrinter
import ch.epfl.insynth.Config
import ch.epfl.insynth.env.InitialEnvironmentBuilder
import ch.epfl.insynth.env.Declaration
import ch.epfl.insynth.reconstruction.Output
import ch.epfl.insynth.reconstruction.Reconstructor
import ch.epfl.insynth.core.Activator
import scala.tools.eclipse.logging.HasLogger

/* 
TODO:
4. predef.exit(1)

*/


object InnerFinder extends ((ScalaCompilationUnit, Int) => java.util.List[ICompletionProposal]) with HasLogger {
  def apply(scu: ScalaCompilationUnit, position: Int): java.util.List[ICompletionProposal] = {
    import java.util.Collections.{ emptyList => javaEmptyList }

    var oldContent: Array[Char] = scu.getContents

    scu.withSourceFile {
      (sourceFile, compiler) =>

        if (compiler != InSynthWrapper.compiler) {
          InSynthWrapper.compiler = compiler
          InSynthWrapper.insynth = new InSynth(compiler)
        } else {
          if (InSynthWrapper.insynth == null) {
            InSynthWrapper.insynth = new InSynth(compiler)
          }
        }

        //Getting builder for the first time
        if (InSynthWrapper.builder == null) {
          InSynthWrapper.builder = new InitialEnvironmentBuilder()
          if (InSynthWrapper.loadPredefs) {
            InSynthWrapper.predefDecls = InSynthWrapper.insynth.getPredefDecls()
            InSynthWrapper.builder.addDeclarations(InSynthWrapper.predefDecls)
          }
        } // else builder is already prepared

        compiler.askReload(scu, getNewContent(position, oldContent))
                
        val response = new compiler.Response[compiler.Tree]
		    compiler.askType(sourceFile, false, response)
		    val typed = response.get
		    
		    val tree = typed.fold(identity, throw _)
		    
		    println("ivaaaaan1" + tree.symbol.info)		    
		    println("ivaaaaan2" + tree.symbol.info)
		    println("ivaaaaan2" + tree.symbol.info)
		    println("ivaaaaan3" + tree.symbol.rawInfo)
		    

        var results = List.empty[Output]
        try {
          InSynthWrapper.builder.synchronized {
            val solution = InSynthWrapper.insynth.getSnippets(sourceFile.position(position), InSynthWrapper.builder)

            results = if (solution != null) Reconstructor(solution.getNodes.head) else Nil
          }
        } catch {
          case ex =>
            logger.error("InSynth synthesis failed.", ex)
            results = Nil
        }

        val sortedResults = results.sortWith((x, y) => x.getWieght.getValue < y.getWieght.getValue).map(x => x.getSnippet) // + "   w = "+x.getWieght.getValue)

        val list1: java.util.List[ICompletionProposal] = new java.util.LinkedList[ICompletionProposal]()

        var i = sortedResults.length
        sortedResults.foreach(x => {
          list1.add(new InSynthCompletitionProposal(x, i))
          i -= 1
        })

        //Make a new builder
        val pl = new PredefBuilderLoader()
        pl.start()

        list1
    }(javaEmptyList())
  }

  private def getNewContent(position: Int, oldContent: Array[Char]): Array[Char] = {
    val (cont1, cont2) = oldContent.splitAt(position)

    val mark = ";{  ;exit()};".toCharArray

    val newContent = Array.ofDim[Char](oldContent.length + mark.length)

    System.arraycopy(cont1, 0, newContent, 0, cont1.length)
    System.arraycopy(mark, 0, newContent, cont1.length, mark.length)
    System.arraycopy(cont2, 0, newContent, cont1.length + mark.length, cont2.length)

    println("New content:")
    newContent.foreach { print }
    //println()
    newContent
  }
}

class InsynthCompletionProposalComputer extends IJavaCompletionProposalComputer {
  
  def sessionStarted() {}
  def sessionEnded() {}
  def getErrorMessage() = null

  /** No context information for the moment. */
  def computeContextInformation(context: ContentAssistInvocationContext, monitor: IProgressMonitor) =
    List[IContextInformation]().asJava  
    
  /** Return InSynth completion proposals. */
  def computeCompletionProposals(context: ContentAssistInvocationContext, monitor: IProgressMonitor): java.util.List[ICompletionProposal] = {
    import java.util.Collections.{ emptyList => javaEmptyList }

    val position = context.getInvocationOffset()

    context match {
      case jc: JavaContentAssistInvocationContext => jc.getCompilationUnit match {
        case scu: ScalaCompilationUnit =>
          InnerFinder(scu, position)             
        case _ => javaEmptyList()
      }
      case _ => javaEmptyList()
    }
  }
}


object InSynthWrapper {
  
  var insynth:InSynth = null;
  var compiler:Global = null;
  
  var builder:InitialEnvironmentBuilder = null;
  var predefDecls:List[Declaration] = null;
  
  final val loadPredefs = true
  
}

class PredefBuilderLoader extends Thread {
  
  override def run(){
    InSynthWrapper.builder.synchronized{
      InSynthWrapper.builder = new InitialEnvironmentBuilder()
      if (InSynthWrapper.loadPredefs) InSynthWrapper.builder.addDeclarations(InSynthWrapper.predefDecls)
    }
  }
}
