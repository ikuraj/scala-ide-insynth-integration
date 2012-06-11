package ch.epfl.insynth.reconstruction

import ch.epfl.insynth.env.SimpleNode
import ch.epfl.insynth.env.Weight
import ch.epfl.insynth.combinator.Combinator

object Reconstructor {

  def apply(tree:SimpleNode):List[Output] = {
    val transformedTrees = IntermediateTransformer(Combinator(tree))
       
    ((transformedTrees.toList map {
      resTree => CodeGenerator(resTree)
    }).flatten.map {      
      output => Output(output.toString, new Weight(0.0))
    })    
  }
  
}

case class Output(snippet:String, weight:Weight){
  def getSnippet = snippet
  def getWieght = weight
}