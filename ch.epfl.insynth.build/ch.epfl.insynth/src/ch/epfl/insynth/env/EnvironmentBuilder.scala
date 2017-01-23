package ch.epfl.insynth.env

import ch.epfl.insynth.trees.Type
import ch.epfl.insynth.scheduler.Scheduler
import ch.epfl.scala.trees.ScalaType
import ch.epfl.insynth.trees.TypeTransformer
import ch.epfl.insynth.trees.TSet


abstract class EnvironmentBuilder  {

  import ch.epfl.insynth.trees.TypeTransformer._
  
  private var taMap = Map.empty[Type, TypeAssignment]

  def addDeclaration(name:String, scalaType:ScalaType) {
    addDeclaration(new Declaration(name, transform(scalaType), scalaType))
  }
  
  def addDeclarations(decls:List[Declaration]){
    decls.foreach(addDeclaration)
  }
  
  def addDeclaration(decl:Declaration) {
    val tpe = decl.getType

    taMap.get(tpe) match {
      case Some(ta) => 
        ta.addDeclaration(decl)
      case None => {
        val ta = new TypeAssignment(decl.getType)
        ta.addDeclaration(decl)
        getEnv.addTypeAssignment(ta)
        taMap += (tpe -> ta)
      }
    }
  }
  
  protected def getEnv:Environment

  def produceEnvirionment:Environment
}

class InitialEnvironmentBuilder extends EnvironmentBuilder {
  
  private val env = new InitialEnvironment() 

  override protected def getEnv = env
  
  override def produceEnvirionment = env
  
  def getAllDeclarations = env.getAllDeclarations
}

class DeltaEnvironmentBuilder(parent:Environment) extends EnvironmentBuilder {
  
  private val env = new DeltaEnvironment(parent)
  
  def create(set:TSet) {
    set.content.foreach{
      tpe =>
          this.addDeclaration(new Declaration(tpe))
    }
  }
  
  override protected def getEnv = env 
  
  override def produceEnvirionment = if (!env.getTypeSet.equals(parent.getTypeSet)) env else parent
}