package javaapi.HashtablekeySet

//http://www.java2s.com/Code/JavaAPI/java.util/HashtablekeySet.htm

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

class MainClass {
  def main(args:Array[String]) {
    var balance = new Hashtable[String, Double]();

    var str:String = null;
    var bal:Double = 0.0;

    balance.put("A", 4.34);
    balance.put("B", 3.22);
    balance.put("C", 8.00);
    balance.put("D", 9.22);
    balance.put("E", -9.08);

    var set:Set[String] = balance.keySet() //r=1

    var itr = set.iterator();
    while (itr.hasNext()) {
      str = itr.next();
      System.out.println(str + ": " + balance.get(str));
    }

    System.out.println();

    bal = balance.get("A");
    balance.put("A", bal + 1000);
    System.out.println("A's new balance: " + balance.get("A"));
  }
}

/*
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class MainClass {
  public static void main(String args[]) {
    Hashtable<String, Double> balance = new Hashtable<String, Double>();

    String str;
    double bal;

    balance.put("A", 4.34);
    balance.put("B", 3.22);
    balance.put("C", 8.00);
    balance.put("D", 9.22);
    balance.put("E", -9.08);

    Set<String> set = balance.keySet();

    Iterator<String> itr = set.iterator();
    while (itr.hasNext()) {
      str = itr.next();
      System.out.println(str + ": " + balance.get(str));
    }

    System.out.println();

    bal = balance.get("A");
    balance.put("A", bal + 1000);
    System.out.println("A's new balance: " + balance.get("A"));
  }
}
*/
