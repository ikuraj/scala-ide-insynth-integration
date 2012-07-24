package javaapi.HashSetiterator

//http://www.java2s.com/Code/JavaAPI/java.util/HashSetiterator.htm

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class Main {

  def main(a:Array[String]) {    
    var set:HashSet[String] = new HashSet[String](Arrays.asList("A", "B", "C", "D", "E"));

    var iter:Iterator[String] = set.iterator() //r=1
    while (iter.hasNext()) {
      System.out.println(iter.next());
    }

  }

}

/*
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Main {

  public static void main(String[] a) {
    String elements[] = { "A", "B", "C", "D", "E" };
    Set<String> set = new HashSet<String>(Arrays.asList(elements));

    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      System.out.println(iter.next());
    }

  }

} 
*/
