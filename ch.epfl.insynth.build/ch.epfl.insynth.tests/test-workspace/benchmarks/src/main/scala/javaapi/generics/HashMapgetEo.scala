package javaapi.HashMapgetEo

//http://www.java2s.com/Code/JavaAPI/java.util/HashMapgetEo.htm

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class MainClass {
  def main(args:Array[String]) {

    var hm = new HashMap[String, Double]();

    hm.put("A", 3434.34);
    hm.put("B", 123.22);
    hm.put("C", 1378.00);
    hm.put("D", 99.22);
    hm.put("E", -19.08);

    var set:Set[Map.Entry[String, Double]] = hm.entrySet()

    for(i <- 0 until set.size){
      val me = set.toArray()(i).asInstanceOf[Map.Entry[String, Double]]
      System.out.print(me.getKey() + ": ");
      System.out.println(me.getValue());
    }

    System.out.println();

    var balance:Double = hm.get("B") //r>5 
    hm.put("B", balance + 1000);

    System.out.println("B's new balance: " + hm.get("B"));
  }
}

/*
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainClass {
  public static void main(String args[]) {

    HashMap<String, Double> hm = new HashMap<String, Double>();

    hm.put("A", new Double(3434.34));
    hm.put("B", new Double(123.22));
    hm.put("C", new Double(1378.00));
    hm.put("D", new Double(99.22));
    hm.put("E", new Double(-19.08));

    Set<Map.Entry<String, Double>> set = hm.entrySet();

    for (Map.Entry<String, Double> me : set) {
      System.out.print(me.getKey() + ": ");
      System.out.println(me.getValue());
    }

    System.out.println();

    double balance = hm.get("B");
    hm.put("B", balance + 1000);

    System.out.println("B's new balance: " + hm.get("B"));
  }
}
*/








