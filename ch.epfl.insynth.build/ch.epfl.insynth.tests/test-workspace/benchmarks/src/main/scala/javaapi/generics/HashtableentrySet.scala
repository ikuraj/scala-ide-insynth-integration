package javaapi.HashtableentrySet

//http://www.java2s.com/Code/JavaAPI/java.util/HashtableentrySet.htm

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class MainClass {
  def main(args:Array[String]){
    var hash = new Hashtable[String,String](89);
    hash.put("one", "two");
    hash.put("two", "three");
    hash.put("three", "four");
    hash.put("four", "five");
    System.out.println(hash);
    System.out.println(hash.size());
    var e = hash.keys();
    while (e.hasMoreElements()) {
      var key = e.nextElement().asInstanceOf[String]
      System.out.println(key + " : " + hash.get(key));
    }
    var set:Set[Map.Entry[String, String]] = hash.entrySet() //r=1
    var it = set.iterator();
    while (it.hasNext()) {
      var entry = it.next().asInstanceOf[Map.Entry[String, String]];
      System.out.println(entry.getKey() + " : " + entry.getValue());
    }
  }
}

/*
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MainClass {
  public static void main(String args[]) throws Exception {
    Hashtable hash = new Hashtable(89);
    hash.put("one", "two");
    hash.put("two", "three");
    hash.put("three", "four");
    hash.put("four", "five");
    System.out.println(hash);
    System.out.println(hash.size());
    Enumeration e = hash.keys();
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      System.out.println(key + " : " + hash.get(key));
    }
    Set set = hash.entrySet();
    Iterator it = set.iterator();
    while (it.hasNext()) {
      Map.Entry entry = (Map.Entry) it.next();
      System.out.println(entry.getKey() + " : " + entry.getValue());
    }
  }
}
*/
