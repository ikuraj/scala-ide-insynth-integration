package long.javaapi.bufferedinputstreamfileinputstream

//http://www.java2s.com/Code/JavaAPI/java.io/newBufferedInputStreamFileInputStreamfileInputStream.htm

import java.io.BufferedInputStream;
import java.io.FileInputStream;

class FileManager {

  def main(args:Array[String]) {
    try { 
      var bis:BufferedInputStream = new BufferedInputStream(new FileInputStream(args(0))) //r=4

      var i:Int = bis.read();
      while (i != -1) {
        System.out.println(i);
      }
    } catch {
      case e => System.out.println("Exception: " + e);
    }
  }
}


/*
import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class MainClass {

  public static void main(String args[]) {
    try {
      FileInputStream fis = new FileInputStream(args[0]);

      BufferedInputStream bis = new BufferedInputStream(fis);

      int i;
      while ((i = bis.read()) != -1) {
        System.out.println(i);
      }
      fis.close();
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
  }
}
*/









































//http://www.java2s.com/Code/JavaAPI/java.io/newBufferedInputStreamFileInputStreamfileInputStream.htm

/*
import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class MainClass {

  public static void main(String args[]) {
    try {
      FileInputStream fis = new FileInputStream(args[0]);

      BufferedInputStream bis = new BufferedInputStream(fis);

      int i;
      while ((i = bis.read()) != -1) {
        System.out.println(i);
      }
      fis.close();
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
  }
}
*/
