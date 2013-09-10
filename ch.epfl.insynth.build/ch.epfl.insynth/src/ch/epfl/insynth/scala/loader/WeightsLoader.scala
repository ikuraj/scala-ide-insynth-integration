package ch.epfl.insynth.scala.loader

import scala.io.Source

import ch.epfl.insynth.InSynth
import ch.epfl.insynth.scala.loader.{ ScalaDeclaration => Declaration }

object WeightsLoader {

  private var examples = Map[String, Float]()

  loadExamples()

  def loadExamples() {
    val lines = Source.fromInputStream(this.getClass().getResourceAsStream("/resources/Examples.txt")).getLines

    lines.foreach {
      x =>
        val elem = x.split(";")
        examples += elem(0) -> (215.f + (785.f / (Integer.parseInt(elem(1)) + 1)))
    }
  }

  private final val THIS_WEIGHT = 5.f
  private final val LITERAL_WEIGHT = 200.f
  private final val LOCAL_WEIGHT = (5.f, 10.f)
  private final val MNT_DECL_WEIGHT = 20.f
  private final val NESTED_DECL_WEIGHT = 20.f
  private final val PACKAGE_DECL_WEIGHT = 25.f
  private final val COERCTION_WEIGHT = 10.f
  private final val ABSTRACT_DECL_WEIGHT = 1.f
  private final val IMPORTED_DECL_WEIGHT = 1000.f
  private final val PREDEF_DECL_WEIGHT = 1000.f
  private final val SUPERTYPE_DECL_WEIGHT = 1000.f

  //this
  def loadThisWeight(_this: Declaration) {
    _this.setWeight(new Weight(THIS_WEIGHT))
  }
  //literals
  def loadLiteralWeights(literals: List[Declaration]) {
    literals.foreach(x => x.setWeight(new Weight(LITERAL_WEIGHT)))
  }

  //I don't know what we should do with "null"?!

  //local
  //We assum they are orderd
  def loadLocalWeights(locals: List[Declaration]) {
    var localWeight = LOCAL_WEIGHT._1
    locals.reverse.foreach {
      x =>
        if (localWeight < LOCAL_WEIGHT._2) localWeight += 1.f
        x.setWeight(new Weight(localWeight))
    }
  }

  //most nested
  def loadMostNestedTypeDeclsWeights(decls: List[Declaration]) {
    decls.foreach(x => x.setWeight(new Weight(MNT_DECL_WEIGHT)))
  }

  //other nested
  def loadOtherNestedTypeDeclsWeights(decls: List[Declaration]) {
    decls.foreach(x => x.setWeight(new Weight(NESTED_DECL_WEIGHT)))
  }

  //package 
  def loadPackageDeclsWeights(decls: List[Declaration]) {
    decls.foreach(x => x.setWeight(new Weight(PACKAGE_DECL_WEIGHT)))
  }

  //coerctions    
  def loadCoerctionsWeights(decls: List[Declaration]) {
    decls.foreach(x => x.setWeight(new Weight(COERCTION_WEIGHT)))
  }

  //package imports
  def loadImportedDeclsWeights(decls: List[Declaration]) {
    decls.foreach {
      x =>
        val fullName = x.getFullNameForWeights
        x.setWeight(new Weight(if (examples.contains(fullName)) {
          examples(fullName)
        } else {
          IMPORTED_DECL_WEIGHT
        }))
    }
  }

  //supertypes
  def loadPredefsDeclsWeights(decls: List[Declaration]) {
    decls.foreach {
      x =>
        val fullName = x.getFullNameForWeights
        x.setWeight(new Weight(if (examples.contains(fullName)) {
          examples(fullName)
        } else {
          PREDEF_DECL_WEIGHT
        }))
    }
  }

  def loadSupertypeDeclsWeights(decls: List[Declaration]) {
    decls.foreach {
      x =>
        val fullName = x.getFullNameForWeights
        x.setWeight(new Weight(if (examples.contains(fullName)) {
          examples(fullName)
        } else {
          SUPERTYPE_DECL_WEIGHT
        }))
    }
  }
}