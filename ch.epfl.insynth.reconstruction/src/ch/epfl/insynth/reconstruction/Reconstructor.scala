package ch.epfl.insynth.reconstruction

import ch.epfl.insynth.env.SimpleNode
import ch.epfl.insynth.env.Weight
import ch.epfl.insynth.combinator.Combinator
import java.util.logging.Logger

object Reconstructor {

  def apply(tree:SimpleNode):List[Output] = {
    // transform the trees (first two steps of the code generation phase)
    val transformedTrees = IntermediateTransformer(Combinator(tree))
       
    // for each tree, generate the code for it
    val generatedCode = transformedTrees.toList map {
      resTree => CodeGenerator(resTree)
    }
        
    // log all snippets
//    Logger.getLogger("reconstruction.codegen.reconstructor").info(
//      (generatedCode.flatten.head.toString /: generatedCode.flatten) {
//        (stringSoFar, formatable) => stringSoFar + "\n" + formatable
//      } 
//    )
    
    // collect all generated snippets
    (generatedCode.flatten.map {      
      output => Output(output.toString, new Weight(0.0))
    })    
  }
  
}

case class Output(snippet:String, weight:Weight){
  def getSnippet = snippet
  def getWieght = weight
}