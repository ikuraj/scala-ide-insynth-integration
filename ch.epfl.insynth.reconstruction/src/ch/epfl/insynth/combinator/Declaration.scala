package ch.epfl.insynth.combinator

import ch.epfl.insynth.trees.{ Type }
import ch.epfl.insynth.{ env => InSynth }

object DeclarationTransformer {
  
  def fromInSynthDeclaration(dec: InSynth.Declaration):Declaration = {
    dec match {
      case InSynth.Declaration("#abs#", _, _) => AbsDeclaration(dec.getType)
      case _ => NormalDeclaration(dec)
    }
  }  
}

abstract class Declaration(inSynthType: Type) {  
  def getType = inSynthType
  def getWeight:Double
  def getSimpleName: String  
}

case class AbsDeclaration(inSynthType: Type) extends Declaration(inSynthType) {
  private val abstractDeclarationWeight: Double = 1.0d
  
  def getWeight = abstractDeclarationWeight
  def getSimpleName = "#abs#"
}

case class NormalDeclaration(val declaration: InSynth.Declaration)
extends Declaration(declaration.getType)
{
  def getWeight:Double = declaration.getWeight.getValue
  def getSimpleName = declaration.getSimpleName
  def getScalaType = declaration.scalaType
}