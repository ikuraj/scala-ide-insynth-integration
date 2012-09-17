package cleancodegen

class A {
  def getLong: Long = 5l
}

class B {
  def getInstanceOfA() = new A
}

class Main {

  def main(){
  	// should return: new B().getInstanceOfA getLong
    val val1: Long =  /*!*/
  }  
}