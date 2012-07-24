package javaapi.bufferedinputstreamfileinputstream

//http://www.java2s.com/Code/JavaAPI/java.io/newBufferedInputStreamFileInputStreamfileInputStream.htm

import java.io.BufferedInputStream;
import java.io.FileInputStream;

class FileManager {

  def main(args:Array[String]) {
    try {
      var fis:FileInputStream = new FileInputStream(args(0))
      
      //var bis:BufferedInputStream = new BufferedInputStream(fis) //r=1
      var bis:BufferedInputStream =  /*!*/ //r=1

      var i:Int = bis.read();
      while (i != -1) {
        System.out.println(i);
      }
      fis.close();
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
