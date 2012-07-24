package gjavaapi.TreeMaplastKey

//http://www.java2s.com/Code/JavaAPI/java.util/TreeMaplastKey.htm

import java.util._

class MainClass {
  def main(args:Array[String]) {
    var map = new TreeMap[String, String]();
    map.put("Virginia", "Richmond");
    map.put("Massachusetts", "Boston");
    map.put("New York", "Albany");
    map.put("Maryland", "Annapolis");

    if (!map.isEmpty()) {
      var last:Object = map.lastKey() //r>5
      var first = true;
      var cond = false
      while(!cond) {
        if (!first) {
          System.out.print(", ");
        }
        System.out.print(last);
        last = map.headMap(last.asInstanceOf[String]).lastKey();
        first = false;
	cond = last != map.firstKey()
      }
      System.out.println();
    }
  }
}


/*
import java.util.TreeMap;

public class MainClass {
  public static void main(String args[]) {
    TreeMap map = new TreeMap();
    map.put("Virginia", "Richmond");
    map.put("Massachusetts", "Boston");
    map.put("New York", "Albany");
    map.put("Maryland", "Annapolis");

    if (!map.isEmpty()) {
      Object last = map.lastKey();
      boolean first = true;
      do {
        if (!first) {
          System.out.print(", ");
        }
        System.out.print(last);
        last = map.headMap(last).lastKey();
        first = false;
      } while (last != map.firstKey());
      System.out.println();
    }
  }
}
*/
