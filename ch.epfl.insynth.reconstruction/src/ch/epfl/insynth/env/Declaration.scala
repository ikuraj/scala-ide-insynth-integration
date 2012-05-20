package ch.epfl.insynth.env

import ch.epfl.insynth.trees._
import ch.epfl.scala.trees.ScalaType

/**
 * @param fullName
 * @param inSynthType
 */
case class Declaration(val fullName:String, val inSynthType:Type, val scalaType:ScalaType) {
  assert(fullName != null && inSynthType != null)
  
  private var weight:Weight = null
  
  private var method = false
  private var field = false
  private var inheritanceFun = false
  private var literal = false
  private var needThis = false
  private var needParentheses = false
  private var receiverObject = false
  private var apply = false
  private var query = false
  
  private val simpleName = fullName.substring(fullName.lastIndexOf(".")+1)
  
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

  def getWeight = weight
  
  def setWeight(weight:Weight){this.weight = weight}
  
  def getType = inSynthType
    
  def getSimpleName = simpleName

  def getReturnType = returnType
  
  /**
   * @return list of parameter types
   */
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
  
  def isReceiverObject = this.receiverObject
  def setIsReceiverObject(receiverObject:Boolean){this.receiverObject = receiverObject}
  
  def isApply = this.apply
  def setIsApply(apply:Boolean){this.apply = apply}
  
  def isQuery = this.query
  def setIsQuery(query:Boolean){this.query = query}
}