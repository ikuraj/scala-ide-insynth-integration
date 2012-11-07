package gjavaapi.ByteArrayOutputStreamintsize

//http://www.java2s.com/Code/JavaAPI/java.io/newByteArrayOutputStreamintsize.htm

import java.io._

class MainClass {
  def  main(args:Array[String]){
//    var f:ByteArrayOutputStream = new ByteArrayOutputStream(12) //r=2
    var f:ByteArrayOutputStream =  /*!*/ //r=2
    System.out.println("Please 10 characters and a return");
    while (f.size() != 10) {
      f.write(System.in.read());
    }
    System.out.println("Buffer as a string");
    System.out.println(f.toString());
    System.out.println("Into array");
    var b:Array[Byte] = f.toByteArray()
    for (i <- 0 until b.length) {
      System.out.print(b(i).toChar);
    }
    System.out.println();
    var f2:OutputStream = new FileOutputStream("test.txt")
    f.writeTo(f2);
    f.reset();
    System.out.println("10 characters and a return");
    var size:Int = f.size() //r=2
    while (size != 10) {
      f.write(System.in.read());
    }
    System.out.println("Done..");
  }
}


/*
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainClass {
  public static void main(String args[]) throws Exception {
    ByteArrayOutputStream f = new ByteArrayOutputStream(12);
    System.out.println("Please 10 characters and a return");
    while (f.size() != 10) {
      f.write(System.in.read());
    }
    System.out.println("Buffer as a string");
    System.out.println(f.toString());
    System.out.println("Into array");
    byte b[] = f.toByteArray();
    for (int i = 0; i < b.length; i++) {
      System.out.print((char) b[i]);
    }
    System.out.println();
    OutputStream f2 = new FileOutputStream("test.txt");
    f.writeTo(f2);
    f.reset();
    System.out.println("10 characters and a return");
    while (f.size() != 10) {
      f.write(System.in.read());
    }
    System.out.println("Done..");
  }
}
*/
