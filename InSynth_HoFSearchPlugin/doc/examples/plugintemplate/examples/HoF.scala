package plugintemplate.examples

class HoFExample {
    
  def foo: Int=>Boolean = i => true
        
  def m1(a:Int):Boolean = false
  def m2(f:Int => Boolean):Char = '_' // bitno nam je i ovo?
    
  def m1p[T](x: T): Boolean = false  
    
  val a:Char = m2(m1) 
  
  def main(args: Array[String]) {
    foo(5)
    
    m2(m1)
    
    m2(foo)
    
    m2(m1p)
    
    for (ind <- List(1,2,3)) yield ind
        
    ( (x:Int) => 'c' )(5)
  }
}
