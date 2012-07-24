package gjavaapi.DataInputStreamFileInputStreamfileInputStream

//http://www.java2s.com/Code/JavaAPI/java.io/newDataInputStreamFileInputStreamfileInputStream.htm

import java.io._

class MainClass {

  def main(args:Array[String]) {
    try {
      var fis = new FileInputStream("fileName.dat");

      // Create a data input stream
      var dis:DataInputStream = new DataInputStream(fis); //r=1

      // Read and display data
      System.out.println(dis.readBoolean());
      System.out.println(dis.readByte());
      System.out.println(dis.readChar());
      System.out.println(dis.readDouble());
      System.out.println(dis.readFloat());
      System.out.println(dis.readInt());
      System.out.println(dis.readLong());
      System.out.println(dis.readShort());

      // Close file input stream
      fis.close();
    } catch {
      case e => System.out.println("Exception: " + e);
    }
  }
}
 

/*
import java.io.DataInputStream;
import java.io.FileInputStream;

public class MainClass {

  public static void main(String args[]) {
    try {

      FileInputStream fis = new FileInputStream("fileName.dat");

      // Create a data input stream
      DataInputStream dis = new DataInputStream(fis);

      // Read and display data
      System.out.println(dis.readBoolean());
      System.out.println(dis.readByte());
      System.out.println(dis.readChar());
      System.out.println(dis.readDouble());
      System.out.println(dis.readFloat());
      System.out.println(dis.readInt());
      System.out.println(dis.readLong());
      System.out.println(dis.readShort());

      // Close file input stream
      fis.close();
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
  }
}
*/
