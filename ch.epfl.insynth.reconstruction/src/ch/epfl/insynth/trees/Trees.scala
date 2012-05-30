package ch.epfl.insynth.trees

sealed abstract class Type extends FormatableType
  
//--------------------------------------------------- Ground Types ----------------------------------------------------------//
  
case class Const(val name: String) extends Type
case class Instance(val name: String, val t: List[Type]) extends Type
case class Arrow(val paramType:TSet, val returnType:Type) extends Type

// XXX TSet does not have to be a Type
case class TSet(val list:List[Type]) {
  
  def this() = this(Nil)
  
  private var hashCodeValue:Int = list.map(x => x.hashCode >>> 4).foldLeft(2482783)( _+_) ^ 23821
  
  override def equals(that:Any) = {
    if (that == null || !that.isInstanceOf[TSet]) false
    else {
      val tpe = that.asInstanceOf[TSet]
      TSet.equals(this, tpe)
    }
  }
  
  override def hashCode() = hashCodeValue
  
  def subsetOf(tpe:TSet) = {
    if (tpe == null) false
    else {
      TSet.subsetOf(this,tpe)
    }
  }
  
  def minus(tpe1:TSet) = TSet.minus(this, tpe1)
  
  def union(tpe1:TSet) = TSet.union(this, tpe1)
  
}

object TSet {

  val empty = new TSet()
  
  def apply(tpe:Type) = new TSet(List(tpe))
  
  def equals(tpe1:TSet, tpe2:TSet) = {
    val length1 = tpe1.list.length
    val length2 = tpe2.list.length
    
    if (length1 != length2) false
    else {
      tpe1.list.forall(x => tpe2.list.contains(x))
    }
  }
  
  def subsetOf(tpe1:TSet, tpe2:TSet) = {
    val length1 = tpe1.list.length
    val length2 = tpe2.list.length
    
    if (length1 > length2) false
    else {
      tpe1.list.forall(x => tpe2.list.contains(x))
    }
  }
  
  def union(tpe1:TSet, tpe2:TSet) = TSet(tpe1.list ::: tpe2.list.filterNot(tpe1.list.contains))
  
  def minus(tpe1:TSet, tpe2:TSet) = TSet(tpe1.list.filterNot(tpe2.list.contains))
  
}

trait FormatableType extends ch.epfl.insynth.print.Formatable {
  import ch.epfl.insynth.print.FormatHelpers._
    
  def toDocument = {    
    this match {
      case Const(name) => name
      case Arrow(TSet(paramList), returnType) => 
        paren(seqToDoc(paramList, ",", (_:Type).toDocument)) :: "→" :: returnType.toDocument
      case _ => throw new UnsupportedOperationException
    }
  }
}

//------------------------------------------------ Polymorphic Types --------------------------------------------------------//
  
case class Variable(val name:String) extends Type

//-------------------------------------------- Inheritance Function Types ---------------------------------------------------//

case class IArrow(val subclass:TSet, val superclass:Type) extends Type