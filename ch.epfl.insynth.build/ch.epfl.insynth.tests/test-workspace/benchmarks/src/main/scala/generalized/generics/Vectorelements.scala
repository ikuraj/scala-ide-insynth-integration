package gjavaapi.Vectorelements

//http://www.java2s.com/Code/JavaAPI/java.util/Vectorelements.htm

import java.util._

class MainClass {

  def main(args:Array[String]) {

    var vector = new Vector[Any]();
    vector.addElement(5);
    vector.addElement(-14.14f);
    vector.addElement("Hello");

    var e:Enumeration[Any] = vector.elements() //r>5
    while(e.hasMoreElements()) {
      var obj = e.nextElement();
      System.out.println(obj);
    }
  }
}

/*
import java.util.Enumeration;
import java.util.Vector;

public class MainClass {

  public static void main(String args[]) {

    Vector vector = new Vector();
    vector.addElement(new Integer(5));
    vector.addElement(new Float(-14.14f));
    vector.addElement(new String("Hello"));

    Enumeration e = vector.elements();
    while(e.hasMoreElements()) {
      Object obj = e.nextElement();
      System.out.println(obj);
    }
  }
}
*/
