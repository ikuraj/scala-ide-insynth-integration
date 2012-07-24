package javaapi.HashMapkeySet

//http://www.java2s.com/Code/JavaAPI/java.util/HashMapkeySet.htm

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

class Main {
  def main(args:Array[String]) {
    var hMap = new HashMap[String, String]();

    hMap.put("1", "One");
    hMap.put("2", "Two");
    hMap.put("3", "Three");

    var st:Set[String] = hMap.keySet() //r=1
    var itr:Iterator[String] = st.iterator();

    while (itr.hasNext())
      System.out.println(itr.next());

    // remove 2 from Set
    st.remove("2");

    System.out.println(hMap.containsKey("2"));
  }
}

/*
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Main {
  public static void main(String[] args) {
    HashMap<String, String> hMap = new HashMap<String, String>();

    hMap.put("1", "One");
    hMap.put("2", "Two");
    hMap.put("3", "Three");

    Set st = hMap.keySet();
    Iterator itr = st.iterator();

    while (itr.hasNext())
      System.out.println(itr.next());

    // remove 2 from Set
    st.remove("2");

    System.out.println(hMap.containsKey("2"));
  }
}
*/
