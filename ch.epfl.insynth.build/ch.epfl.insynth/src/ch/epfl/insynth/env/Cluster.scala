package ch.epfl.insynth.env

import ch.epfl.insynth.trees.Type

abstract class Cluster(tpe:Type) {
  assert(tpe != null)
  
  protected var tas = List.empty[TypeAssignment]
  
  def getType = tpe

  def getTypeAssignments:List[TypeAssignment]
  
  def addTypeAssignment(ta:TypeAssignment) { tas = ta :: tas} 
}

class InitialCluster(tpe:Type) extends Cluster(tpe) {
  
  override def getTypeAssignments = tas
}

class DeltaCluster(tpe:Type, parent:Cluster) extends Cluster(tpe) {
  assert(parent != null)
  
  override def getTypeAssignments = parent.getTypeAssignments ::: tas
  
}