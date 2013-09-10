package ch.epfl.insynth.scala.loader

import ch.epfl.insynth.InSynth
import ch.epfl.insynth.scala.TypeTransformer
import ch.epfl.insynth.scala.loader.{ScalaDeclaration => Declaration}

import insynth.util.logging.HasLogger

trait TDeclarationFactory extends TData with HasLogger {

  self:InSynth =>
    
  import compiler._  
  
  object DeclarationFactory {
    def getDecl(sdecl:SimpleDecl):Option[Declaration] = {
      val declOption = if (sdecl.needReceiver) makeDecl(sdecl.getSymbol.fullName, sdecl.getReceiver.tpe, ask(()=>sdecl.getSymbol.tpe) )
                 else makeDecl(sdecl.getSymbol.fullName, ask(()=>sdecl.getSymbol.tpe) )
      
      declOption match {
        case Some(decl) =>
          decl.setIsConstructor(sdecl.isConstructor)
          decl.setHasParentheses(sdecl.needParentheses)
          decl.setHasThis(sdecl.needThis)
          decl.setIsApply(sdecl.isApply)
          val inObject = sdecl.isInObject
          if (inObject){
            
            decl.setObjectName(sdecl.getReceiver.simpleName.toString)
            decl.setBelongsToObject(inObject)
          }
          
          decl.setIsMethod(sdecl.isMethod)
          decl.setIsField(!sdecl.isMethod)
                    
          Some(decl)
        case None => None
      }
    }
    
    def getThisDecl(tpe:Type):Option[Declaration]  = {
      val thisOption = makeLocalDecl("this", tpe)
      thisOption match {
        case Some(_this) =>
          _this.setIsThis(true)
          Some(_this)
        case None => None
      }
    }
  
    def getLocalDecl(sym:Symbol):Option[Declaration] = {
      val name = sym.fullName
      val tpe = ask(() => sym.tpe)
      val localOption = makeLocalDecl(name, tpe)
      localOption match {
        case Some(local) =>
          local.setIsLocal(true)
          Some(local)
        case None => None
      }
    }
    
    def makeDecl(name:String, tpe:Type):Option[Declaration] = makeDecl(name, null, tpe)
    
    def makeDecl(name:String, receiverType:Type, tpe:Type):Option[Declaration] = {
      val scalaTypeOption = ScalaTypeExtractor.getType(receiverType, tpe)
      scalaTypeOption match {
        case Some(scalaType) =>
          //TODO:Remove this "try" once we find the bug in TypeTransformer
          try{
            val inSynthType = TypeTransformer.transform(scalaType)
            Some(Declaration(name, inSynthType, scalaType))
          } catch {
            case ex =>
				      fine("exception " + ex)
				      fine(ex.getStackTrace.mkString("\n"))
              None
          }
        case None => None //throw new Exception("No type found for decl in: "+ this.getClass.getName)
      }     
    }
    
    def makeLocalDecl(name:String, tpe:Type):Option[Declaration] = {
      val scalaTypeOption = ScalaTypeExtractor.getLocalType(tpe)
      scalaTypeOption match {
        case Some(scalaType) =>
          //TODO:Remove this "try" once we find the bug in TypeTransformer
          try{          
            val inSynthType = TypeTransformer.transform(scalaType)
            Some(Declaration(name, inSynthType, scalaType))
          } catch {
            case ex =>
			        fine("exception " + ex)
			        fine(ex.getStackTrace.mkString("\n"))
			        None
          }            
        case None => None //throw new Exception("No type found for decl in: "+ this.getClass.getName)
      }     
    }    
  
    def getCoerctionDecl(coerction:Coerction):Option[Declaration] = {
      val superTypeOption = ScalaTypeExtractor.getLocalType(coerction.getSupertype)
      
      superTypeOption match {
        case Some(superType) =>
          val subTypeOption = ScalaTypeExtractor.getLocalType(coerction.getSubtype)          
          subTypeOption match {
            case Some(subType) =>
              try{
                val scalaType = ScalaTypeExtractor.getCoerctionType(subType, superType)
                val inSynthType = TypeTransformer.transform(scalaType)
                val decl = Declaration("#Coerction#", inSynthType, scalaType)
                decl.setInheritanceFun(true)
                Some(decl)
              } catch {
                case ex => 
					        fine("exception " + ex)
					        fine(ex.getStackTrace.mkString("\n"))
					        None
              }              
            case None => None
          }
        case None => None
      }
    }
    
 
  def getLiteralDecls() = {
    var decls = List.empty[Declaration]
    
    //Add Int literal
    val scalaIntType = ScalaTypeExtractor.getIntType
    val insynthIntType = TypeTransformer.transform(scalaIntType)
    val declInt = Declaration("0", insynthIntType, scalaIntType)
    declInt.setIsLiteral(true)
    decls = declInt :: decls      
    
    //Add Boolean literal
    val scalaBooleanType = ScalaTypeExtractor.getBooleanType
    val insynthBooleanType = TypeTransformer.transform(scalaBooleanType)
    val declBoolean = Declaration("false", insynthBooleanType, scalaBooleanType)
    declBoolean.setIsLiteral(true)
    decls = declBoolean :: decls
    
    //Add String literal
    val scalaStringType = ScalaTypeExtractor.getStringType
    val insynthStringType = TypeTransformer.transform(scalaStringType)
    val declString = Declaration("\"?\"", insynthStringType, scalaStringType)
    declString.setIsLiteral(true)    
    decls = declString :: decls    
    
    //Add Long literal
    val scalaLongType = ScalaTypeExtractor.getLongType
    val insynthLongType = TypeTransformer.transform(scalaLongType)
    val declLong = Declaration("0", insynthLongType, scalaLongType)
    declLong.setIsLiteral(true)
    decls = declLong :: decls    
    
    //Add Short literal
    val scalaShortType = ScalaTypeExtractor.getShortType
    val insynthShortType = TypeTransformer.transform(scalaShortType)
    val declShort = Declaration("0", insynthShortType, scalaShortType)
    declShort.setIsLiteral(true)
    decls = declShort :: decls
    
    //Add Double literal
    val scalaDoubleType = ScalaTypeExtractor.getDoubleType
    val insynthDoubleType = TypeTransformer.transform(scalaDoubleType)
    val declDouble = Declaration("0.0", insynthDoubleType, scalaDoubleType)
    declDouble.setIsLiteral(true)
    decls = declDouble :: decls
    
    //Add Float literal
    val scalaFloatType = ScalaTypeExtractor.getFloatType
    val insynthFloatType = TypeTransformer.transform(scalaFloatType)
    val declFloat = Declaration("0f", insynthFloatType, scalaFloatType)
    declFloat.setIsLiteral(true)
    decls = declFloat :: decls

    //Add Char literal
    val scalaCharType = ScalaTypeExtractor.getCharType
    val insynthCharType = TypeTransformer.transform(scalaCharType)
    val declChar = Declaration("'?'", insynthCharType, scalaCharType)
    declChar.setIsLiteral(true)
    decls = declChar :: decls 
    
    decls
  }
      
  }
 
}