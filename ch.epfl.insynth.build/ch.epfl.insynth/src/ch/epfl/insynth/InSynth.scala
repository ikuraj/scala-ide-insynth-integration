package ch.epfl.insynth

import _root_.scala.tools.nsc.interactive.Global
import _root_.scala.reflect.internal.util.SourceFile

import ch.epfl.insynth.scala._
import ch.epfl.insynth.scala.loader.{ ScalaDeclaration => Declaration, _ }
import ch.epfl.insynth.reconstruction.codegen.CodeGenerator
import ch.epfl.insynth.reconstruction._

import insynth.structures.ContainerNode
import insynth.engine._
import insynth.engine.scheduler._
import insynth.util._
import insynth.util.format._
import insynth.util.logging._

class InSynth(val compiler: Global, codeGenerator: CodeGenerator)
	extends TLoader with TPreLoder with HasLogger {
  
  import compiler._
  
  private val loader = new Loader()
  
  def getPredefDecls() = {
    val preloader = new PreLoader()
    preloader.load()
  }
  
  def getSnippets(pos: Position, builder: InitialEnvironmentBuilder): Option[List[Output]] = {       
    ScalaTypeExtractor.clear()
    var tree = wrapTypedTree(pos.source, false)
    val desiredType = loader.load(pos, tree, builder)

    fine("builder.getAllDeclarations.size: " + builder.getAllDeclarations.size)   
    finer("Declarations: \n" + builder.getAllDeclarations.sortWith( (d1, d2) => d1.getSimpleName.compareTo(d2.getSimpleName) < 0 ).map( decl => decl + 
      "[" + decl.getDomainType + " : " + decl.inSynthType + " : " + decl.getWeight + "]" ).mkString("\n"))
    
    val queryBuilder = new QueryBuilder(desiredType)
    val engine = new Engine(builder, queryBuilder.getQuery, new WeightScheduler(), TimeOut(Config.getTimeOutSlot))
    
    val time = System.currentTimeMillis
      
    val solution = engine.run()

    if (solution != null) {
      info("Solution found in " + (System.currentTimeMillis - time) + " ms.")
      info("Solution found: " + TreePrinter(solution, Config.proofTreeLevelToLog))
      //TreePrinter.printAnswerAsXML(Config.proofTreeOutput, solution, Config.proofTreeLevelToLog)
    } else 
      info("No solution found in " + (System.currentTimeMillis - time) + " ms")
    
    if (solution != null) {
    	info("InSynth solution found, proceeding with reconstructor.")
    	
      Some(
        Reconstructor(solution.getNodes.head, codeGenerator).sortWith((x, y) => x.getWieght< y.getWieght) // + "   w = "+x.getWieght.getValue)
			)
    }
    else {
    	warning("InSynth solution not found")
      None
    }
  }

  private def wrapTypedTree(source: SourceFile, forceReload: Boolean): Tree =
  {
    val response = new Response[Tree]
    askType(source, forceReload, response)
    val typed = response.get
    typed.fold(identity, throw _)
  }  
}