package ch.epfl.insynth.simulation

import ch.epfl.insynth.env._
import ch.epfl.scala.trees._
import ch.epfl.insynth.engine.Engine
import ch.epfl.insynth.scheduler.BFSScheduler
import ch.epfl.insynth.util.TimeOut
import ch.epfl.insynth.util.TreePrinter

object Simulation5 {
 
  import ch.epfl.insynth.trees.TypeTransformer._

  private final val timeSlot = 500
  
  private final val INT = Const("Int")
  
  def main(args:Array[String]){
    
    val builder = new InitialEnvironmentBuilder()
    
    builder.addDeclaration("m10", Method(INT, Nil, INT))
    builder.addDeclaration("m11", Method(null, Nil, INT))

    val inSynth = new Engine(builder, INT, new BFSScheduler(), TimeOut(timeSlot))
  
    val solution = inSynth.run()
    if(solution != null) TreePrinter(solution, Nil)
    else println("No solution found!")
  }
}