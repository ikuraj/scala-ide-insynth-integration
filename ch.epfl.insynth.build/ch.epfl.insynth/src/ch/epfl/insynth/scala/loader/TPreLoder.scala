package ch.epfl.insynth.scala.loader

import ch.epfl.insynth.InSynth
import ch.epfl.insynth.scala.loader.{ ScalaDeclaration => Declaration }

import insynth.util.logging.HasLogger

trait TPreLoder extends TExtractor with TDeclarationFactory with HasLogger {
  self:InSynth =>
    
  import compiler._
  
  class PreLoader() {
    
    //TODO: Ugly code and statistics, do it in much better way in the future.
    
    //TODO: We need to keep a set of all loaded packages/types/decls such that we do not load them twice
    def load() = {     
      val predefs = loadFromPackage(compiler.definitions.PredefModule)
      val scalaPkg = loadFromPackage(compiler.definitions.ScalaPackage)
      val javaPkg = loadFromPackage(compiler.definitions.JavaLangPackage)
      val allInitial = predefs ++ scalaPkg ++ javaPkg
      val supertypes = superTypes(allInitial.toSet)
      val all = allInitial ++ supertypes    
      val coercs = loadCoerctions(coerctions(all.toSet))
      val decls = loadDecls(all) ++ coercs
      
      WeightsLoader.loadPredefsDeclsWeights(decls)
      
      val literals = DeclarationFactory.getLiteralDecls
      
      WeightsLoader.loadLiteralWeights(literals)
      
      //TODO:Ugly
      PreLoader.loaded = all.toSet
      
      decls ++ literals
    }
    
    private def coerctions(loadedTypes:Set[Symbol]):List[Coerction] = {
      var coercs = List.empty[Coerction]
      loadedTypes.foreach{
        subclass =>
          subclass.tpe.parents.foreach {
            superclass =>   
              if (!PreLoader.pairs.contains((subclass.fullName, superclass.typeSymbol.fullName))) {
                coercs = new Coerction(subclass, superclass.typeSymbol) :: coercs
                PreLoader.pairs += ((subclass.fullName, superclass.typeSymbol.fullName))
              }
          }
      }
      coercs
    }    
    
    private def loadCoerctions(coercs:List[Coerction]): List[Declaration]= {
      var coerctions = List.empty[Declaration]
      coercs.foreach {
        coerction =>
            
        val declOption = DeclarationFactory.getCoerctionDecl(coerction)
          
        declOption match {
          case Some(decl) => coerctions = decl :: coerctions
          case None => 
        }
      }
      coerctions
    }    
    
    private def loadFromPackage(importSymbol:Symbol):List[Symbol] = {
	  var predefs = List[Symbol]()
	  val imports = importSymbol.tpe.decls
	  for (clazz <- imports){
	    // why is this try here?
	    try {
	      if(!clazz.nameString.contains("$")
	         && ask(() => clazz.exists)
	         && !clazz.isSynthetic
	         && (clazz.isClass || clazz.isModule)
	         && !clazz.isAbstractClass
	         && !clazz.isTrait
	         && !clazz.isAbstractType
	         && !clazz.isPackage)
	         predefs = clazz :: predefs
	    } catch {
	      case ex: Throwable =>
	        fine("exception " + ex)
	        fine(ex.getStackTrace.mkString("\n"))
	    }
	  }

	  predefs
    }
    
    private def loadDecls(types:List[Symbol]):List[Declaration] = {
      var decls = List.empty[Declaration]
      
      for {
        tpe <- types
        decl <- tpe.tpe.decls
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
          case Some(decl) => decls = decl :: decls
          case None => 
        }
      }
      decls
    }
    
    private def superTypes(loadedTypes:Set[Symbol]):List[Symbol] = {
      var types = Set[Symbol]()
      var setOfNames = loadedTypes.map(x=>x.fullName)
      var workingSet = loadedTypes

      while(!workingSet.isEmpty){
	    var curr = workingSet.head
	    workingSet = workingSet.tail
	    
	    //What "parents" contains?
//	    val parents = //curr.tpe.parents
//	 	  ask( () => curr.tpe.parents)
	 	
	    val superTypes = ask( () =>
        curr.tpe.parents.map(x => x.typeSymbol).toSet.filterNot(x => setOfNames.contains(x.fullName))
      )
	    
	    workingSet ++= superTypes
	    setOfNames ++= superTypes.map(x => x.fullName)
	    
	    types ++= superTypes
      }
      
      types.toList
    }    
  }
  
  object PreLoader{
    var loaded = Set.empty[Symbol] 
    var pairs = Set.empty[(String, String)]
  }
}