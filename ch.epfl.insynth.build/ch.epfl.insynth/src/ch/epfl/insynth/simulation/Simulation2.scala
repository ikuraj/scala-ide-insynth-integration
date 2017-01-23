package ch.epfl.insynth.simulation

import ch.epfl.insynth.env._
import ch.epfl.scala.trees._
import ch.epfl.insynth.engine.Engine
import ch.epfl.insynth.scheduler.BFSScheduler
import ch.epfl.insynth.util.TimeOut
import ch.epfl.insynth.util.TreePrinter

object Simulation2 {
 
  import ch.epfl.insynth.trees.TypeTransformer._
  
  private final val timeSlot = 500
  
  private final val INT = Const("Int")
  private final val STRING = Const("String")
  private final val CHAR = Const("Char")

  def main(args:Array[String]){
    
    val builder = new InitialEnvironmentBuilder()
    
    builder.addDeclaration("m1", Method(STRING, Nil, INT))
    builder.addDeclaration("m2", Method(null, Nil, STRING))    
    builder.addDeclaration("m3", Method(null, List(List(CHAR)), STRING))
    builder.addDeclaration("m4", Method(null, Nil, CHAR))

    val inSynth = new Engine(builder, INT, new BFSScheduler(), TimeOut(timeSlot))
  
    val solution = inSynth.run()
    if(solution != null) TreePrinter(solution, Nil)
    else println("No solution found!")
  }
}