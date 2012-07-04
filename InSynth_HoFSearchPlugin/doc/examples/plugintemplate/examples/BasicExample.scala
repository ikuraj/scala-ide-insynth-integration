package plugintemplate.examples

/** An example demonstrating the fancy features of the new
 *  compiler plugin.
 */
class BasicExample {
  val bla = m1(6)
  
  def foo = ()
  
  def m1(i:Int) = ()
  
  def m2(i:Int) = m1(i)
  
  def m3(i:Int)(i2: Int) = 5
  
  def main(args: Array[String]) {
    m1(5)
    m3(4)(6)
    m1(5)
  }
}
