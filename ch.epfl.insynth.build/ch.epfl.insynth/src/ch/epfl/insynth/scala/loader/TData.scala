package ch.epfl.insynth.scala.loader

import scala.tools.nsc.interactive.Global

import ch.epfl.insynth.InSynth

trait TData {
  self: InSynth =>

  import compiler._

  class RawData {

    private var fieldCompletition = false
    private var methodCompletition = false
    private var localCompletition = false

    /*
   * imports
   */
    private var imports = List[Import]()

    /*
   * package 
   */
    private var pkg: Symbol = null

    /*
   * classes on the path to the query point
   */
    private var ownerTypes = List[Symbol]()

    /*
   * field whose initializer must be completed
   */
    private var fieldToComplete: Symbol = null

    /*
   * chain of all methods that contain program point
   */
    private var methodToComplete: Symbol = null

    /*
   * local that contain program point
   */
    private var localToComplete: Symbol = null

    /*
   * vals and vars from the last method body prior to var/val that should be completed
   */
    private var localContext = List[Symbol]()

    private var local: Symbol = null

    /*
   * a type of expression that needs to be completed at a given point
   */
    private var desiredType: Type = null

    def isFieldCompletition = this.fieldCompletition
    def setFieldCompletition(fieldCompletition: Boolean) {
      this.fieldCompletition = fieldCompletition
    }

    def isMethodCompletition = this.methodCompletition
    def setMethodCompletition(methodCompletition: Boolean) {
      this.methodCompletition = methodCompletition
    }

    def isLocalCompletition = this.localCompletition
    def setLocalCompletition(localCompletition: Boolean) {
      this.localCompletition = localCompletition
    }

    def hasPackage = this.pkg != null
    def getPackage = this.pkg
    def setPackage(pkg: Symbol) {
      this.pkg = pkg
    }

    def hasOwnerTypes = !this.ownerTypes.isEmpty
    def getOwnerTypes = this.ownerTypes
    def addOwnerTypes(tpe: Symbol) {
      this.ownerTypes = tpe :: this.ownerTypes
    }

    def getMostNestedOwnerType = if (hasOwnerTypes) this.ownerTypes.head else null
    def getTopMostOwnerType = if (hasOwnerTypes) this.ownerTypes.last else null
    def getOtherOwnerTypes = if (hasOwnerTypes) this.ownerTypes.tail else null

    def hasFieldToComplete = this.fieldToComplete != null
    def getFieldToComplete = this.fieldToComplete
    def setFieldToComplete(field: Symbol) {
      this.fieldToComplete = field
      this.fieldCompletition = true
    }

    def hasMethodToComplete = this.methodToComplete != null
    def getMethodToComplete = this.methodToComplete
    def setMethodToComplete(method: Symbol) {
      this.methodToComplete = method
      this.methodCompletition = true
    }

    def hasLocalToComplete = this.localToComplete != null
    def getLocalToComplete = this.localToComplete
    def setLocalToComplete(local: Symbol) {
      this.localToComplete = local
      this.localCompletition = true
    }

    def hasImports = !this.imports.isEmpty
    def getImports = this.imports
    def addImport(imp: Import) {
      this.imports = imp :: this.imports
    }

    def hasLocalContext = !this.localContext.isEmpty
    def getLocalContext = this.localContext
    def addToLocalContext(decl: Symbol) {
      this.localContext = decl :: this.localContext.filterNot(x => decl.fullName.equals(x.fullName)) //TODO: or simpleName?
    }

    def hasDesiredType = this.desiredType != null
    def getDesiredType = this.desiredType
    def setDesiredType(desiredType: Type) {
      this.desiredType = desiredType
    }
  }

  class SimpleDecl(symbol: Symbol, receiver: Symbol, inObject: Boolean, hasThis: Boolean, constructor: Boolean) {
    def getSymbol = symbol
    def getReceiver = receiver
    def needThis = hasThis
    def isInObject = inObject
    def isConstructor = constructor
    def needReceiver = !inObject && !constructor
    def needParentheses: Boolean = ask(() => this.symbol.tpe.paramSectionCount != 0)
    def isApply = this.symbol.simpleName.toString.equals("apply")
    def isMethod = symbol.isMethod
  }

  class Coerction(subclass: Symbol, superclass: Symbol) {
    def getSubclass = subclass
    def getSubtype = ask(() => subclass.tpe)
    def getSuperclass = superclass
    def getSupertype = ask(() => superclass.tpe)
  }

  class Data {

    //Load all of them
    private var mostNestedOwnerTypeDecls = List.empty[SimpleDecl]

    //TODO: Load these
    //all except the most nested type
    private var ownerTypes = List.empty[Symbol]

    //package types
    private var packageTypes = List.empty[Symbol]

    //owner types
    private var importedTypes = List.empty[Symbol]

    //super types
    private var superTypes = List.empty[Symbol]

    //Load only public decls of these classes
    private var coerctions = List.empty[Coerction]

    //Load all of them
    private var locals = List.empty[Symbol]

    //type of "this"
    private var thisType: Type = null

    //a user desired type
    private var desiredType: Type = null

    private var loadedTypes = Set.empty[Symbol]

    def getMostNestedOwnerTypeDecls = mostNestedOwnerTypeDecls
    def setMostNestedOwnerTypeDecls(typeDecls: List[SimpleDecl]) {
      this.mostNestedOwnerTypeDecls = typeDecls
    }

    def getOtherNestedOwnerTypes = ownerTypes
    def setOtherNestedOwnerTypes(types: List[Symbol]) {
      this.ownerTypes = types
    }

    //package types except the top owner type
    def getPackageTypes = packageTypes
    def hasPackageTypes = !packageTypes.isEmpty
    def setPackageTypes(types: List[Symbol]) {
      this.packageTypes = types
    }

    //All except the most nested owner type
    def getImportedTypes = importedTypes
    def hasImportedTypes = !importedTypes.isEmpty
    def setImportedTypes(types: List[Symbol]) {
      this.importedTypes = types
    }

    //All except the most nested owner type
    def getSuperTypes = superTypes
    def hasSuperTypes = !superTypes.isEmpty
    def setSuperTypes(types: List[Symbol]) {
      this.superTypes = types
    }

    def getCoerctions = coerctions
    def setCoerctions(coerctions: List[Coerction]) {
      this.coerctions = coerctions
    }

    def getLocals = locals
    def setLocals(locals: List[Symbol]) {
      this.locals = locals
    }

    def getThisType = thisType
    def hasThisType = thisType != null
    def setThisType(thisType: Type) {
      this.thisType = thisType
    }

    def getDesiredType = desiredType
    def hasDesiredType = desiredType != null
    def setDesiredType(desiredType: Type) {
      this.desiredType = desiredType
    }

    def getLoadedTypes = loadedTypes
    def setLoadedTypes(loadedTypes: Set[Symbol]) {
      this.loadedTypes = loadedTypes
    }

  }

  object Transform {

    def apply(rdata: RawData): Data = {
      val data = new Data()

      if (rdata.hasDesiredType) {

        //0) find desired type
        data.setDesiredType(rdata.getDesiredType)

        //1) find all visible classes (without the inner most class) + all super-classes
        //2) find inherited functions
        //3) find all local decls
        val locals = filterLocalDecls(rdata)
        data.setLocals(locals)

        var loadedTypes = Set.empty[Symbol]

        //4) find decls for the most-inner class, some of them may be invisible due to the local decls, or invisible field that needs to be completed and in local completition local val      
        if (rdata.hasOwnerTypes) {
          val mostNestedOwnerType = rdata.getMostNestedOwnerType

          loadedTypes += mostNestedOwnerType

          val tpeDecls = mostNestedOwnerTypeDecls(rdata, locals)
          data.setMostNestedOwnerTypeDecls(tpeDecls)

          if (!mostNestedOwnerType.isModule) {
            data.setThisType(mostNestedOwnerType.tpe)
          }

          val ownertypes = ownerTypes(rdata)

          loadedTypes ++= ownertypes

          data.setOtherNestedOwnerTypes(ownertypes)

          //6) package types
          val packagetypes = packageTypes(rdata)
          loadedTypes ++= packagetypes
          data.setPackageTypes(packagetypes)

          if (rdata.hasImports) {
            val importedtypes = importedTypes(rdata, loadedTypes ++ PreLoader.loaded)
            loadedTypes ++= importedtypes
            data.setImportedTypes(importedtypes)
          }

          //What if superTypes are already loaded, should we give them priority over package and imported decls?!
          val supertypes = superTypes(loadedTypes)
          data.setSuperTypes(supertypes)
          loadedTypes ++= supertypes

          //data.setCoerctions(coerctions(loadedTypes))          

          data.setLoadedTypes(loadedTypes)
        }

      }

      data
    }

    private def filterLocalDecls(rdata: RawData) = {
      val locals = rdata.getLocalContext
      val localToComplete = rdata.getLocalToComplete

      if (rdata.isLocalCompletition) locals.filterNot(x => localToComplete.fullName.equals(x.fullName))
      else locals
    }

    private def mostNestedOwnerTypeDecls(rdata: RawData, locals: List[Symbol]): List[SimpleDecl] = {
      val tpe = rdata.getMostNestedOwnerType

      val decls = ask(() => tpe.tpe.decls.toList)

      for {
        decl <- decls
        if (!decl.nameString.contains("$") &&
          decl.exists &&
          !decl.isSynthetic &&
          !(tpe.isModule && decl.isConstructor) &&
          !(rdata.isFieldCompletition && rdata.getFieldToComplete.fullName.equals(decl.fullName)) &&
          !(rdata.isMethodCompletition && rdata.getMethodToComplete.fullName.equals(decl.fullName)) &&
          !decl.isGetter &&
          !decl.isSetter &&
          decl.isValue) //What was this? I guess with this we get rid of type defs and other junk.
      } yield new SimpleDecl(decl, tpe, tpe.isModule, filterTypeDecl(decl, locals, rdata), decl.isConstructor)
    }

    private def ownerTypes(rdata: RawData): List[Symbol] = {
      var types = List[Symbol]()
      val otherNestedTypes = rdata.getOtherOwnerTypes
      if (otherNestedTypes != null) {
        types ++= otherNestedTypes
        val tpes = otherNestedTypes.map(x => x.tpe.decls).flatten
        val mostNestedOwnerType = rdata.getMostNestedOwnerType
        for {
          tpe <- tpes
          if (!tpe.nameString.contains("$")
            && ask(() => tpe.exists)
            && (tpe.isClass || tpe.isModule || tpe.isAbstractClass || tpe.isTrait)
            && !tpe.fullName.equals(mostNestedOwnerType.fullName)
            && !(otherNestedTypes.exists(x => x.fullName.equals(tpe.fullName)))
            && !tpe.isSynthetic
            && !tpe.isAbstractType
            && !tpe.isPackage)
        } types = tpe :: types
      }
      types
    }

    private def packageTypes(rdata: RawData): List[Symbol] = {
      var types = List[Symbol]()
      if (rdata.hasPackage) {
        val pkg = rdata.getPackage
        val topOwnerType = rdata.getTopMostOwnerType
        for {
          tpe <- pkg.tpe.decls
          if (!tpe.nameString.contains("$")
            && ask(() => tpe.exists)
            && !tpe.fullName.equals(topOwnerType.fullName)
            && (tpe.isClass || tpe.isModule || tpe.isAbstractClass || tpe.isTrait)
            && !tpe.isSynthetic
            && !tpe.isAbstractType
            && !tpe.isPackage)
        } types = tpe :: types
      }
      types
    }

    private def importedTypes(rdata: RawData, loadedTypes: Set[Symbol]) = {
      var types = List[Symbol]()
      if (rdata.hasImports) {
        val imports = rdata.getImports
        for {
          imp <- imports
          tpe <- imp.expr.tpe.decls
          if (!tpe.nameString.contains("$")
            && ask[Boolean](() => tpe.exists)
            && selected(tpe, imp.selectors)
            && !loadedTypes.exists(x => tpe.fullName.equals(x.fullName))
            && (tpe.isClass || tpe.isModule || tpe.isAbstractClass || tpe.isTrait)
            && !tpe.isSynthetic
            && !tpe.isAbstractType
            && !tpe.isPackage)
        } types = tpe :: types
      }
      types
    }

    private def selected(tpe: Symbol, selectors: List[ImportSelector]): Boolean = {
      selectors.exists(x => x.name.toString.equals("_") || x.name.toString.equals(tpe.simpleName.toString))
    }

    //TODO: Check if we load all classes 
    private def superTypes(loadedTypes: Set[Symbol]): List[Symbol] = {
      var types = Set[Symbol]()
      var setOfNames = loadedTypes.map(x => x.fullName)
      var workingSet = loadedTypes

      while (!workingSet.isEmpty) {
        var curr = workingSet.head
        workingSet = workingSet.tail

        //What "parents" contains?
        val parents = //curr.tpe.parents
          ask(() => curr.tpe.parents)

        val superTypes = parents.map(x => x.typeSymbol).toSet.filterNot(x => setOfNames.contains(x.fullName))

        workingSet ++= superTypes
        setOfNames ++= superTypes.map(x => x.fullName)

        types ++= superTypes
      }

      types.toList
    }

    def coerctions(loadedTypes: Set[Symbol]): List[Coerction] = {
      var coercs = List.empty[Coerction]
      var pairs = PreLoader.pairs

      val types = loadedTypes ++ ScalaTypeExtractor.getAllTypes
      types.foreach {
        subclass =>
          ask(() =>
            subclass.tpe.parents.foreach {
              superclass =>
                if (!pairs.contains((subclass.fullName, superclass.typeSymbol.fullName))) {
                  coercs = new Coerction(subclass, superclass.typeSymbol) :: coercs
                  pairs += ((subclass.fullName, superclass.typeSymbol.fullName))
                }
            })
      }
      coercs
    }

    private def filterTypeDecl(decl: Symbol, locals: List[Symbol], rdata: RawData) = {
      !decl.isConstructor &&
        (locals.exists(y => simpleName(y).equals(simpleName(decl))) ||
          (rdata.isLocalCompletition && rdata.getLocalToComplete.fullName.equals(decl.fullName)) ||
          (rdata.isMethodCompletition && rdata.getMethodToComplete.fullName.equals(decl.fullName)) ||
          (rdata.isFieldCompletition && rdata.getFieldToComplete.fullName.equals(decl.fullName)))
    }

    private def simpleName(decl: Symbol) = decl.simpleName.toString.replace(" ", "")

  }

  object TData {
    /*  
    private val unitTupe = Unit.getClass.getName.replace(".runtime.",".").replace("$","")

    def returnsUnit(decl:Symbol) = 
      if (decl.tpe != null &&
        decl.tpe.resultType != null &&
        decl.tpe.resultType.typeSymbol != null) decl.tpe.resultType.typeSymbol.fullName.equals(unitTupe)
	  else false
*/
  }
}