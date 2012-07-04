package plugintemplate.examples

class ApplicationInfoTest {
  
  def m1(i:Int)(i2: Int) = 5
  
  // this throws and exception with m2("bla")
  //def m2: String => Int = _ => 5
  
  def m2: String => Int = (_:String) => 5
  
  def m3(a1:Char)(a2: Int)(a3: Float): (String, Boolean) => (Int, Double) => Char = 
    (_, _) => (_,_) => 'c'
  def m3(a1:Float)(a2: Int): (String, Boolean) => (Int, Double) => Char = 
    (_, _) => (_,_) => 'c'
  
  def main(args: Array[String]) {
    m1(5)(6)
        
    m2("bla")
    
    m3('5')(6)(3.0f)("bla", true)(5, 6d)
    m3('5')(6)(3.0f)("bla", true)
    m3('5')(6)(3.0f)
        
    m3(1.0f)(5)("bla", true)(5, 6d)
    m3(1.0f)(5)("bla", true)
    m3(1.0f)(5)
  }
}
