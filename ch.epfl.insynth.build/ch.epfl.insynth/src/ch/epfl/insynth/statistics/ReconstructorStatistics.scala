package ch.epfl.insynth.statistics

import scala.collection.mutable.{ LinkedList => MutableList }

object ReconstructorStatistics {

  var currentRun: SynthesisRun = _
  
  var reconstructionTime: MutableList[Long] = MutableList.empty
  
  def resetStatistics = {
    reconstructionTime = MutableList.empty
    lastDeclarationCount = MutableList.empty
  }
  
  val formatString = "%20s%s"
    
  override def toString = {
		List[(String, String)](
	    ("Reconstruction time:", reconstructionTime.mkString(", ") ),
	    ("Average reconstruction time:", (reconstructionTime.sum.toFloat/reconstructionTime.size).toString )
    ) map { case (description, value) => String.format(formatString, description, value)  } mkString ("\n")
  }
  
  var lastDeclarationCount: MutableList[Int] = MutableList.empty
  
}