package subexamplepkg4

//Some duplicates occur.
class Data private()

trait A {
  //def m():Data = null
  def m1(a:A):Data = null
}

class B extends A

class C extends B with A
//TODO: What has happened with this example?
class Example4 {

  def main(){
    //val a:Data =  
  }
  
}