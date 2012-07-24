package javaapi.HashSetE

//http://www.java2s.com/Code/JavaAPI/java.util/newHashSetE.htm

import java.util.HashSet;

class MainClass {
  def main(args:Array[String]) {
    var hs:HashSet[String] = new HashSet[String]() //r=1

    hs.add("B");
    hs.add("A");
    hs.add("D");
    hs.add("E");
    hs.add("C");
    hs.add("F");

    System.out.println(hs);
  }
}

/*
import java.util.HashSet;

public class MainClass {
  public static void main(String args[]) {
    HashSet<String> hs = new HashSet<String>();

    hs.add("B");
    hs.add("A");
    hs.add("D");
    hs.add("E");
    hs.add("C");
    hs.add("F");

    System.out.println(hs);
  }
}
*/
