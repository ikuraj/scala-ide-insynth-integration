package javaapi.InputStreamReaderInputStreamin

//http://www.java2s.com/Code/JavaAPI/java.io/newInputStreamReaderInputStreamin.htm

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class MainClass {
  def main(args:Array[String]) {
    try {
      System.out.print("Enter your name: ");
//      var reader:InputStreamReader = new InputStreamReader(System.in) //r=1
      var reader:InputStreamReader =  /*!*/ //r=1
      var in:BufferedReader = new BufferedReader(reader)
      
      var name:String = in.readLine()
      System.out.println("Hello, " + name + ". Enter three ints...");
      var values = new Array[Int](3);
      var sum:Double = 0.;

      for (i <- 0 until values.length) {
        System.out.print("Number " + (i + 1) + ": ");
        var temp:String = in.readLine()
        values(i) = Integer.parseInt(temp);
        sum += values(i);
      }

      System.out.println("The average equals " + sum / values.length);

    } catch {
      case e => e.printStackTrace();
    }
  }
}



/*
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainClass {
  public static void main(String args[]) {
    try {
      System.out.print("Enter your name: ");
      InputStreamReader reader = new InputStreamReader(System.in);
      BufferedReader in = new BufferedReader(reader);

      String name = in.readLine();
      System.out.println("Hello, " + name + ". Enter three ints...");
      int[] values = new int[3];
      double sum = 0.0;

      for (int i = 0; i < values.length; i++) {
        System.out.print("Number " + (i + 1) + ": ");
        String temp = in.readLine();
        values[i] = Integer.parseInt(temp);
        sum += values[i];
      }

      System.out.println("The average equals " + sum / values.length);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
*/
