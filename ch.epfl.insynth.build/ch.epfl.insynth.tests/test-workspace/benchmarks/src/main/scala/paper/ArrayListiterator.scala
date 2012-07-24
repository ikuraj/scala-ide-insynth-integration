package paper.ArrayListiterator

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

class MainClass {
  def main(args:Array[String]) {
    var al:ArrayList[String] = new ArrayList[String]()

    al.add("A");
    al.add("B");

    var itr:Iterator[String] = al.iterator(); //r=1
    while (itr.hasNext()) {
      var element = itr.next();
      System.out.print(element + " ");
    }
  }
}

//Inspired by http://www.java2s.com/Code/JavaAPI/java.util/ArrayListiterator.htm 

/*
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class MainClass {
  public static void main(String args[]) {
    ArrayList<String> al = new ArrayList<String>();

    al.add("C");
    al.add("A");
    al.add("E");
    al.add("B");
    al.add("D");
    al.add("F");

    System.out.print("Original contents of al: ");
    Iterator<String> itr = al.iterator();
    while (itr.hasNext()) {
      String element = itr.next();
      System.out.print(element + " ");
    }
    System.out.println();

    ListIterator<String> litr = al.listIterator();
    while (litr.hasNext()) {
      String element = litr.next();
      litr.set(element + "+");
    }

    // Now, display the list backwards.
    System.out.print("Modified list backwards: ");
    while (litr.hasPrevious()) {
      String element = litr.previous();
      System.out.print(element + " ");
    }
  }
}
*/
