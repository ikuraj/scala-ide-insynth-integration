package ch.epfl.insynth.scala

import insynth.structures.{ SuccinctType => InSynthType }
import insynth.engine.InitialSender
import insynth.{ query => ins }

import ch.epfl.insynth.scala._
import ch.epfl.insynth.scala.loader.ScalaDeclaration

class QueryBuilder(tpe: ScalaType) extends ins.QueryBuilder(TypeTransformer.transform(tpe)) {
  assert(tpe != null)
  
  private val scalaRetType = Const("$Bottom_Type_Just_For_Resolution$") 
  private val scalaType = Function(List(tpe), scalaRetType)
  
  private val inSynthRetType = TypeTransformer.transform(scalaRetType)
  private val inSynthType = succinctType
  
  override def getQuery = Query(new ScalaDeclaration("query", inSynthType, scalaType), inSynthRetType, new InitialSender())
  
}