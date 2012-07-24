package gjavaapi.PriorityQueueintinitialCapacity

//http://www.java2s.com/Code/JavaAPI/java.util/newPriorityQueueintinitialCapacity.htm

import java.util._

object ProductQuality {
  var High = 0
  var Medium = 1
  var Low = 2
}

class Product(str:String, pri:Int) extends Comparable[Product] {
  var name = str

  var priority = pri

  def compareTo(msg2:Product) = priority - msg2.priority
}

class MessageComparator extends Comparator[Product] {
  def compare(msg1:Product, msg2:Product) =  msg2.priority - msg1.priority
}

class Main {
  def main(args:Array[String]) {

    var pq:PriorityQueue[Product] = new PriorityQueue[Product](3) //r>5

    pq.add(new Product("A", ProductQuality.Low));
    pq.add(new Product("B", ProductQuality.High));
    pq.add(new Product("C", ProductQuality.Medium));
    var m:Product = pq.poll()
    while (m != null){
      System.out.println(m.name + " Priority: " + m.priority);
       m = pq.poll()
    }
    var pqRev:PriorityQueue[Product] = new PriorityQueue[Product](3, new MessageComparator());

    pqRev.add(new Product("D", ProductQuality.Low));
    pqRev.add(new Product("E", ProductQuality.High));
    pqRev.add(new Product("F", ProductQuality.Medium));
    
    m = pqRev.poll()
    while (m != null){
      System.out.println(m.name + " Priority: " + m.priority);
      m = pqRev.poll()
    }
  }
}


/*
import java.util.Comparator;
import java.util.PriorityQueue;

enum ProductQuality {
  High, Medium, Low
}

class Product implements Comparable<Product> {
  String name;

  ProductQuality priority;

  Product(String str, ProductQuality pri) {
    name = str;
    priority = pri;
  }

  public int compareTo(Product msg2) {
    return priority.compareTo(msg2.priority);
  }
}

class MessageComparator implements Comparator<Product> {
  public int compare(Product msg1, Product msg2) {
    return msg2.priority.compareTo(msg1.priority);
  }
}

public class Main {
  public static void main(String args[]) {

    PriorityQueue<Product> pq = new PriorityQueue<Product>(3);

    pq.add(new Product("A", ProductQuality.Low));
    pq.add(new Product("B", ProductQuality.High));
    pq.add(new Product("C", ProductQuality.Medium));
    Product m;
    while ((m = pq.poll()) != null)
      System.out.println(m.name + " Priority: " + m.priority);

    PriorityQueue<Product> pqRev = new PriorityQueue<Product>(3, new MessageComparator());

    pqRev.add(new Product("D", ProductQuality.Low));
    pqRev.add(new Product("E", ProductQuality.High));
    pqRev.add(new Product("F", ProductQuality.Medium));

    while ((m = pqRev.poll()) != null)
      System.out.println(m.name + " Priority: " + m.priority);
  }
}

*/
