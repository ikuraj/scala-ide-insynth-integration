package gjavaapi.ArrayListCollectionc

//http://www.java2s.com/Code/JavaAPI/java.util/newArrayListCollectionc.htm
 
import java.util._

class Main {

  def main(args:Array[String]) {
    var queue:Queue[String] = new LinkedList[String]();
    queue.add("Hello");
    queue.add("World");
    var list:List[String] = new ArrayList[String](queue); //r>5

    System.out.println(list);
  }

}

/*
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Main {

  public static void main(String[] args) {
    Queue<String> queue = new LinkedList<String>();
    queue.add("Hello");
    queue.add("World");
    List<String> list = new ArrayList<String>(queue);

    System.out.println(list);
  }

}
*/
