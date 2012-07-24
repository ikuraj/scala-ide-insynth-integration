package gjavaapi.SequenceInputStreamInputStreams1InputStreams2

import java.io._

object Main {
  def main(args:Array[String]):Unit = {
    var f1 = new FileInputStream("ByteArrayIOApp.java");
    var f2 = new FileInputStream("FileIOApp.java");

    var inStream:SequenceInputStream = new SequenceInputStream(f1, f2); //r>5

    var eof:Boolean = false;
    var byteCount:Int = 0;
    while (!eof) {
      var c:Int = inStream.read()
      if (c == -1)
        eof = true;
      else {
        System.out.print(c.toChar);
        byteCount+=1;
      }
    }
    System.out.println(byteCount + " bytes were read");
    inStream.close();

  }
}


//http://www.java2s.com/Code/JavaAPI/java.io/newSequenceInputStreamInputStreams1InputStreams2.htm

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










