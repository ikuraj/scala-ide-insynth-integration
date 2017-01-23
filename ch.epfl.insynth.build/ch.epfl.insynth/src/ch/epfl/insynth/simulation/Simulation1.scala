package ch.epfl.insynth.simulation

import ch.epfl.insynth.env._
import ch.epfl.scala.trees._
import ch.epfl.insynth.engine.Engine
import ch.epfl.insynth.scheduler.BFSScheduler
import ch.epfl.insynth.util.TimeOut
import ch.epfl.insynth.util.TreePrinter

object Simulation1 { 
 
  import ch.epfl.insynth.trees.TypeTransformer._

  private final val timeSlot = 500
  
  private final val INT = Const("Int")
  private final val STRING = Const("String")
  
  def main(args:Array[String]){
    
    val builder = new InitialEnvironmentBuilder()
    
    builder.addDeclaration("m1", Method(STRING, Nil, INT))
    builder.addDeclaration("m2", Method(null, Nil, STRING))
    
    val inSynth = new Engine(builder, INT, new BFSScheduler(), TimeOut(timeSlot))
  
    val solution = inSynth.run()
    if(solution != null) TreePrinter(solution, Nil)
    else println("No solution found!")
  }
}