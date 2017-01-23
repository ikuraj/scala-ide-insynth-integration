package ch.epfl.insynth.loader

import scala.tools.nsc.interactive.Global
import ch.epfl.insynth.env.InitialEnvironmentBuilder
import ch.epfl.insynth.InSynth
import ch.epfl.insynth.typetransformations.TExtractor
import ch.epfl.insynth.trees.TypeTransformer
import ch.epfl.insynth.trees.{Type => InSynthType}
import ch.epfl.scala.trees.ScalaType
import ch.epfl.insynth.env.Declaration

trait TLoader extends TCollector with TExtractor with TDeclarationFactory {
  self:InSynth =>
  
  import compiler._
  
  class Loader() {
    
    private var collector = new Collector()
  
    def load(pos:Position, tree:Tree, builder:InitialEnvironmentBuilder):ScalaType = {
      
      val data = collector.gather(tree, pos)
      
      if (data.hasDesiredType) {

        if (data.hasThisType) {    
          //println("This type: "+ data.getThisType)      
          val thisOption = DeclarationFactory.getThisDecl(data.getThisType)
          
          thisOption match {
            case Some(_this) => 
              //TODO:add weight
              WeightsLoader.loadThisWeight(_this)
              builder.addDeclaration(_this)
            case None => 
          }
        }
        
        //println("Locals: ")
        var locals = List.empty[Declaration]
        data.getLocals.foreach {
          localSym =>
            //println(localSym.fullName)
            
            val localOption = DeclarationFactory.getLocalDecl(localSym)
          
            localOption match {
              case Some(local) => 
                locals :::= List(local)
                builder.addDeclaration(local)
              case None => 
            }
        }
        
        //TODO:Check if locals are ordered
        WeightsLoader.loadLocalWeights(locals)
        
        //println("Type decls:")
        var mntDecls = List.empty[Declaration]
        data.getMostNestedOwnerTypeDecls.foreach {
          sdecl =>
            //println(sdecl.getSymbol.fullName)
 
            val declOption = DeclarationFactory.getDecl(sdecl)
            
            declOption match {
              case Some(decl) => 
                mntDecls :::= List(decl)
                builder.addDeclaration(decl)
              case None => 
            }
        }
        
        WeightsLoader.loadMostNestedTypeDeclsWeights(mntDecls)
        
        //Load other nested decls
        
        WeightsLoader.loadOtherNestedTypeDeclsWeights(loadDecls(data.getOtherNestedOwnerTypes, builder))       
        
        WeightsLoader.loadPackageDeclsWeights(loadDecls(data.getPackageTypes, builder))
        
        WeightsLoader.loadImportedDeclsWeights(loadDecls(data.getImportedTypes, builder))
        
        WeightsLoader.loadSupertypeDeclsWeights(loadDecls(data.getSuperTypes, builder))
        
        //println("Desired type: "+data.getDesiredType)
                
        val desiredTypeOption = ScalaTypeExtractor.getLocalType(data.getDesiredType)

        WeightsLoader.loadCoerctionsWeights(loadCoerctions(Transform.coerctions(data.getLoadedTypes), builder)) 
        
        desiredTypeOption match {
          case Some(desiredType) => 
            desiredType
          case None => throw new Exception("Desired Type not found in: "+this.getClass.getName)
        }
      } else throw new Exception("Desired Type not found in: "+this.getClass.getName)
      
    }
    
    private def loadDecls(types:List[Symbol], builder:InitialEnvironmentBuilder) = {      
       var decls = List.empty[Declaration]
       for {
         tpe <- types
         val tpeDecls = ask( () => tpe.tpe.decls )
         decl <- tpeDecls
         if(!decl.nameString.contains("$") && 
	        decl.exists &&
	        decl.isPublic &&
	        !decl.isSynthetic &&
	        !(tpe.isModule && decl.isConstructor) &&
	        !decl.isSetter &&
	        decl.isValue)
        } {
          val declOption = DeclarationFactory.getDecl(new SimpleDecl(decl, tpe, tpe.isModule, false, decl.isConstructor))
          
          declOption match {
            case Some(decl) => 
              decls :::= List(decl)
              builder.addDeclaration(decl)
            case None => 
          }
        }
        
        decls
    }
    
    private def loadCoerctions(coercs:List[Coerction], builder:InitialEnvironmentBuilder) = {
      var decls = List.empty[Declaration]
      coercs.foreach {
        coerction =>
        val declOption = DeclarationFactory.getCoerctionDecl(coerction)
          
        declOption match {
          case Some(decl) => 
            decls :::= List(decl)
            builder.addDeclaration(decl)
          case None => 
        }
      }
      
      decls
    }
  }
}