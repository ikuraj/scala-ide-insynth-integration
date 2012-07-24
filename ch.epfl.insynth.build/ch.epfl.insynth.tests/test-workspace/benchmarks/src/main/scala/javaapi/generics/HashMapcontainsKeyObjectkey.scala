package javaapi.HashMapcontainsKeyObjectkey

//http://www.java2s.com/Code/JavaAPI/java.util/HashMapcontainsKeyObjectkey.htm

import java.util.HashMap;

object Main {
  def main(args:Array[String]){
    var hMap = new HashMap[String, String]();

    hMap.put("1", "One");
    hMap.put("2", "Two");
    hMap.put("3", "Three");

    var blnExists:Boolean = hMap.containsKey("3") //r>5
    System.out.println("3 exists in HashMap ? : " + blnExists);
  }
}

/*
import java.util.HashMap;

public class Main {
  public static void main(String[] args) {
    HashMap<String, String> hMap = new HashMap<String, String>();

    hMap.put("1", "One");
    hMap.put("2", "Two");
    hMap.put("3", "Three");

    boolean blnExists = hMap.containsKey("3");
    System.out.println("3 exists in HashMap ? : " + blnExists);
  }
}

*/















