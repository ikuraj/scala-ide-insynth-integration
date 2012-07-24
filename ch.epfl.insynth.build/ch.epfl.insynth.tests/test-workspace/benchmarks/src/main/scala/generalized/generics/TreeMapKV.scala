package gjavaapi.TreeMapKV

//http://www.java2s.com/Code/JavaAPI/java.util/newTreeMapKV.htm

import java.util._

class MainClass {
  def main(args:Array[String]) {

    var tm:TreeMap[String, Double] = new TreeMap[String,Double]() //r=1

    tm.put("A", 3434.34);
    tm.put("B", 123.22);
    tm.put("C", 1378.00);
    tm.put("D", 99.22);
    tm.put("E", -19.08);

    var set:Set[Map.Entry[String, Double]] = tm.entrySet();

    for (i <- 0 until set.size) {
      var me = set.toArray()(i).asInstanceOf[Map.Entry[String, Double]]
      System.out.print(me.getKey() + ": ");
      System.out.println(me.getValue());
    }
    System.out.println();

    var balance = tm.get("B");
    tm.put("B", balance + 1000);

    System.out.println("B's new balance: " + tm.get("B"));
  }
}

/*
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MainClass {
  public static void main(String args[]) {

    TreeMap<String, Double> tm = new TreeMap<String, Double>();

    tm.put("A", new Double(3434.34));
    tm.put("B", new Double(123.22));
    tm.put("C", new Double(1378.00));
    tm.put("D", new Double(99.22));
    tm.put("E", new Double(-19.08));

    Set<Map.Entry<String, Double>> set = tm.entrySet();

    for (Map.Entry<String, Double> me : set) {
      System.out.print(me.getKey() + ": ");
      System.out.println(me.getValue());
    }
    System.out.println();

    double balance = tm.get("B");
    tm.put("B", balance + 1000);

    System.out.println("B's new balance: " + tm.get("B"));
  }
}
*/
