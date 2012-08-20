package ch.epfl.insynth.reconstruction

// InSynth library
import ch.epfl.insynth.env.{ SimpleNode, Weight }

// InSynth reconstructor
import ch.epfl.insynth.reconstruction.combinator.{ Combinator, FormatPrNode }
import ch.epfl.insynth.reconstruction.intermediate.{ IntermediateTransformer, FormatableIntermediate }
import ch.epfl.insynth.reconstruction.codegen.{ Extractor, CodeGenerator }

// InSynth core
import ch.epfl.insynth.core.Activator
import ch.epfl.insynth.core.preferences.InSynthConstants

/**
 * Object for reconstruction of proof trees into output(s)
 */
object Reconstructor extends (SimpleNode => List[Output]) {

  def apply(tree: SimpleNode): List[Output] = {
    // get needed number of snippets from the store
    val numberOfCombinations = Activator.getDefault.getPreferenceStore.getInt(
  		InSynthConstants.OfferedSnippetsPropertyString)
  		
		// return immediately if reconstruction is not needed
  	if (numberOfCombinations <= 0)
  	  return List.empty
		
		// get maximum duration of the combination step
		val maximumTime = Activator.getDefault.getPreferenceStore.getInt(
	    InSynthConstants.MaximumTimePropertyString)
    
    // construct the combinator tree
    val combinatorTree = Combinator(tree, numberOfCombinations, maximumTime) match {
  	  // check result, if some return it, otherwise return an empty list
  	  case Some(result) => result
  	  case None => {
  	    // logging
  	    if (Config.isLogging)
  	    	Config.logReconstructor.warning("Combinator returned None")
  	    return List.empty
  	  }
  	}
    
    // logging
    if (Config.isLogging) {
      Config.logReconstructor.info(
        "after combinator " + FormatPrNode(combinatorTree)
      )    
    }
    
    // transform the trees (first two steps of the code generation phase)
    val transformedTree = IntermediateTransformer(combinatorTree)
     
    // logging
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
      "Generated code snippets: " + 
  		{
        if (generatedCode.size > 15)
        	(generatedCode take 15 map { _._1.toString } mkString(", ")) + "... and " + (generatedCode.size - 15) + " more"
  		  else
        	(generatedCode map { _._1.toString } mkString(", "))
  		}    		    
    )
    
    // collect all generated snippets
    (generatedCode map {      
      output => Output(output._1.toString.trim, new Weight(output._2))
    })    
  }
  
}

/**
 * Encapsulation of the result output from the reconstruction phase, non UI dependent
 */
case class Output(snippet:String, weight:Weight){
  def getSnippet = snippet
  def getWieght = weight
}