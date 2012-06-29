package insynth.format

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
    
  override def toString = {
    val prettyPrinter = new PrettyPrinter(80, 2)
	prettyPrinter format this.toXML
  }
  
}