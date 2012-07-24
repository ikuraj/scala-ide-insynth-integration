package javaapi.VectorlistIterator

//http://www.java2s.com/Code/JavaAPI/java.util/VectorlistIterator.htm

import java.util.ListIterator;
import java.util.Vector;

class Main {
  def main(args:Array[String]) {
    var v = new Vector[String]();

    v.add("1");
    v.add("2");
    v.add("3");
    v.add("4");
    v.add("5");

    var itr:ListIterator[String] = v.listIterator() //r>5
    while (itr.hasNext()){
      System.out.println(itr.next());
    }
    while (itr.hasPrevious()){
      System.out.println(itr.previous());
    }
  }
}

/*
import java.util.ListIterator;
import java.util.Vector;

public class Main {
  public static void main(String[] args) {
    Vector<String> v = new Vector<String>();

    v.add("1");
    v.add("2");
    v.add("3");
    v.add("4");
    v.add("5");

    ListIterator itr = v.listIterator();
    while (itr.hasNext()){
      System.out.println(itr.next());
    }
    while (itr.hasPrevious()){
      System.out.println(itr.previous());
    }
  }
}
*/
