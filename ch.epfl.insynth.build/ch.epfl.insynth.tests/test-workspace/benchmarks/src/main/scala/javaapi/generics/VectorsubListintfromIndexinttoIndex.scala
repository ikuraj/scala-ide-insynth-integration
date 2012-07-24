package javaapi.VectorsubListintfromIndexinttoIndex

//http://www.java2s.com/Code/JavaAPI/java.util/VectorsubListintfromIndexinttoIndex.htm

import java.util.List;
import java.util.Vector;

class Main {
  def main(args:Array[String]) {
    var v1 = new Vector[String]();
    v1.add("A");
    v1.add("B");
    v1.add("C");
    var l:List[String] = v1.subList(1, 2); //r>5

    for (i <- 0 until l.size()) {
      System.out.println(l.get(i));
    }
  }
}

/*
import java.util.List;
import java.util.Vector;

public class Main {
  public static void main(String args[]) {
    Vector<String> v1 = new Vector<String>();
    v1.add("A");
    v1.add("B");
    v1.add("C");
    List l = v1.subList(1, 2);

    for (int i = 0; i < l.size(); i++) {
      System.out.println(l.get(i));
    }
  }
}
*/
