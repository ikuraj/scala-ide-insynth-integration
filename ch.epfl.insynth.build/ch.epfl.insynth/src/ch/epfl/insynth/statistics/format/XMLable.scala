package ch.epfl.insynth.statistics.format

import scala.xml.Node
import scala.xml.PrettyPrinter

object XMLable {
  
  def apply(xml: Node): String = {
    val prettyPrinter = new PrettyPrinter(80, 2)
    prettyPrinter format xml    
  }
  
}

trait XMLable {
  def toXML: Node
    
  def toXMLString = {
    val prettyPrinter = new PrettyPrinter(80, 2)
    prettyPrinter format this.toXML
  }
  
}