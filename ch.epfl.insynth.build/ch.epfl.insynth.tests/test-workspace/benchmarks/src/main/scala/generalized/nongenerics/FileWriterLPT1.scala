package gjava.FileWriterLPT1

//http://www.java2s.com/Code/JavaAPI/java.io/newFileWriterLPT1.htm

import java.io._

class MainClass {
  def main(args:Array[String]) {
    try {
      var fw:FileWriter = new FileWriter("?") //r=1

      var pw:PrintWriter = new PrintWriter(fw)

      var s = "www.java2s.com";

      var i = 0
      var len = s.length();

      for (i <- 0 until (len / 80)) {
	val subBeg = i*80
        pw.print(s.substring(subBeg, subBeg + 80));
        pw.print("\r\n");
      }

      if (len > 0) {
        pw.print(s.substring(i));
        pw.print("\r\n");
      }

      pw.close();
    } catch {
      case e => System.out.println(e);
    }
  }
}


/*
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MainClass {
  public static void main(String[] args) {
    try {
      FileWriter fw = new FileWriter("LPT1:");

      PrintWriter pw = new PrintWriter(fw);
      String s = "www.java2s.com";

      int i, len = s.length();

      for (i = 0; len > 80; i += 80) {
        pw.print(s.substring(i, i + 80));
        pw.print("\r\n");
        len -= 80;
      }

      if (len > 0) {
        pw.print(s.substring(i));
        pw.print("\r\n");
      }

      pw.close();
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}
*/
