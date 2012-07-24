package javaapi.TreeSetE

//http://www.java2s.com/Code/JavaAPI/java.util/newTreeSetE.htm

import java.util.TreeSet;

class MainClass {
  def main(args:Array[String]) {
    var ts:TreeSet[String] = new TreeSet[String]() //r=1

    ts.add("C");
    ts.add("A");
    ts.add("B");
    ts.add("E");
    ts.add("F");
    ts.add("D");

    System.out.println(ts);
  }
}

/*
import java.util.TreeSet;

public class MainClass {
  public static void main(String args[]) {
    TreeSet<String> ts = new TreeSet<String>();

    ts.add("C");
    ts.add("A");
    ts.add("B");
    ts.add("E");
    ts.add("F");
    ts.add("D");

    System.out.println(ts);
  }
}
*/
