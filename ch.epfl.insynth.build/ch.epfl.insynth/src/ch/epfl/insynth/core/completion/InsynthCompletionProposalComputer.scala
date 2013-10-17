package ch.epfl.insynth.core
package completion

import java.io._

import scala.collection.JavaConverters._

import org.eclipse.jdt.ui.text.java._
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.jface.text.contentassist.{ ICompletionProposal, IContextInformation }

import scala.tools.eclipse.javaelements.ScalaCompilationUnit
import scala.tools.nsc.interactive.Global

import insynth.engine.InitialEnvironmentBuilder
import insynth.load.Declaration
import insynth.util.logging.HasLogger

import ch.epfl.insynth.core.preferences.InSynthConstants
import ch.epfl.insynth.InSynth
import ch.epfl.insynth.reconstruction._
import ch.epfl.insynth.reconstruction.Output
import ch.epfl.insynth.reconstruction.codegen._

/* 
TODO:
4. predef.exit(1)

*/

object InnerFinder extends ((ScalaCompilationUnit, Int) => Option[List[Output]]) with HasLogger {
    
  var predefBuildLoader: PredefBuilderLoader = new PredefBuilderLoader()
  
  def apply(scu: ScalaCompilationUnit, position: Int): Option[List[Output]] = {

    if (predefBuildLoader.isAlive())
      predefBuildLoader.join()
    
    var oldContent: Array[Char] = scu.getContents

    scu.withSourceFile {
      (sourceFile, compiler) =>
        
        info("InSynth working on source file: " + sourceFile.path)
        val codegen = getSourceCodeGenerator

        if (compiler != InSynthWrapper.compiler) {
          InSynthWrapper.compiler = compiler
          InSynthWrapper.insynth = new InSynth(compiler, codegen)
        } else {
          if (InSynthWrapper.insynth == null) {
            InSynthWrapper.insynth = new InSynth(compiler, codegen)
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

        //Make a new builder
        predefBuildLoader = new PredefBuilderLoader()
        
        try {
          InSynthWrapper.builder.synchronized {                       	
            InSynthWrapper.insynth
            	.getSnippets(sourceFile.position(position), InSynthWrapper.builder) map
            	{ _.sortWith((x, y) => x.getWieght< y.getWieght) } // + "   w = "+x.getWieght.getValue)
          }
        } catch {
          case ex: Throwable  =>
            error("InSynth synthesis failed." + ex)
            None
        } finally {          
        	predefBuildLoader.start()
        }
    } ( None )
  }

  private def getNewContent(position: Int, oldContent: Array[Char]): Array[Char] = {
    val (cont1, cont2) = oldContent.splitAt(position)

    val mark = ";{  ;exit()};".toCharArray

    val newContent = Array.ofDim[Char](oldContent.length + mark.length)

    System.arraycopy(cont1, 0, newContent, 0, cont1.length)
    System.arraycopy(mark, 0, newContent, cont1.length, mark.length)
    System.arraycopy(cont2, 0, newContent, cont1.length + mark.length, cont2.length)

//    println("New content:")
//    newContent.foreach { print }
    //println()
    newContent
  }
  
  private def getSourceCodeGenerator() = {    
    	// import InSynth constants for convenience
    	import InSynthConstants._
    	
    	// apply transformer?
    	val applyTransformerFlag = Activator.getDefault.getPreferenceStore.getBoolean(
  	    CodeStyleApplyOmittingPropertyString)
    	// simple application name transformer?
    	val simpleApplicationNameTransformerFlag = Activator.getDefault.getPreferenceStore.
    		getBoolean( CodeStyleSimpleApplicationNameTransformPropertyString )
    		
    	// choose a code generator object according to the code style property
    	Activator.getDefault.getPreferenceStore.getString(
				CodeStyleParenthesesPropertyString
			) match {
    	  case `CodeStyleParenthesesClean` if applyTransformerFlag && simpleApplicationNameTransformerFlag =>
    	    new CleanCodeGenerator with ApplyTransfromer with SimpleApplicationNamesTransfromer
    	  case `CodeStyleParenthesesClassic` if applyTransformerFlag && simpleApplicationNameTransformerFlag =>
    	    new ClassicStyleCodeGenerator with ApplyTransfromer with SimpleApplicationNamesTransfromer
    	  case `CodeStyleParenthesesClean` if simpleApplicationNameTransformerFlag => new CleanCodeGenerator with SimpleApplicationNamesTransfromer
    	  case `CodeStyleParenthesesClassic` if simpleApplicationNameTransformerFlag => new ClassicStyleCodeGenerator with SimpleApplicationNamesTransfromer
    	  case `CodeStyleParenthesesClean` if applyTransformerFlag => new CleanCodeGenerator with ApplyTransfromer
    	  case `CodeStyleParenthesesClassic` if applyTransformerFlag => new ClassicStyleCodeGenerator with ApplyTransfromer
    	  case `CodeStyleParenthesesClean` => new CleanCodeGenerator
    	  case `CodeStyleParenthesesClassic` => new ClassicStyleCodeGenerator
    	}
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
          import java.util.Collections.{ emptyList => javaEmptyList }

          val sortedResults = InnerFinder(scu, position).getOrElse(return javaEmptyList()).map(x => x.getSnippet)
                    
          val list1: java.util.List[ICompletionProposal] = new java.util.LinkedList[ICompletionProposal]()

          var i = sortedResults.length
          sortedResults.foreach(x => {
            list1.add(new InSynthCompletitionProposal(x, i))
            i -= 1
          })

          list1
        case _ => javaEmptyList()
      }
      case _ => javaEmptyList()
    }
  }
}


object InSynthWrapper {
  
  var insynth:InSynth = null
  var compiler:Global = null
  
  var builder:InitialEnvironmentBuilder = null
  var predefDecls:List[Declaration] = null
  
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
