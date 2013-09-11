package ch.epfl.insynth.scala

import insynth.structures.{ SuccinctType => InSynthType }
import insynth.engine.InitialSender
import insynth.{ query => ins }

import ch.epfl.insynth.scala.loader.ScalaDeclaration

case class Query(decl: ScalaDeclaration, override val inSynthRetType:InSynthType, sender: InitialSender)
	extends ins.Query(decl.getType) {
  
  def getSolution = sender.getAnswers
  
  def getDeclaration = decl
  
  override def getReturnType = inSynthRetType
  
  def getSender = sender
}