package ch.epfl.insynth.scala

import insynth.{ structures => succ }
import insynth.structures.{ DomainType => Type, _ }

import ch.epfl.insynth.scala.{ Instance => ScalaInstance, Const => ScalaConst, _}

import insynth.util.logging._

object DomainTypeTransformer extends (ScalaType => Type) with HasLogger {

  implicit def toSuccinctType(st: ScalaType) = TypeTransformer.transform(st)

  def apply(scalaType: ScalaType): Type = scalaType match {
    case Method(null, paramss, returnType) if (paramss != null && returnType != null) =>
      transformFunction(returnType, paramss.flatten)
    case Method(receiver, paramss, returnType) if (paramss != null && returnType != null) =>
      transformFunction(returnType, receiver +: paramss.flatten)
    case Function(params, returnType) if (params != null && !params.isEmpty && returnType != null) =>
      transformFunction(returnType, params)
    case inst@ ScalaInstance(name, list) if (list != null && !list.isEmpty && name != null) =>
      succ.Atom(inst)
    case ScalaConst(name) if(name != null) => Atom(scalaType)
    case Inheritance(subtype, supertype) if(subtype != null && supertype != null) =>
      succ.Function(List(this(subtype)), this(supertype))
    case _: Variable =>
      throw new UnsupportedOperationException("matching variables not implemented")
    case t =>
      fine("Case: "+t.getClass.getName+" should be covered in 'DomainTypeTransformer.transform()'.")
      throw new Exception("Case: "+t.getClass.getName+" should be covered in 'DomainTypeTransformer.transform()'.")
  }

  private def transformFunction(fun: ScalaType, params: List[ScalaType]): Type =
    succ.Function(params map this, this(fun))      
}