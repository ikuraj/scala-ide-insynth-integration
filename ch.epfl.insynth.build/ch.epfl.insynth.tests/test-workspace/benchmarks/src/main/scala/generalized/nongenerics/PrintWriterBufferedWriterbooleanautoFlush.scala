package gjavaapi.printwriterbufferedwriterbooleanautoflush

//http://www.java2s.com/Code/JavaAPI/java.io/newPrintWriterBufferedWriterbooleanautoFlush.htm

import java.io._

class MainClass {

  def main(args:Array[String]) {
    try {
      // Create a print writer
      var fw:FileWriter  = new FileWriter(args(0))
      var bw:BufferedWriter = new BufferedWriter(fw)
      var pw:PrintWriter = new PrintWriter(bw, false) //r>5

      // Experiment with some methods
      pw.println(true);
      pw.println('A');
      pw.println(500);
      pw.println(40000L);
      pw.println(45.67f);
      pw.println(45.67);
      pw.println("Hello");
      pw.println("99".toInt);

      // Close print writer
      pw.close();
    } catch{
      case e => System.out.println("Exception: " + e);
    }
  }
}



/*
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public class MainClass {

  public static void main(String args[]) {

    try {

      // Create a print writer
      FileWriter fw = new FileWriter(args[0]);
      BufferedWriter bw = new BufferedWriter(fw);
      PrintWriter pw = new PrintWriter(bw, false);

      // Experiment with some methods
      pw.println(true);
      pw.println('A');
      pw.println(500);
      pw.println(40000L);
      pw.println(45.67f);
      pw.println(45.67);
      pw.println("Hello");
      pw.println(new Integer("99"));

      // Close print writer
      pw.close();
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
  }
}
*/
