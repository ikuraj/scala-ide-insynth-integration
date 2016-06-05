package ch.epfl.insynth.reconstruction.combinator

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
  
  override def equals(other: Any): Boolean = { 
    other match {
      case that: Declaration =>
        if (this.getType != that.getType) return false
        if (this.getWeight != that.getWeight) return false
        if (this.getSimpleName != that.getSimpleName) return false
        true
      case _ => false
    }
  }

}

case class AbsDeclaration(inSynthType: Type) extends Declaration(inSynthType) {
  private val abstractDeclarationWeight: Double = 1.0d
  
  def getWeight = abstractDeclarationWeight
  def getSimpleName = "#abs#"
  override def equals(other: Any): Boolean = { 
//    println("AbsDeclaration equals ")
    other match { 
      case that: AbsDeclaration => 
        val that = other.asInstanceOf[Declaration]
        if (this.getType != that.getType) return false
        if (this.getWeight != that.getWeight) return false
        if (this.getSimpleName != that.getSimpleName) return false
    
        return super.equals(this)
      case _ => false
      
    }

  }
}

case class NormalDeclaration(val declaration: InSynth.Declaration)
extends Declaration(declaration.getType)
{
  def getWeight:Double = declaration.getWeight.getValue
  def getSimpleName = declaration.getSimpleName
  def getScalaType = declaration.scalaType
  override def equals(other: Any): Boolean = { 
//    println("NormalDeclaration equals ")
    other match{ 
      case that: NormalDeclaration => 
        val that = other.asInstanceOf[Declaration]
        if (this.getType != that.getType) return false
        if (this.getWeight != that.getWeight) return false
        if (this.getSimpleName != that.getSimpleName) return false
    
        return super.equals(this)
      case _ => false
      
    }


  }
}