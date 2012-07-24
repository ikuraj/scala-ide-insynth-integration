package gjavaapi.Hashtablekeys

//http://www.java2s.com/Code/JavaAPI/java.util/Hashtablekeys.htm

import java.util._

class MainClass {

  def main(args:Array[String]) {
    var hashtable = new Hashtable[String,String]();
    hashtable.put("apple", "red");
    hashtable.put("strawberry", "red");

    var e:Enumeration[String] = hashtable.keys() //r>5
    while(e.hasMoreElements()) {
      var k = e.nextElement();
      var v = hashtable.get(k);
      System.out.println("key = " + k + "; value = " + v);
    } 

  }
}
 
/*
import java.util.Enumeration;
import java.util.Hashtable;

public class MainClass {

  public static void main(String args[]) {
    Hashtable hashtable = new Hashtable();
    hashtable.put("apple", "red");
    hashtable.put("strawberry", "red");

    Enumeration e = hashtable.keys();
    while(e.hasMoreElements()) {
      Object k = e.nextElement();
      Object v = hashtable.get(k);
      System.out.println("key = " + k + "; value = " + v);
    } 

  }
} 
*/
