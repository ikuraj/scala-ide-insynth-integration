package ch.epfl.insynth.scala.loader

class Weight(value: Float) extends Ordered[Weight] {
  def compare(that: Weight) = {
    val thatVal = that.getValue
      if (value < thatVal) -1
      else if (value > thatVal) 1
      else 0
  }
  
  def getValue = value
}