package ch.epfl.insynth.scala

import ch.epfl.insynth.scala.{ Instance => ScalaInstance, Const => ScalaConst, _ }

import insynth.{ structures => succ }
import insynth.structures._

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

    assertEquals(TypeTransformer.transform(scalaType),
      succ.Arrow(TSet(List(succ.Const("T1"), succ.Const("Int"))), succ.Const("Boolean")))
  }
  
  @Test
  def testTransformer3(){  
    val scalaType = Method(ScalaConst("T1"), List(List(ScalaConst("Int"))), ScalaConst("Boolean"))

    assertEquals(TypeTransformer.transform(scalaType),
      Arrow(succ.TSet(List(succ.Const("Int"), succ.Const("T1"))), succ.Const("Boolean")))
  }
  
  @Test
  def testTransformer4(){  
    val scalaType = Method(null, List.empty[List[ScalaConst]], ScalaConst("Boolean"))

    assertEquals(TypeTransformer.transform(scalaType), Const("Boolean"))
  }
  
  @Test
  def testTransformer5(){  
    val scalaType = Method(ScalaConst("T1"), List.empty[List[ScalaConst]], ScalaConst("Boolean"))

    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(List(succ.Const("T1"))), succ.Const("Boolean")))
  }
  
  @Test
  def testTransformer6(){  
    val scalaType = Method(null, List(List(ScalaConst("Int"), ScalaConst("Char"), ScalaConst("String"))), ScalaConst("Boolean"))
    assertEquals(TypeTransformer.transform(scalaType),
      Arrow(TSet(List(succ.Const("Int"), succ.Const("Char"), succ.Const("String"))), succ.Const("Boolean")))
  }
  
  @Test
  def testTransformer7(){  
    val scalaType = Method(null, List(List(Function(List(ScalaConst("Int")), ScalaConst("Char")), ScalaConst("String"))), ScalaConst("Boolean"))

    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(List(Arrow(TSet(succ.Const("Int")), succ.Const("Char")), succ.Const("String"))), succ.Const("Boolean")))
  }
  
  @Test
  def testTransformer8(){  
    val scalaType = Method(ScalaConst("T1"), List(List(ScalaConst("T1"))), ScalaConst("Boolean"))
    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(succ.Const("T1")), succ.Const("Boolean")))
  }
  
  @Test
  def testTransformer9(){  
    val scalaType = Function(List(ScalaConst("Char"), ScalaConst("Int")), ScalaConst("Boolean"))
    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(List(succ.Const("Char"), succ.Const("Int"))), succ.Const("Boolean")))
  }
  
  @Test
  def testTransformer10(){  
    val scalaType = Function(List(ScalaConst("Char"), ScalaConst("Int")), Function(List(ScalaConst("String"), ScalaConst("Object")),ScalaConst("Boolean")))

    assertEquals(TypeTransformer.transform(scalaType),
      Arrow(TSet(List(succ.Const("Char"), succ.Const("Int"), succ.Const("String"), succ.Const("Object"))), succ.Const("Boolean")))
  }
  
  @Test
  def testTransformer11(){  
    val scalaType = Method(ScalaConst("T1"), List(List(ScalaConst("T2"))), Function(List(ScalaConst("Char"), ScalaConst("Int")), Function(List(ScalaConst("String"), ScalaConst("Object")),ScalaConst("Boolean"))))
    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(List(succ.Const("T1"), succ.Const("T2"), succ.Const("Char"), succ.Const("Int"), succ.Const("String"), succ.Const("Object"))), succ.Const("Boolean")))
  }  
    
  @Test
  def testTransformer12(){  
    val scalaType = Method(ScalaConst("T1"), List(List(ScalaConst("T2"))), Function(List(ScalaConst("Char"), ScalaConst("T1")), Function(List(ScalaConst("String"), ScalaConst("Object")),ScalaConst("Boolean"))))
    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(List(succ.Const("T1"), succ.Const("T2"), succ.Const("Char"), succ.Const("String"), succ.Const("Object"))), succ.Const("Boolean")))
  }
  
  @Test
  def testTransformer13(){  
    val scalaType = Method(ScalaConst("T1"), List(List(ScalaConst("T2"))), Function(List(ScalaConst("Char"), ScalaConst("Int")), Function(List(ScalaConst("String"), ScalaConst("Object")),ScalaConst("Boolean"))))

    assertEquals(TypeTransformer.transform(scalaType), Arrow(TSet(List(succ.Const("Int"), succ.Const("String"), succ.Const("T1"), succ.Const("Object"), succ.Const("T2"), succ.Const("Char"))), succ.Const("Boolean")))
  }
  
  @Test
  def testTransformer14(){
    val scalaType = ScalaInstance("Int", List(ScalaConst("Boolean"), ScalaConst("String")))   

    assertEquals(TypeTransformer.transform(scalaType), succ.Instance("Int", List(succ.Const("Boolean"), succ.Const("String"))))
  }
  
  @Test
  def testTransformer15(){  
    val scalaType = Method(ScalaConst("T1"), List(List(ScalaConst("T2"))), Function(List(Function(List(ScalaConst("Char"), ScalaInstance("Int", List(ScalaConst("Boolean"), ScalaConst("String")))), ScalaConst("Object")), ScalaConst("T1")), Function(List(ScalaConst("String"), ScalaConst("Object")),ScalaConst("Boolean"))))

    assertEquals(TypeTransformer.transform(scalaType),
      Arrow(TSet(List(succ.Const("T1"), succ.Const("T2"), Arrow(TSet(List(succ.Const("Char"), succ.Instance("Int", List(succ.Const("Boolean"), succ.Const("String"))))), succ.Const("Object")), succ.Const("String"), succ.Const("Object"))), succ.Const("Boolean")))
  }
  
  @Test
  def testTransformer16(){
    val scalaType1 = INTtoCHAR
    val scalaType2 = Method(null, List(List(INT)), CHAR)
    assertEquals(TypeTransformer.transform(scalaType2), TypeTransformer.transform(scalaType1))
  }  
  
}