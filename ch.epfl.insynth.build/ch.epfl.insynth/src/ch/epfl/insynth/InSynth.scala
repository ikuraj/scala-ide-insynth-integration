package ch.epfl.insynth

import scala.tools.nsc.interactive.Global
import ch.epfl.insynth.loader.TLoader
import scala.tools.nsc.util.SourceFile
import ch.epfl.insynth.engine.Engine
import ch.epfl.insynth.util.TimeOut
import ch.epfl.insynth.scheduler.BFSScheduler
import ch.epfl.insynth.util.TreePrinter
import ch.epfl.insynth.env.InitialEnvironmentBuilder
import ch.epfl.insynth.loader.TPreLoder
import ch.epfl.insynth.scheduler.WeightScheduler
import ch.epfl.insynth.env.ContainerNode

class InSynth(val compiler: Global) extends TLoader with TPreLoder {
  
  import compiler._
  
  private val loader = new Loader()
  
  def getPredefDecls() = {
    val preloader = new PreLoader()
    preloader.load()
  }
  
  def getSnippets(pos:Position, builder:InitialEnvironmentBuilder):ContainerNode = {       
    ScalaTypeExtractor.clear()
    var tree = wrapTypedTree(pos.source, false)
    val desiredType = loader.load(pos, tree, builder)

    Config.inSynthLogger.fine("builder.getAllDeclarations.size: " + builder.getAllDeclarations.size)   
    Config.inSynthLogger.finer("Declarations: \n" + builder.getAllDeclarations.sortWith( (d1, d2) => d1.fullName.compareTo(d2.fullName) < 0 ).map( decl => decl.fullName + 
      "[" + decl.scalaType + " : " + decl.inSynthType + " : " + decl.getWeight.getValue + "]" ).mkString("\n"))
    
    val engine = new Engine(builder, desiredType, new WeightScheduler(), TimeOut(Config.getTimeOutSlot))
    
    val time = System.currentTimeMillis
      
    val solution = engine.run()

    if (solution != null) {
      Config.inSynthLogger.info("Solution found in " + (System.currentTimeMillis - time) + " ms.")
      Config.inSynthLogger.info("Solution found: " + TreePrinter(solution, Config.proofTreeLevelToLog))
      //TreePrinter.printAnswerAsXML(Config.proofTreeOutput, solution, Config.proofTreeLevelToLog)
    } else 
      Config.inSynthLogger.info("No solution found in " + (System.currentTimeMillis - time) + " ms")
    
    solution
  }

  private def wrapTypedTree(source: SourceFile, forceReload: Boolean): Tree =
  {
    val response = new Response[Tree]
    askType(source, forceReload, response)
    val typed = response.get
    typed.fold(identity, throw _)
  }  
}