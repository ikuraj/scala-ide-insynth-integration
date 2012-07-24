package gjavaapi.HashtableKV

//http://www.java2s.com/Code/JavaAPI/java.util/newHashtableKV.htm

import java.util._

class MainClass {
  def main(args:Array[String]) {
    var balance:Hashtable[String, Double] = new Hashtable[String,Double]() //r=1

    var  names:Enumeration[String] = null;
    var  str:String = null;

    balance.put("A", 3434.34);

    names = balance.keys();
    while (names.hasMoreElements()) {
      str = names.nextElement();
      System.out.println(str + ": " + balance.get(str));
    }

    System.out.println();
  }
}

/*
import java.util.Enumeration;
import java.util.Hashtable;

public class MainClass {
  public static void main(String args[]) {
    Hashtable<String, Double> balance = new Hashtable<String, Double>();

    Enumeration<String> names;
    String str;

    balance.put("A", 3434.34);

    names = balance.keys();
    while (names.hasMoreElements()) {
      str = names.nextElement();
      System.out.println(str + ": " + balance.get(str));
    }

    System.out.println();
  }
}
*/
