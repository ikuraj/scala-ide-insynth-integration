package ch.epfl.insynth.scala

import insynth.util.format.Formatable

case class FormatType(tpe: ScalaType) extends Formatable {
  override def toDocument = toDocument(tpe)
  
  def toDocument(tpe: ScalaType): scala.text.Document = {
    import insynth.util.format.FormatHelpers._
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