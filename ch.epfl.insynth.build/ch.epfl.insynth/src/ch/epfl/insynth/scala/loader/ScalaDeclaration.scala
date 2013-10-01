package ch.epfl.insynth.scala
package loader

import insynth.structures.{ SuccinctType => Type, _ }
import insynth.load.Declaration

import ch.epfl.insynth.scala
import ch.epfl.insynth.scala._

case class ScalaDeclaration(val fullName: String, override val inSynthType: Type, val scalaType: ScalaType,
  var weight: Weight = new Weight(1.0f)) extends Declaration(inSynthType) {
  assert(fullName != null && inSynthType != null)
  
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
  
  def this(fullName:String, scalaType: ScalaType) = this(fullName,
    TypeTransformer.transform(scalaType), scalaType)
  
  def this(scalaType: ScalaType) = {
    this("#abs#", scalaType)
    this._abstract = true
  }

  // TODO to be added
  def getObjectName = this.objectName  
  def setObjectName(objectName:String) {this.objectName = objectName}
        
  override def getWeight = weight.getValue
  
  def setWeight(weight:Weight){this.weight = weight}
  
  override def getType = inSynthType

  override def getDomainType = DomainTypeTransformer(scalaType) 

  override def getSimpleName = if (simpleName != null) simpleName 
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
      case Method(reciver, paramss, returnType) if (paramss != null) =>
        val list = paramss.flatten.map{
          x:ScalaType => x match {
              case scala.Const(name) => name
              case scala.Instance(name, params) => name
              case scala.Function(params, returnType) => "scala.Function"+params.length
              case _ => throw new UnsupportedOperationException
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
  
  override def isQuery = this.query
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