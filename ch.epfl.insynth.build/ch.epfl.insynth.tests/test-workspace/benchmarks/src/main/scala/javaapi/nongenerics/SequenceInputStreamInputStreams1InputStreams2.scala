package javaapi.SequenceInputStreamInputStreams1InputStreams2

//http://www.java2s.com/Code/JavaAPI/java.io/newSequenceInputStreamInputStreams1InputStreams2.htm

import java.io.FileInputStream;
import java.io.SequenceInputStream;

object MainClass {
  def main(args:Array[String]){

    var f1= new FileInputStream("ByteArrayIOApp.java");
    var f2 = new FileInputStream("FileIOApp.java");
//    var inStream:SequenceInputStream = new SequenceInputStream(f1,f2) //r=2
    var inStream:SequenceInputStream =  /*!*/ //r=2

    var eof:Boolean = false;
    var byteCount:Int = 0;
    while (!eof) {
      var c = inStream.read() //r=2
      if (c == -1)
        eof = true;
      else {
        System.out.print(c.toChar);
        byteCount+=1;
      }
    }
    System.out.println(byteCount + " bytes were read");
    inStream.close();
    f1.close();
    f2.close();
  }
}



/*
import java.io.FileInputStream;
import java.io.IOException;
import java.io.SequenceInputStream;

public class Main {
  public static void main(String args[]) throws IOException {
    FileInputStream f1 = new FileInputStream("ByteArrayIOApp.java");
    FileInputStream f2 = new FileInputStream("FileIOApp.java");
    SequenceInputStream  inStream = new SequenceInputStream(f1, f2);
    boolean eof = false;
    int byteCount = 0;
    while (!eof) {
      int c = inStream.read();
      if (c == -1)
        eof = true;
      else {
        System.out.print((char) c);
        ++byteCount;
      }
    }
    System.out.println(byteCount + " bytes were read");
    inStream.close();
    f1.close();
    f2.close();
  }
}
*/
