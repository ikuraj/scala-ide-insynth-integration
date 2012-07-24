package paper.SequenceInputStreamInputStreams1InputStreams2

import java.io.FileInputStream;
import java.io.SequenceInputStream;

object MainClass {
  def main(args:Array[String]){

    var f1= "testfiles\\Text1.txt" 
    var f2 ="testfiles\\Text2.txt"
    var inStream:SequenceInputStream = new SequenceInputStream(new FileInputStream(f1),new FileInputStream(f2)) //r=2

    var eof:Boolean = false;
    var byteCount:Int = 0;
    while (!eof) {
      var c = inStream.read()
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

  /* Uncomment to run tests
  def test(){
    main(Array[String]())
  }
  */ 
}

//Inspired by http://www.java2s.com/Code/JavaAPI/java.io/newSequenceInputStreamInputStreams1InputStreams2.htm

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
