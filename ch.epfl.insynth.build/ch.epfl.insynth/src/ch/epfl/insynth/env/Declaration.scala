package ch.epfl.insynth.env

import ch.epfl.insynth.trees._
import ch.epfl.scala.trees.{Method => ScalaMethod, Const => ScalaConst , Instance =>ScalaInstance, Function => ScalaFunction}
import ch.epfl.scala.trees.ScalaType

case class Declaration(val fullName:String, val inSynthType:Type, val scalaType:ScalaType){
  assert(fullName != null && inSynthType != null)
  
  private var weight:Weight = new Weight(1.0)
  
  private var method = false
  private var field = false
  private var inheritanceFun = false
  private var literal = false
  private var needThis = false
  private var needParentheses = false
  private var receiverObject = false
  private var apply = false
  private var query = false
  private var local = false
  private var _this = false
  private var _abstract = false
  private var constructor = false
  private var objectName:String = null
  
  private var simpleName:String = null

  private val returnType = inSynthType match {
    case Arrow(_,retType) => retType
    case IArrow(_,retType) => retType
    case retType => retType
  }
  
  private val paramTypes = inSynthType match {
    case Arrow(TSet(params),_) => params
    case IArrow(TSet(params),_) => params
    case _ => Nil
  }  
  
  private val paramSetTypes = inSynthType match {
    case Arrow(params,_) => params
    case IArrow(params,_) => params
    case _ => TSet.empty
  }
  
  def this(fullName:String, inSynthType:Type) = this(fullName, inSynthType, null)
  
  def this(inSynthType:Type) = {
    this("#abs#", inSynthType)
    this._abstract = true
  }

  // TODO to be added
  def getObjectName = this.objectName  
  def setObjectName(objectName:String) {this.objectName = objectName}
    
    
  def getWeight = weight
  
  def setWeight(weight:Weight){this.weight = weight}
  
  def getType = inSynthType

  def getSimpleName = if (simpleName != null) simpleName 
                      else {
                        val temp = if (constructor) fullName.substring(0, fullName.lastIndexOf("."))
                                   else fullName
                        simpleName = temp.substring(temp.lastIndexOf(".")+1)
                        simpleName
                      }

  def getFullNameForWeights = if (_abstract) fullName else fullName+paramsForWeights
  
  private def paramsForWeights= {
    assert(scalaType != null)
    
    scalaType match {
      case ScalaMethod(reciver, paramss, returnType) if (paramss != null) =>
        val list = paramss.flatten.map{
          x:ScalaType => x match {
              case ScalaConst(name) => name
              case ScalaInstance(name, params) => name
              case ScalaFunction(params, returnType) => "scala.Function"+params.length
            }
        }
        list.mkString(" ",",","")
      case _ => ""
    }
  }
  
  
  
  def getReturnType = returnType
  
  def getParamTypes = paramTypes
  
  def getParamSetTypes = paramSetTypes
  
  def isMethod = this.method
  def setIsMethod(method:Boolean){this.method = method}
  
  def isField = this.field
  def setIsField(field:Boolean){this.field = field}
  
  def isInheritanceFun= this.inheritanceFun
  def setInheritanceFun(inheritanceFun:Boolean){this.inheritanceFun = inheritanceFun}
  
  def isLiteral = this.literal
  def setIsLiteral(literal:Boolean){this.literal = literal}
  
  def hasThis = this.needThis
  def setHasThis(needThis:Boolean){this.needThis = needThis}
  
  def hasParentheses = this.needParentheses
  def setHasParentheses(needParentheses:Boolean){this.needParentheses = needParentheses}  
  
  def belongsToObject = this.receiverObject
  def setBelongsToObject(receiverObject:Boolean){this.receiverObject = receiverObject}
  
  def isApply = this.apply
  def setIsApply(apply:Boolean){this.apply = apply}
  
  def isQuery = this.query
  def setIsQuery(query:Boolean){this.query = query}

  def isLocal = this.local
  def setIsLocal(local:Boolean){this.local= local}  
  
  def isAbstract = this._abstract
  def setIsAbstract(_abstract:Boolean){this._abstract= _abstract}
  
  def isThis = this._this
  def setIsThis(_this:Boolean){this._this = _this}
  
  def isConstructor = this.constructor
  def setIsConstructor(constructor:Boolean){this.constructor = constructor}
}