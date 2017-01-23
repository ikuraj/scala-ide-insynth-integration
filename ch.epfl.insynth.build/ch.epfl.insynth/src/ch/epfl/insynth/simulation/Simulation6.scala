package ch.epfl.insynth.simulation

import ch.epfl.insynth.env._
import ch.epfl.scala.trees._
import ch.epfl.insynth.engine.Engine
import ch.epfl.insynth.scheduler.BFSScheduler
import ch.epfl.insynth.util.TimeOut
import ch.epfl.insynth.util.TreePrinter

object Simulation6 {
 
  import ch.epfl.insynth.trees.TypeTransformer._

  private final val timeSlot = 500

  //K combiantor type: (A->  (B -> C)) -> ((A -> B) ->  (A -> C)).
  
  private final val A = Const("A")
  private final val B = Const("B")
  private final val C = Const("C")
  
  private final val AtoB = Function(List(A), B)
  private final val ABtoC = Function(List(A, B), C)  
  private final val KComb = Function(List(ABtoC, AtoB, A), C) 
  
  def main(args:Array[String]){
    
    val builder = new InitialEnvironmentBuilder()
    
    val inSynth = new Engine(builder, KComb, new BFSScheduler(), TimeOut(timeSlot))
  
    //Maybe every TA should have a list of found solutions
    val solution = inSynth.run()
    if(solution != null) TreePrinter(solution, Nil)
    else println("No solution found!")
  }
}