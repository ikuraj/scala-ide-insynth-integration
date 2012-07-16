package ch.epfl.insynth.reconstruction

import ch.epfl.insynth.env.SimpleNode
import ch.epfl.insynth.env.Weight
import ch.epfl.insynth.Config
import ch.epfl.insynth.reconstruction.combinator.Combinator
import ch.epfl.insynth.reconstruction.intermediate.IntermediateTransformer
import ch.epfl.insynth.reconstruction.codegen.{ Extractor, CodeGenerator }

// for log printing
import java.util.logging.Logger
import ch.epfl.insynth.reconstruction.intermediate.FormatableIntermediate
import ch.epfl.insynth.reconstruction.combinator.FormatPrNode

object Reconstructor {

  def apply(tree:SimpleNode):List[Output] = {
    // get needed number of snippets from Config
    val numberOfCombinations = Config.numberOfSnippets
    
    val combinatorTree = Combinator(tree, numberOfCombinations)
    
    if (Config.isLogging) {
      Config.logReconstructor.info(
        "after combinator " + FormatPrNode(combinatorTree)
      )    
    }
    
    // transform the trees (first two steps of the code generation phase)
    val transformedTree = IntermediateTransformer(combinatorTree)
     
    if (Config.isLogging) {
      Config.logReconstructor.info(
        "after intermediate " + FormatableIntermediate(transformedTree)
      )    
    }
    
    // for each tree, generate the code for it
    val extractedTrees = Extractor(transformedTree, numberOfCombinations)
    
    // for each tree, generate the code for it
    val generatedCode = extractedTrees map {
      resPair => (CodeGenerator(resPair._1).head, resPair._2)
    }
        
    // log all snippets
    Config.logSolutions.info(
      generatedCode map { _._1.toString } mkString("\n")
    )
    
    // collect all generated snippets
    (generatedCode map {      
      output => Output(output._1.toString, new Weight(output._2))
    })    
  }
  
}

case class Output(snippet:String, weight:Weight){
  def getSnippet = snippet
  def getWieght = weight
}