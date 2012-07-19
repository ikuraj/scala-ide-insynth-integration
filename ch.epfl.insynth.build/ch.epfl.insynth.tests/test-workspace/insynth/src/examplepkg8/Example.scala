package examplepkg8

// seems to be working fine (note the infix and brackets)
object A {

  var f2:Long = 0
  
  def m1(f:Float=>Long):Int = 0  
  
}

class Example7 {

  def main(){
    val a:Int = A m1 { var_1 => A f2  }
  }
}