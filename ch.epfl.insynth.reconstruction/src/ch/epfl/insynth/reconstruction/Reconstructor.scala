package ch.epfl.insynth.reconstruction

import ch.epfl.insynth.env.SimpleNode
import ch.epfl.insynth.env.Weight

object Reconstructor {

  def apply(tree:SimpleNode):List[Output] = {
    //TODO: Implement
    List.empty
  }
  
}

case class Output(snippet:String, weight:Weight){
  def getSnippet = snippet
  def getWieght = weight
}