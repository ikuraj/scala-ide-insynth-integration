package javaapi.ArrayListE

//http://www.java2s.com/Code/JavaAPI/java.util/newArrayListE.htm

import java.util.ArrayList;

 class MainClass {
  def main(args:Array[String]) {
    var al:ArrayList[String] = new ArrayList[String]() //r=1

    System.out.println("Initial size of al: " + al.size());

    al.add("C");
    al.add("A");
    al.add("E");
    al.add("B");
    al.add("D");
    al.add("F");
    al.add(1, "A2");

    System.out.println("Size of al after additions: " + al.size());

    System.out.println("Contents of al: " + al);

    al.remove("F");
    al.remove(2);

    System.out.println("Size of al after deletions: " + al.size());
    System.out.println("Contents of al: " + al);
  }
}           

/*
import java.util.ArrayList;

public class MainClass {
  public static void main(String args[]) {
    ArrayList<String> al = new ArrayList<String>();

    System.out.println("Initial size of al: " + al.size());

    al.add("C");
    al.add("A");
    al.add("E");
    al.add("B");
    al.add("D");
    al.add("F");
    al.add(1, "A2");

    System.out.println("Size of al after additions: " + al.size());

    System.out.println("Contents of al: " + al);

    al.remove("F");
    al.remove(2);

    System.out.println("Size of al after deletions: " + al.size());
    System.out.println("Contents of al: " + al);
  }
}           
*/
