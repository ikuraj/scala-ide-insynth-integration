package javaapi.Vectorgetintindex

//http://www.java2s.com/Code/JavaAPI/java.util/Vectorgetintindex.htm

import java.util.Vector;

class Main {
  def main(args:Array[String]) {
    var v = new Vector[String]();
    v.add("Hello");
    v.add("Hello");
    var s:String = v.get(0) //r>5
    System.out.println(s);
  }
}


/*
import java.util.Vector;

public class Main {
  public static void main(String args[]) {
    Vector<String> v = new Vector<String>();
    v.add("Hello");
    v.add("Hello");
    String s = v.get(0);
    System.out.println(s);
  }
}

*/

