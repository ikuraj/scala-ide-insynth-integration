package gjavaapi.bufferedoutputstream

//http://www.java2s.com/Code/JavaAPI/java.io/newBufferedOutputStreamOutputStreamout.htm

import java.io._

class Main {
  def main(args:Array[String]) {
    var primes:Array[Int] = Array[Int](100)
    var numPrimes:Int = 0;

    var candidate:Int = 2;
    while (numPrimes < 400) {
      primes(numPrimes) = candidate;
      numPrimes+=1;
      candidate+=1;
    }

    try {
      var file:FileOutputStream = new FileOutputStream("p.dat")
      var buff:BufferedOutputStream  = new BufferedOutputStream(file) //r=1

      var data:DataOutputStream  = new DataOutputStream(buff)

      for (i <- 0 until 400)
        data.writeInt(primes(i));
      data.close();
    } catch {
      case e => System.out.println("Error - " + e.toString());
    }
  }
}

/*
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
  public static void main(String[] arguments) {
    int[] primes = new int[400];
    int numPrimes = 0;

    int candidate = 2;
    while (numPrimes < 400) {
      primes[numPrimes] = candidate;
      numPrimes++;
      candidate++;
    }

    try {
      FileOutputStream file = new FileOutputStream("p.dat");
      BufferedOutputStream buff = new BufferedOutputStream(file);
      DataOutputStream data = new DataOutputStream(buff);

      for (int i = 0; i < 400; i++)
        data.writeInt(primes[i]);
      data.close();
    } catch (IOException e) {
      System.out.println("Error - " + e.toString());
    }
  }
}
*/
