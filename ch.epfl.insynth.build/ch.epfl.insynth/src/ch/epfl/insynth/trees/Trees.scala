package ch.epfl.insynth.trees

sealed abstract class Type

object BottomType extends Type

//--------------------------------------------------- Ground Types ----------------------------------------------------------//
  
case class Const(val name: String) extends Type
case class Instance(val name: String, val t: List[Type]) extends Type
case class Arrow(val paramType:TSet, val returnType:Type) extends Type


case class TSet(val list:List[Type]) extends Type {  // TODO: Maybe find better representation
  
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
  
  def contains(tpe1:Type) = list.contains(tpe1)
  
  def content = list
  
}

object TSet {

  val empty = new TSet()
  
  def apply(tpe:Type*) = new TSet(tpe.toList)
  
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

object Type {
  
  def returnType(tpe:Type) = tpe match {
    case Arrow(_,returnType) => returnType
    case IArrow(_,returnType) => returnType
    case t => t
  }
  
  def paramTypes(tpe:Type) = tpe match {
    case Arrow(params,_) => params.content
    case IArrow(params,_) => params.content
    case _ => Nil
  }
  
  def paramSetType(tpe:Type) = tpe match {
    case Arrow(params,_) => params
    case IArrow(params,_) => params
    case _ => TSet.empty
  }
}

case class FormatType(tpe: Type) extends ch.epfl.insynth.print.Formatable {
  import ch.epfl.insynth.print.FormatHelpers._
  
  override def toDocument = toDocument(tpe)
  
  def toDocument(tpe: Type): scala.text.Document = {    
    tpe match {
      case Const(name) => name
      case Arrow(TSet(paramList), returnType) => 
        paren(seqToDoc(paramList, ",", toDocument(_:Type))) :: "→" :: toDocument(returnType)
      case BottomType => "⊥"
      case Instance(name, list) => name :: "[" :: seqToDoc(list, ",", toDocument(_:Type)) :: "]" 
      case _ => throw new UnsupportedOperationException
    }
  }
}



//------------------------------------------------ Polymorphic Types --------------------------------------------------------//
  
case class Variable(val name:String) extends Type

//-------------------------------------------- Inheritance Function Types ---------------------------------------------------//

case class IArrow(val subclass:TSet, val superclass:Type) extends Type