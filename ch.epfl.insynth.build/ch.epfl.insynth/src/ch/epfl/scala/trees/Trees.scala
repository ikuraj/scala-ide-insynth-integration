package ch.epfl.scala.trees

sealed abstract class ScalaType

//------------------------------------------- GROUND TYPES ----------------------------------------------------------//

case class Method(receiver:ScalaType, paramss:List[List[ScalaType]], returnType:ScalaType) extends ScalaType
case class Function(params:List[ScalaType], returnType:ScalaType) extends ScalaType
case class Instance(val name: String, val t: List[ScalaType]) extends ScalaType
case class Inheritance(subtype:ScalaType, supertype:ScalaType) extends ScalaType
case class Const(val name: String) extends ScalaType

//----------------------------------------- POLYMORPHIC TYPES --------------------------------------------------------//

case class Variable(val name:String) extends ScalaType

case class FormatScalaType(tpe: ScalaType) extends ch.epfl.insynth.print.Formatable {
  override def toDocument = toDocument(tpe)
  
  def toDocument(tpe: ScalaType): scala.text.Document = {
    import ch.epfl.insynth.print.FormatHelpers._
    import scala.text.Document._
    
    tpe match {
      case Method(receiver, params, returnType) =>
        "Method" :: paren(
            foldDoc(
              for (list <- params) yield {
                paren(seqToDoc(list, ",", toDocument(_:ScalaType)))
              }, " "
            )
        )
      case Function(params, returnType) =>
        "Function" :: paren(
            seqToDoc(params, ",", toDocument(_:ScalaType))
        )   
      case Const(name) =>
        name
      case Instance(name, list) => "Instance " :: name  
      case Inheritance(subType, superType) => "Inheritance:" :: toDocument(subType) :: "-" :: toDocument(superType)
      case null => "Null"
      case _ => "Not implemented yet!"
    }
  }
}