package javaapi.MapkeySet

//http://www.java2s.com/Code/JavaAPI/java.util/MapkeySet.htm

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class MainClass {

  def main(argv:Array[String]) {
    var map = new HashMap[String, String]();

    map.put("Adobe", "Mountain View, CA");
    map.put("IBM", "White Plains, NY");
    map.put("Learning Tree", "Los Angeles, CA");

    var k:Iterator[String] = map.keySet().iterator() //r>5
    while (k.hasNext()) {
      var key = k.next().asInstanceOf[String];
      System.out.println("Key " + key + "; Value " + map.get(key));
    }
  }
}

/*
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainClass {

  public static void main(String[] argv) {
    Map map = new HashMap();

    map.put("Adobe", "Mountain View, CA");
    map.put("IBM", "White Plains, NY");
    map.put("Learning Tree", "Los Angeles, CA");

    Iterator k = map.keySet().iterator();
    while (k.hasNext()) {
      String key = (String) k.next();
      System.out.println("Key " + key + "; Value " + (String) map.get(key));
    }
  }
}
*/
