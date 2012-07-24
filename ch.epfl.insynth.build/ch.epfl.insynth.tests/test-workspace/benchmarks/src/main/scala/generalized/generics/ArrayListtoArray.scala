package gjavaapi.ArrayListtoArray

//http://www.java2s.com/Code/JavaAPI/java.util/ArrayListtoArray.htm

import java.util._

class Main {
  def main(args:Array[String]) {
    var arrayList = new ArrayList[String]();

    arrayList.add("1");
    arrayList.add("2");
    arrayList.add("3");
    arrayList.add("4");
    arrayList.add("5");

    var objArray:Array[Object] = arrayList.toArray() //r>5

    for (i <-0 until objArray.size)
      System.out.println(objArray(i));
  }
}


/*
import java.util.ArrayList;

public class Main {
  public static void main(String[] args) {
    ArrayList<String> arrayList = new ArrayList<String>();

    arrayList.add("1");
    arrayList.add("2");
    arrayList.add("3");
    arrayList.add("4");
    arrayList.add("5");

    Object[] objArray = arrayList.toArray();

    for (Object obj : objArray)
      System.out.println(obj);
  }
}
*/
