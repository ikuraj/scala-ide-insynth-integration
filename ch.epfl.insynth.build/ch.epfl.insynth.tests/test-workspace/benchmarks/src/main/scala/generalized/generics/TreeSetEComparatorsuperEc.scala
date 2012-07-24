package gjavaapi.TreeSetEComparatorsuperEc

//http://www.java2s.com/Code/JavaAPI/java.util/newTreeSetEComparatorsuperEc.htm

import java.util._

class MyComparator extends Comparator[String] {
  def compare(a:String, b:String) = {
    var aStr = a;
    var bStr = b;

    bStr.compareTo(aStr)
  }
  // No need to override equals.
}

class MainClass {
  def main(args:Array[String]) {
    var ts:TreeSet[String] = new TreeSet[String](new MyComparator()) //r>5

    ts.add("C");
    ts.add("A");
    ts.add("B");
    ts.add("E");
    ts.add("F");
    ts.add("D");

    for (i <- 0 until ts.size()){
      var element = ts.toArray()(i)
      System.out.print(element + " ");
    }
    System.out.println();
  }
}

/*
import java.util.Comparator;
import java.util.TreeSet;

class MyComparator implements Comparator<String> {
  public int compare(String a, String b) {
    String aStr, bStr;

    aStr = a;
    bStr = b;

    return bStr.compareTo(aStr);
  }
  // No need to override equals.
}

public class MainClass {
  public static void main(String args[]) {
    TreeSet<String> ts = new TreeSet<String>(new MyComparator());

    ts.add("C");
    ts.add("A");
    ts.add("B");
    ts.add("E");
    ts.add("F");
    ts.add("D");

    for (String element : ts)
      System.out.print(element + " ");

    System.out.println();
  }
}
*/
