package ch.epfl.insynth.trees.tests

import ch.epfl.insynth.trees._
import ch.epfl.scala.trees.{Instance => ScalaInstance, Const => ScalaConst, _}

import org.junit.Test
import org.junit.Assert._

class TypeTransformerTest {

  private final val INT = ScalaConst("Int")
  private final val STRING = ScalaConst("String")
  private final val CHAR = ScalaConst("Char")
  private final val INTtoCHAR = Function(List(INT), CHAR)
  
  @Test
  def testTransformer1(){
    val scalaType = ScalaConst("Int")   
    assertEquals(TypeTransformer.transform(scalaType), Const("Int"))
  }
  
  @Test
  def testTransformer2(){  
    val scalaType = Method(ScalaConst("T1"), List(List(ScalaConst("Int"))), ScalaConst("Boolean"))
    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(List(Const("T1"), Const("Int"))), Const("Boolean")))
  }
  
  @Test
  def testTransformer3(){  
    val scalaType = Method(ScalaConst("T1"), List(List(ScalaConst("Int"))), ScalaConst("Boolean"))
    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(List(Const("Int"), Const("T1"))), Const("Boolean")))
  }
  
  @Test
  def testTransformer4(){  
    val scalaType = Method(null, List.empty[List[ScalaConst]], ScalaConst("Boolean"))
    assertEquals(TypeTransformer.transform(scalaType), Const("Boolean"))
  }
  
  @Test
  def testTransformer5(){  
    val scalaType = Method(ScalaConst("T1"), List.empty[List[ScalaConst]], ScalaConst("Boolean"))
    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(List(Const("T1"))), Const("Boolean")))
  }
  
  @Test
  def testTransformer6(){  
    val scalaType = Method(null, List(List(ScalaConst("Int"), ScalaConst("Char"), ScalaConst("String"))), ScalaConst("Boolean"))
    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(List(Const("Int"), Const("Char"), Const("String"))), Const("Boolean")))
  }
  
  @Test
  def testTransformer7(){  
    val scalaType = Method(null, List(List(Function(List(ScalaConst("Int")), ScalaConst("Char")), ScalaConst("String"))), ScalaConst("Boolean"))
    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(List(Arrow(TSet(Const("Int")), Const("Char")), Const("String"))), Const("Boolean")))
  }
  
  @Test
  def testTransformer8(){  
    val scalaType = Method(ScalaConst("T1"), List(List(ScalaConst("T1"))), ScalaConst("Boolean"))
    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(Const("T1")), Const("Boolean")))
  }
  
  @Test
  def testTransformer9(){  
    val scalaType = Function(List(ScalaConst("Char"), ScalaConst("Int")), ScalaConst("Boolean"))
    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(List(Const("Char"), Const("Int"))), Const("Boolean")))
  }
  
  @Test
  def testTransformer10(){  
    val scalaType = Function(List(ScalaConst("Char"), ScalaConst("Int")), Function(List(ScalaConst("String"), ScalaConst("Object")),ScalaConst("Boolean")))
    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(List(Const("Char"), Const("Int"), Const("String"), Const("Object"))), Const("Boolean")))
  }
  
  @Test
  def testTransformer11(){  
    val scalaType = Method(ScalaConst("T1"), List(List(ScalaConst("T2"))), Function(List(ScalaConst("Char"), ScalaConst("Int")), Function(List(ScalaConst("String"), ScalaConst("Object")),ScalaConst("Boolean"))))
    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(List(Const("T1"), Const("T2"), Const("Char"), Const("Int"), Const("String"), Const("Object"))), Const("Boolean")))
  }  
    
  @Test
  def testTransformer12(){  
    val scalaType = Method(ScalaConst("T1"), List(List(ScalaConst("T2"))), Function(List(ScalaConst("Char"), ScalaConst("T1")), Function(List(ScalaConst("String"), ScalaConst("Object")),ScalaConst("Boolean"))))
    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(List(Const("T1"), Const("T2"), Const("Char"), Const("String"), Const("Object"))), Const("Boolean")))
  }
  
  @Test
  def testTransformer13(){  
    val scalaType = Method(ScalaConst("T1"), List(List(ScalaConst("T2"))), Function(List(ScalaConst("Char"), ScalaConst("Int")), Function(List(ScalaConst("String"), ScalaConst("Object")),ScalaConst("Boolean"))))
    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(List(Const("Int"), Const("String"), Const("T1"), Const("Object"), Const("T2"), Const("Char"))), Const("Boolean")))
  }
  
  @Test
  def testTransformer14(){
    val scalaType = ScalaInstance("Int", List(ScalaConst("Boolean"), ScalaConst("String")))   
    assertEquals(TypeTransformer.transform(scalaType), Instance("Int", List(Const("Boolean"), Const("String"))))
  }
  
  @Test
  def testTransformer15(){  
    val scalaType = Method(ScalaConst("T1"), List(List(ScalaConst("T2"))), Function(List(Function(List(ScalaConst("Char"), ScalaInstance("Int", List(ScalaConst("Boolean"), ScalaConst("String")))), ScalaConst("Object")), ScalaConst("T1")), Function(List(ScalaConst("String"), ScalaConst("Object")),ScalaConst("Boolean"))))
    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(List(Const("T1"), Const("T2"), Arrow(TSet(List(Const("Char"), Instance("Int", List(Const("Boolean"), Const("String"))))), Const("Object")), Const("String"), Const("Object"))), Const("Boolean")))
  }
  
  @Test
  def testTransformer16(){
    val scalaType1 = INTtoCHAR
    val scalaType2 = Method(null, List(List(INT)), CHAR)
    assertEquals(TypeTransformer.transform(scalaType2), TypeTransformer.transform(scalaType1))
  }  
  
}