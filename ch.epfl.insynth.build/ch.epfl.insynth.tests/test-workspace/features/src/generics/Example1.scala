package examplepkg1

object A {
  def m1():Array[Int] = null

  def m2[T]():Array[T] = null
}

class Example1 {

  def main(){
    // this one should be generated, the type constructors are instantiated
    // val val1:Array[Int] = m1() - this should be generated
    val val1:Array[Int] =  /*!*/

    // this one is not valid, should not be generated, since generic types are involved
    val val2:Array[T] =  /*!*/
  }  
}