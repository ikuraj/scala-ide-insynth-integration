package gjavaapi.InputStreamReaderInputStreaminStringcharsetName

//http://www.java2s.com/Code/JavaAPI/java.io/newInputStreamReaderInputStreaminStringcharsetName.htm

import java.io._

object MainClass {

  def Converter(input:String, output:String) {
    try {
      var fis:FileInputStream  = new FileInputStream(new File(input))

      var in:BufferedReader = new BufferedReader(new InputStreamReader(fis, "SJIS")) //r>5

      var fos:FileOutputStream = new FileOutputStream(new File(output))

      var out:BufferedWriter = new BufferedWriter(new OutputStreamWriter(fos, "UTF8"))

      var len = 80;
      var buf: Array[Char] = new Array[Char](len);

      var numRead = in.read(buf, 0, len)
      while (numRead != -1){
        out.write(buf, 0, numRead);
	in.read(buf, 0, len)
      }

      out.close();
      in.close();
    } catch {
      case e => System.out.println("An I/O Exception Occurred: " + e);
    }
  }

  def main(args:Array[String]) {
    MainClass.Converter(args(0), args(1));
  }

}




/*
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainClass {

  public static void Converter(String input, String output) {
    try {
      FileInputStream fis = new FileInputStream(new File(input));
      BufferedReader in = new BufferedReader(new InputStreamReader(fis, "SJIS"));

      FileOutputStream fos = new FileOutputStream(new File(output));
      BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos, "UTF8"));

      int len = 80;
      char buf[] = new char[len];

      int numRead;
      while ((numRead = in.read(buf, 0, len)) != -1)
        out.write(buf, 0, numRead);

      out.close();
      in.close();
    } catch (IOException e) {
      System.out.println("An I/O Exception Occurred: " + e);
    }
  }

  public static void main(String args[]) {
    MainClass.Converter(args[0], args[1]);
  }

}
*/
