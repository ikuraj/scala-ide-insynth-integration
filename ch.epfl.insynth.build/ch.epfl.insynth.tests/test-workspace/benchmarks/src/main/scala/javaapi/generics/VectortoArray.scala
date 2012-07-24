package javaapi.VectortoArray

//http://www.java2s.com/Code/JavaAPI/java.util/VectortoArray.htm

import java.util.Vector;

class Main {
  def main(args:Array[String]) {
    var v1 = new Vector[String]();
    v1.add("A");
    v1.add("B");
    v1.add("C");
    var array:Array[Object] = v1.toArray()//r=2

    for (i <-0 until array.length) {
      System.out.println(array(i));
    }
  }
} 

/*
import java.util.Vector;

public class Main {
  public static void main(String args[]) {
    Vector<String> v1 = new Vector<String>();
    v1.add("A");
    v1.add("B");
    v1.add("C");
    Object[] array = v1.toArray();

    for (int i = 0; i < array.length; i++) {
      System.out.println(array[i]);
    }
  }
} 
*/
