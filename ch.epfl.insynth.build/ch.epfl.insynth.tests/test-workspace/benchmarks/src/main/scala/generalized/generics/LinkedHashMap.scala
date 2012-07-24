package gjavaapi.LinkedHashMap

//http://www.java2s.com/Code/JavaAPI/java.util/newLinkedHashMap.htm

import java.util._

class Main {

  def main(args:Array[String]) {
    var linkedMap:LinkedHashMap[Int, Int] = new LinkedHashMap[Int,Int]() //r=1
    for (i <-0 until 10) {
      linkedMap.put(i, i);
    }

    System.out.println(linkedMap);
    // Least-recently used order:
    linkedMap = new LinkedHashMap[Int, Int](16, 0.75f, true);

    for (i <-0 until 10) {
      linkedMap.put(i, i);
    }
    System.out.println(linkedMap);
    for (i <- 0 until 7)
      System.out.println(linkedMap.get(i));
    System.out.println(linkedMap);
  }
}



/*
import java.util.LinkedHashMap;

public class Main {

  public static void main(String[] args) {
    LinkedHashMap<Integer, Integer> linkedMap = new LinkedHashMap<Integer, Integer>();
    for (int i = 0; i < 10; i++) {
      linkedMap.put(i, i);
    }

    System.out.println(linkedMap);
    // Least-recently used order:
    linkedMap = new LinkedHashMap<Integer, Integer>(16, 0.75f, true);

    for (int i = 0; i < 10; i++) {
      linkedMap.put(i, i);
    }
    System.out.println(linkedMap);
    for (int i = 0; i < 7; i++)
      System.out.println(linkedMap.get(i));
    System.out.println(linkedMap);
  }
}
*/
