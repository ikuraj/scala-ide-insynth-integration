package ch.epfl.insynth.test

import ch.epfl.insynth.trees._

import org.junit.Assert._
import org.junit.Test


import scala.tools.eclipse.semantichighlighting.classifier.AbstractSymbolClassifierTest
import scala.tools.eclipse.semantichighlighting.classifier.SymbolTypes._

class MyClass extends AbstractSymbolClassifierTest {
  
  def lazy_template_val() {
    println("ivaaaaan")
    
    checkSymbolClassification("""
        class A {
          lazy val immutableVal = 42
          immutableVal
        }""", """
        class A {
          lazy val $   TVAL   $ = 42
          $   TVAL   $
        }""",
      Map("TVAL" -> LazyTemplateVal))
  }
  
}

class TsdfsdfdsfsdfdsTest {
  
  @Test
  def testTree1 {
    
		    (new MyClass).lazy_template_val
		    (new MyClass).lazy_template_val
		    (new MyClass).lazy_template_val
  }
  
}