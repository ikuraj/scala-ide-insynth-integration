package ch.epfl.insynth.scala

import insynth.{ structures => succ }
import insynth.structures.{ SuccinctType => Type, _ }

import ch.epfl.insynth.scala.{ Instance => ScalaInstance, Const => ScalaConst, _}

import insynth.util.logging._

object TypeTransformer extends HasLogger {

  def transform(scalaType:ScalaType): Type = scalaType match {
    case Method(null, paramss, returnType) if (paramss != null && returnType != null) => transformFunction(returnType, paramss.flatten.toSet)
    case Method(receiver, paramss, returnType) if (paramss != null && returnType != null) => transformFunction(returnType, Set(receiver) ++ paramss.flatten.toSet)
    case Function(params, returnType) if (params != null && !params.isEmpty && returnType != null) => transformFunction(returnType, params.toSet)
    case ScalaInstance(name, list) if (list != null && !list.isEmpty && name != null) => succ.Instance(name, list.map(transform))
    case ScalaConst(name) if(name != null) => succ.Const(name)
    case Inheritance(subtype, supertype) if(subtype != null && supertype != null)  => IArrow(TSet(transform(subtype)), transform(supertype))
    case _: Variable =>
      throw new UnsupportedOperationException("matching variables not implemented")
    case t =>
      fine("Case: "+t.getClass.getName+" should be covered in 'TypeTransformer.transform()'.")
      throw new Exception("Case: "+t.getClass.getName+" should be covered in 'TypeTransformer.transform()'.")
  }

  private def transformFunction(fun:ScalaType, params:Set[ScalaType]): Type = fun match {
    case Function(params1, returnType) if (params1 != null && returnType != null) => 
      transformFunction(returnType, params ++ params1)
    case ScalaInstance(name, list) if (list != null && !list.isEmpty && name != null) => 
      if(params.isEmpty) succ.Instance(name, list.map(transform))
      else succ.Arrow(TSet(params.toList.map(transform)), succ.Instance(name, list.map(transform)))
    case ScalaConst(name)  if(name != null) => 
      if(params.isEmpty) succ.Const(name)
      else succ.Arrow(TSet(params.toList.map(transform)), succ.Const(name))
    case _: Variable =>
      throw new UnsupportedOperationException("matching variables not implemented")
    case t =>
      fine("Case: "+t.getClass.getName+" should be covered in 'TypeTransformer.transformFunction()'.")
      throw new Exception("Case: "+t.getClass.getName+" should be covered in 'TypeTransformer.transformFunction()'.")
  }
  
  def trasformInheritance(subType:ScalaType, superType:ScalaType):Type = IArrow(TSet(transform(subType)), transform(superType))
}