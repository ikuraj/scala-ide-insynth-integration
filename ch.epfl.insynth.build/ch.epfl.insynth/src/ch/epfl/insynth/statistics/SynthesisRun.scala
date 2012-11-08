package ch.epfl.insynth.statistics

import ch.epfl.insynth.statistics.format.XMLable

case class SynthesisRun(var fileName: String) extends XMLable {

  private var _synthesisTime: Long = _
  
  def synthesisTime_=(l: Long) = _synthesisTime = l
  def synthesisTime = _synthesisTime
  
  private var _reconstructionTime: Long = _
  
  def reconstructionTime_=(l: Long) = _reconstructionTime = l
  def reconstructionTime = _reconstructionTime 
  
  private var _initialDeclarations: Int = _
  
  def initialDeclarations_=(i: Int) = _initialDeclarations = i
  def initialDeclarations = _initialDeclarations

  override def toXML =
    <run name={ fileName }>
    	<reconstruction>
    		<time> { reconstructionTime } </time>
    	</reconstruction>
    	<synthesis>
    		<time> { synthesisTime } </time>
    	</synthesis>
    	<overall>
    		<time> { reconstructionTime + synthesisTime } </time>
    		<initialDeclarations> { initialDeclarations } </initialDeclarations>
    	</overall>
    </run>
  
}