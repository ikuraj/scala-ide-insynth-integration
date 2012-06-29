package insynth.format

import scala.xml.Node
import scala.xml.PrettyPrinter

trait XMLable {
  def toXML: Node
    
  override def toString = {
    val prettyPrinter = new PrettyPrinter(80, 2)
	prettyPrinter format this.toXML
  }
  
}