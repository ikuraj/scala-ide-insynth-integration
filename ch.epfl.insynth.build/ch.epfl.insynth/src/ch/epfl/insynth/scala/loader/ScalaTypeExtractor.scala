package ch.epfl.insynth.scala.loader

import scala.tools.nsc.interactive.Global

import ch.epfl.insynth.scala.{
  Const => ScalaConst, Method => ScalaMethod, Function => ScalaFunction, Instance => ScalaInstance, ScalaType
}
import ch.epfl.insynth.InSynth

import insynth.util.logging.HasLogger

trait TExtractor extends HasLogger {
  
  self:InSynth =>

  import compiler._  
    
object ScalaTypeExtractor {

    //TODO:This is ugly
    private var allTypes = Set.empty[Symbol]

    def getAllTypes = allTypes
    
    def clear(){
      allTypes = Set.empty[Symbol]
    }
    //Here ends the ugly code
 
  def getIntType = ScalaConst(SugarFree(Int.getClass.getName.replace("$","")))
  def getBooleanType = ScalaConst(SugarFree(Boolean.getClass.getName.replace("$","")))
  def getLongType = ScalaConst(SugarFree(Long.getClass.getName.replace("$","")))
  def getStringType = ScalaConst(SugarFree("".getClass.getName))
  def getCharType = ScalaConst(SugarFree(Char.getClass.getName.replace("$","")))
  def getDoubleType = ScalaConst(SugarFree(Double.getClass.getName.replace("$","")))
  def getFloatType = ScalaConst(SugarFree(Float.getClass.getName.replace("$","")))
  def getShortType = ScalaConst(SugarFree(Short.getClass.getName.replace("$","")))
  
  def getCoerctionType(superType:ScalaType, subType:ScalaType):ScalaType = {
    assert(superType != null && subType != null)
    ScalaFunction(List(superType), subType)
  }  
    
    
  def getLocalType(tpe:Type):Option[ScalaType] = {
    assert(tpe != null)
    try{
      Some(traverse(tpe))
    } catch {
      case ex =>
        fine("exception " + ex)
        fine(ex.getStackTrace.mkString("\n"))
        None
    }
  }

  def getType(tpe:Type):Option[ScalaType] = getType(null, tpe)
  
  def getType(receiverType:Type, tpe:Type):Option[ScalaType] = {
    assert(tpe != null)
    try {
      Some(ScalaMethod(if (receiverType != null) 
        traverse(receiverType) else null, 
        getParamList(tpe), 
        getReturnType(tpe)))
    } catch {
      case ex: Throwable =>
        fine("exception " + ex)
        fine(ex.getStackTrace.mkString("\n"))
        None
    }
  }   
  
  private def isRepeated(sym:Symbol) = sym.tpe.typeSymbol.fullName == "scala.<repeated>" 
    // caller will wrap into ask
    //ask( () => sym.tpe.typeSymbol.fullName == "scala.<repeated>")  
  
  //TODO: What to do with implicit params and type* ?
  private def getParamList(tpe:Type): List[List[ScalaType]] = {
    val paramss = tpe.paramss
    ask( () => paramss.map(params => params.filterNot(param => param.isImplicit || isRepeated(param)).map(param => traverse(param.tpe))) )
  }
  
  private def onlyReturnType(rawReturn:Type):Type = rawReturn match {
    case MethodType(_, resultType) => onlyReturnType(resultType)
    case t => t
  }
  
  private def getReturnType(tpe:Type): ScalaType = {
    val returnType = onlyReturnType(tpe.resultType)
      
    traverse(returnType)
  }

  private def traverse(tpe:Type):ScalaType = {
    tpe match {
	  //Polymorphic
	  case PolyType(typeParams: List[Symbol], resultType: Type) =>
        traverse(resultType)
	    
      //Function type
	  case TypeRef(pre: Type, sym: Symbol, args: List[Type])
	    if( ask( () => definitions.isFunctionType(tpe) ) ) =>
	    val list = args.init.map(traverse)
	    val result = traverse(args.last)
	      
	    ScalaFunction(list, result)
	      
	  //TODO: => Type  
	  /*case TypeRef(pre: Type, sym: Symbol, args: List[Type])
	    if (!sym.isMonomorphicType && args.length == 1 && sym.fullName == "scala.<byname>")=>
	      traverse(args(0))
	  */
	    
	  //Polymorphic instantiated types
	  case TypeRef(pre: Type, sym: Symbol, args: List[Type])
	    if (!sym.isMonomorphicType && !args.isEmpty)=>
	      ScalaInstance(SugarFree(sym.fullName), args.map(traverse))
	     
	  //Base types
	  case TypeRef(pre: Type, sym: Symbol, args: List[Type]) =>
	    if (!sym.isTypeParameter) {
	      //TODO:Ugly
	      allTypes += sym
	      
	      ScalaConst(SugarFree(sym.fullName))
	    } else throw new Exception("<<Parametrized types not supported: "+ ask ( () => tpe.getClass.getName) +">>")
	    
	  case _ => throw new Exception("<<Not supported: "+ ask ( () => tpe.getClass.getName) +">>") 
    }
  }  
}
  
  object SugarFree {
    private val map = Map("scala.Predef.String"        -> "java.lang.String",
                          "scala.Predef.Set"           -> collection.immutable.Set.getClass.getName.replace("$",""),
                          "scala.Predef.Map"           -> collection.immutable.Map.getClass.getName.replace("$",""),
                          "scala.Predef.Manifest"      -> scala.reflect.Manifest.getClass.getName.replace("$",""),
                          "scala.Predef.ClassManifest" -> scala.reflect.ClassManifest.getClass.getName.replace("$",""),
                          "scala.Predef.Pair"          -> scala.Tuple2.getClass.getName.replace("$",""),
                          "scala.Predef.Triple"        -> scala.Tuple3.getClass.getName.replace("$",""),
                          "scala.Predef.Class"         -> "java.lang.Class")

    def apply(name:String):String = {
      if (map.contains(name)) map(name)
      else name
    }
  }
    
  
  
}