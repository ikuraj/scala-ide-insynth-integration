package ch.epfl.insynth.reconstruction

import ch.epfl.insynth.env.SimpleNode
import ch.epfl.insynth.env.Weight
import ch.epfl.insynth.Config
import ch.epfl.insynth.reconstruction.combinator.Combinator
import ch.epfl.insynth.reconstruction.intermediate.IntermediateTransformer
import ch.epfl.insynth.reconstruction.codegen.{ Extractor, CodeGenerator }
import java.util.logging.Logger
import ch.epfl.insynth.reconstruction.intermediate.FormatableIntermediate
import ch.epfl.insynth.reconstruction.combinator.FormatPrNode
import ch.epfl.insynth.core.Activator
import ch.epfl.insynth.core.preferences.InSynthConstants

object Reconstructor {

  def apply(tree:SimpleNode): List[Output] = {
    // get needed number of snippets from the store
    val numberOfCombinations = Activator.getDefault.getPreferenceStore.getInt(
  		InSynthConstants.OfferedSnippetsPropertyString)
  		
		// return immediately if reconstruction is not needed
  	if (numberOfCombinations <= 0)
  	  return List.empty
		
		// get maximum duration of the combination step
		val maximumTime = Activator.getDefault.getPreferenceStore.getInt(
	    InSynthConstants.MaximumTimePropertyString)
    
    val combinatorTree = Combinator(tree, numberOfCombinations, maximumTime) match {
  	  case Some(result) => result
  	  case None => return List.empty
  	}
    
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
      "Generated code snippets: " + (generatedCode map { _._1.toString } mkString(","))
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