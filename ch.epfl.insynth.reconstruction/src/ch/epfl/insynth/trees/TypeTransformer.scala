package ch.epfl.insynth.trees

import ch.epfl.scala.trees.{Instance => ScalaInstance, Const => ScalaConst, _}

object TypeTransformer {

  //TODO: Cover Variables
  def transform(scalaType:ScalaType):Type = scalaType match {
    case Method(null, paramss, returnType) if (paramss != null && returnType != null) => transformFunction(returnType, paramss.flatten.toSet)
    case Method(receiver, paramss, returnType) if (paramss != null && returnType != null) => transformFunction(returnType, Set(receiver) ++ paramss.flatten.toSet)
    case Function(params, returnType) if (params != null && !params.isEmpty && returnType != null) => transformFunction(returnType, params.toSet)
    case ScalaInstance(name, list) if (list != null && !list.isEmpty && name != null) => Instance(name, list.map(transform))
    case ScalaConst(name) if(name != null) => Const(name)
    case Inheritance(subtype, supertype) if(subtype != null && supertype != null)  => IArrow(TSet(transform(subtype)), transform(supertype))
    case t => throw new Exception("Case: "+t.getClass.getName+" should be covered in 'TypeTransformer.transform()'.")
  }

  //TODO: Cover Variables
  private def transformFunction(fun:ScalaType, params:Set[ScalaType]):Type = fun match {
    case Function(params1, returnType) if (params1 != null && returnType != null) => 
      transformFunction(returnType, params ++ params1)
    case ScalaInstance(name, list) if (list != null && !list.isEmpty && name != null) => 
      if(params.isEmpty) Instance(name, list.map(transform))
      else Arrow(TSet(params.toList.map(transform)), Instance(name, list.map(transform)))
    case ScalaConst(name)  if(name != null) => 
      if(params.isEmpty) Const(name)
      else Arrow(TSet(params.toList.map(transform)), Const(name))
    case t => throw new Exception("Case: "+t.getClass.getName+" should be covered in 'TypeTransformer.transformFunction()'.")
  }
  
  def trasformInheritance(subType:ScalaType, superType:ScalaType):Type = IArrow(TSet(transform(subType)), transform(superType))
}