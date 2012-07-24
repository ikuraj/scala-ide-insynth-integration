package javaapi.LinkedList

//http://www.java2s.com/Code/JavaAPI/java.util/LinkedListiterator.htm

import java.util.Iterator;
import java.util.LinkedList;

class Main {
  def main(args:Array[String]) {
    var lList = new LinkedList[String](); 
    lList.add("1");
    lList.add("2");
    lList.add("3");
    lList.add("4");
    lList.add("5");

    var itr:Iterator[String] = lList.iterator(); //r>5
    while (itr.hasNext()) {
      System.out.println(itr.next());
    }
  }
}

/*
import java.util.Iterator;
import java.util.LinkedList;

public class Main {
  public static void main(String[] args) {
    LinkedList<String> lList = new LinkedList<String>();
    lList.add("1");
    lList.add("2");
    lList.add("3");
    lList.add("4");
    lList.add("5");

    Iterator itr = lList.iterator();
    while (itr.hasNext()) {
      System.out.println(itr.next());
    }
  }
}
*/
