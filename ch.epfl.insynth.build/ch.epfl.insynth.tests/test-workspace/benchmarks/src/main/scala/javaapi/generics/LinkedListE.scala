package javaapi.LinkedListE

//http://www.java2s.com/Code/JavaAPI/java.util/newLinkedListE.htm

import java.util.LinkedList;

class MainClass {
  def main(args:Array[String]) {

    var ll:LinkedList[String] = new LinkedList[String]() //r=1

    ll.add("B");
    ll.add("C");
    ll.add("D");
    ll.add("E");
    ll.add("F");
    ll.addLast("Z");
    ll.addFirst("A");

    ll.add(1, "A2");

    System.out.println("Original contents of ll: " + ll);

    ll.remove("F");
    ll.remove(2);

    System.out.println("Contents of ll after deletion: " + ll);

    ll.removeFirst();
    ll.removeLast();

    System.out.println("ll after deleting first and last: " + ll);

    var value = ll.get(2);
    ll.set(2, value + " Changed");

    System.out.println("ll after change: " + ll);
  }
}

/*
import java.util.LinkedList;

public class MainClass {
  public static void main(String args[]) {

    LinkedList<String> ll = new LinkedList<String>();

    ll.add("B");
    ll.add("C");
    ll.add("D");
    ll.add("E");
    ll.add("F");
    ll.addLast("Z");
    ll.addFirst("A");

    ll.add(1, "A2");

    System.out.println("Original contents of ll: " + ll);

    ll.remove("F");
    ll.remove(2);

    System.out.println("Contents of ll after deletion: " + ll);

    ll.removeFirst();
    ll.removeLast();

    System.out.println("ll after deleting first and last: " + ll);

    String val = ll.get(2);
    ll.set(2, val + " Changed");

    System.out.println("ll after change: " + ll);
  }
}
*/
