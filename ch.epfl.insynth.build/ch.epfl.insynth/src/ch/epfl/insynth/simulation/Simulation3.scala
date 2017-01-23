package ch.epfl.insynth.simulation

import ch.epfl.insynth.env._
import ch.epfl.scala.trees._
import ch.epfl.insynth.engine.Engine
import ch.epfl.insynth.scheduler.BFSScheduler
import ch.epfl.insynth.util.TimeOut
import ch.epfl.insynth.util.TreePrinter

object Simulation3 { 
 
  import ch.epfl.insynth.trees.TypeTransformer._

  private final val timeSlot = 500
  
  private final val INT = Const("Int")
  private final val STRING = Const("String")
  private final val CHAR = Const("Char")

  private final val STRINGtoCHAR = Function(List(STRING), CHAR)
  
  def main(args:Array[String]){
    
    val builder = new InitialEnvironmentBuilder()
    
    builder.addDeclaration("m7", Method(null, Nil, CHAR))
    builder.addDeclaration("m8", Method(STRINGtoCHAR, Nil, INT))
    builder.addDeclaration("m9", Method(null, List(List(STRING)), CHAR)) 
    
    val inSynth = new Engine(builder, INT, new BFSScheduler(), TimeOut(timeSlot))
  
    val solution = inSynth.run()
    if(solution != null) TreePrinter(solution, Nil)
    else println("No solution found!")
  }
}