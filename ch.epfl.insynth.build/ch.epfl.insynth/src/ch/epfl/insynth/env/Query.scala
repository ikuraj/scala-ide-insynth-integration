package ch.epfl.insynth.env

import ch.epfl.insynth.trees.{Type => InSynthType}
import ch.epfl.scala.trees._
import ch.epfl.insynth.trees.TypeTransformer

class QueryBuilder(tpe:ScalaType) {
  assert(tpe != null)
  
  private val scalaRetType = Const("$Bottom_Type_Just_For_Resolution$") 
  private val scalaType = Function(List(tpe), scalaRetType)
  
  private val inSynthRetType = TypeTransformer.transform(scalaRetType)
  private val inSynthType = TypeTransformer.transform(scalaType)
  
  def getQuery = Query(new Declaration("query", inSynthType, scalaType), inSynthRetType, new InitialSender())
  
}

case class Query(decl:Declaration, inSynthRetType:InSynthType, sender:InitialSender) {
  
  def getSolution = sender.getAnswers
  
  def getDeclaration = decl
  
  def getReturnType = inSynthRetType
  
  def getSender = sender
}