package javaapi.DataOutputStreamFileOutputStreamfileOutputStream

//http://www.java2s.com/Code/JavaAPI/java.io/newDataOutputStreamFileOutputStreamfileOutputStream.htm

import java.io.DataOutputStream;
import java.io.FileOutputStream;

class MainClass {

  def main(args:Array[String]) {
    try {
      var fos:FileOutputStream = new FileOutputStream(args(0))
//      var dos:DataOutputStream = new DataOutputStream(fos) //r=1
      var dos:DataOutputStream =  /*!*/ //r=1

      dos.writeBoolean(false);
      dos.writeByte(java.lang.Byte.MAX_VALUE);
      dos.writeChar('A');
      dos.writeDouble(java.lang.Double.MAX_VALUE);
      dos.writeFloat(java.lang.Float.MAX_VALUE);
      dos.writeInt(java.lang.Integer.MAX_VALUE);
      dos.writeLong(java.lang.Long.MAX_VALUE);
      dos.writeShort(java.lang.Short.MAX_VALUE);

      fos.close();
    } catch {
      case e => System.out.println("Exception: " + e);
    }
  }
}


/*
import java.io.DataOutputStream;
import java.io.FileOutputStream;

public class MainClass {

  public static void main(String args[]) {
    try {

      FileOutputStream fos = new FileOutputStream(args[0]);

      DataOutputStream dos = new DataOutputStream(fos);

      dos.writeBoolean(false);
      dos.writeByte(Byte.MAX_VALUE);
      dos.writeChar('A');
      dos.writeDouble(Double.MAX_VALUE);
      dos.writeFloat(Float.MAX_VALUE);
      dos.writeInt(Integer.MAX_VALUE);
      dos.writeLong(Long.MAX_VALUE);
      dos.writeShort(Short.MAX_VALUE);

      fos.close();
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
  }
}
*/
