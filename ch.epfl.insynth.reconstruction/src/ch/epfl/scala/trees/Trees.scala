package ch.epfl.scala.trees

sealed abstract class ScalaType

object BottomType extends ScalaType

//------------------------------------------- GROUND TYPES ----------------------------------------------------------//

case class Method(receiver:ScalaType, paramss:List[List[ScalaType]], returnType:ScalaType) extends ScalaType
case class Function(params:List[ScalaType], returnType:ScalaType) extends ScalaType
case class Instance(val name: String, val t: List[ScalaType]) extends ScalaType
case class Inheritance(subtype:ScalaType, supertype:ScalaType) extends ScalaType
case class Const(val name: String) extends ScalaType

//----------------------------------------- POLYMORPHIC TYPES --------------------------------------------------------//

// NOTE do this later...
case class Variable(val name:String) extends ScalaType