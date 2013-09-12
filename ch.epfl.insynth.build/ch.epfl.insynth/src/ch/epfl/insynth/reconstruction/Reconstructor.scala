package ch.epfl.insynth.reconstruction

// InSynth library
import insynth.structures.{ SimpleNode, Weight }
import insynth.structures.Weight.Weight
import insynth.reconstruction.Streamer

// InSynth reconstructor
import ch.epfl.insynth.reconstruction.codegen.CodeGenerator

// InSynth core
import ch.epfl.insynth.core.Activator
import ch.epfl.insynth.core.preferences.InSynthConstants

/**
 * Object for reconstruction of proof trees into output(s)
 */
object Reconstructor extends ( (SimpleNode, CodeGenerator) => List[Output]) {

  def apply(tree: SimpleNode, codeGenerator: CodeGenerator): List[Output] = {
    // get needed number of snippets from the store
    val numberOfCombinations = Activator.getDefault.getPreferenceStore.getInt(
  		InSynthConstants.OfferedSnippetsPropertyString)
  		
		// return immediately if reconstruction is not needed
  	if (numberOfCombinations <= 0)
  	  return List.empty
		
		// get maximum duration of the combination step
		val maximumTime = Activator.getDefault.getPreferenceStore.getInt(
	    InSynthConstants.MaximumTimePropertyString)
    
    // logging
    if (Config.isLogging) {
      Config.logReconstructor.info(
        "going into combinator phase with (numberOfCombinations, maximumTime): " + (numberOfCombinations, maximumTime)
      )    
    }

    val extractedTrees = Streamer(tree, true)
	    
    // for each tree, generate the code for it
    val generatedCode = extractedTrees.take(numberOfCombinations) map {
      resPair => (codeGenerator(resPair._1), resPair._2)
    }
        
    // logging
    if (Config.isLogging) {
      Config.logReconstructor.info("solutions are generated")    
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
      output => Output(output._1.toString.trim, output._2)
    }).toList 
  }
  
}

/**
 * Encapsulation of the result output from the reconstruction phase, non UI dependent
 */
case class Output(snippet:String, weight:Weight){
  def getSnippet = snippet
  def getWieght = weight
}
