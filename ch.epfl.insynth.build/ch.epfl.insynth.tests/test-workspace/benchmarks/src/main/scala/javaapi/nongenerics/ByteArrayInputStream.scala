package javaapi.bytearrayinputstream

//http://www.java2s.com/Code/JavaAPI/java.io/newByteArrayInputStreambytebuf.htm

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

class Main {
  def main(args:Array[String]){
    try {
       //var in3:DataInputStream = new DataInputStream(new ByteArrayInputStream("a dbcde".getBytes())); //r>5
       var in3:DataInputStream =  /*!*/ //r>5
       while(true)
	System.out.print(in3.readByte().toChar);
    } catch {
      case e => System.err.println("End of stream");
    }
  }
}


/*
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

public class Main {
  public static void main(String[] args) throws IOException {

    try {
      DataInputStream in3 = new DataInputStream(
        new ByteArrayInputStream("a dbcde".getBytes()));
      while(true)
        System.out.print((char)in3.readByte());
    } catch(EOFException e) {
      System.err.println("End of stream");
    }


  }
}
*/
