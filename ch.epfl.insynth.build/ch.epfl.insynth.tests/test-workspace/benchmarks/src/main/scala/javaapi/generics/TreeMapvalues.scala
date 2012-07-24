package javaapi.TreeMapvalues

//http://www.java2s.com/Code/JavaAPI/java.util/TreeMapvalues.htm

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

class Main {
  def main(args:Array[String]) {
    var treeMap = new TreeMap[String,String]();
    treeMap.put("1", "One");
    treeMap.put("2", "Two");
    treeMap.put("3", "Three");

    var c:Collection[String] = treeMap.values() //r=1

    var itr = c.iterator();

    while (itr.hasNext()){
      System.out.println(itr.next());
    }
  }
}

/*
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

public class Main {
  public static void main(String[] args) {
    TreeMap<String, String> treeMap = new TreeMap<String,String>();
    treeMap.put("1", "One");
    treeMap.put("2", "Two");
    treeMap.put("3", "Three");

    Collection c = treeMap.values();
    Iterator itr = c.iterator();

    while (itr.hasNext()){
      System.out.println(itr.next());
    }
  }
}
*/
