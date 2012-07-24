package javaapi.Hashtableelements

//http://www.java2s.com/Code/JavaAPI/java.util/Hashtableelements.htm

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

class Main {
  def main(args:Array[String]) {
    var ht = new Hashtable[String,String]();
    ht.put("1", "One");
    ht.put("2", "Two");
    ht.put("3", "Three");

    var c = ht.values()
    var itr = c.iterator(); 
    
    while (itr.hasNext()){
      System.out.println(itr.next());
    }
    c.remove("One");

    var e:Enumeration[String] = ht.elements() //r=1
    
    while (e.hasMoreElements()){
      System.out.println(e.nextElement());
    }
  }
}

/*
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

public class Main {
  public static void main(String[] args) {
    Hashtable<String, String> ht = new Hashtable<String,String>();
    ht.put("1", "One");
    ht.put("2", "Two");
    ht.put("3", "Three");

    Collection c = ht.values();
    Iterator itr = c.iterator();
    while (itr.hasNext()){
      System.out.println(itr.next());
    }
    c.remove("One");

    Enumeration e = ht.elements();

    while (e.hasMoreElements()){
      System.out.println(e.nextElement());
    }
  }
}
*/
