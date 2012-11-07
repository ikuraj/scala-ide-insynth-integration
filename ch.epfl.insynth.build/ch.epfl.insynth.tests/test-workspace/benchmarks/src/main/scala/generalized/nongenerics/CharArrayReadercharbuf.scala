package gjavaapi.CharArrayReadercharbuf

//http://www.java2s.com/Code/JavaAPI/java.io/newCharArrayReadercharbuf.htm

import java.io._

class Main {
  def main(args:Array[String]) {
    var outStream:CharArrayWriter = new CharArrayWriter()
    var s:String = "This is a test.";
    for (i <- 0 until s.length())
      outStream.write(s.charAt(i));
    System.out.println("outstream: " + outStream);
    System.out.println("size: " + outStream.size());
    //var inStream:CharArrayReader = new CharArrayReader(outStream.toCharArray()) //r=1
    var inStream:CharArrayReader =  /*!*/ //r=1

    var ch:Int = 0;
    var string = ""
    var sb:StringBuffer = new StringBuffer()
    ch = inStream.read()
    while (ch != -1){
      sb.append(ch.toChar);
      ch = inStream.read()
    }
    s = sb.toString();
    System.out.println(s.length() + " characters were read");
    System.out.println("They are: " + s);
  }
}

/*
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;

public class Main {
  public static void main(String args[]) throws IOException {
    CharArrayWriter outStream = new CharArrayWriter();
    String s = "This is a test.";
    for (int i = 0; i < s.length(); ++i)
      outStream.write(s.charAt(i));
    System.out.println("outstream: " + outStream);
    System.out.println("size: " + outStream.size());
    CharArrayReader inStream;
    inStream = new CharArrayReader(outStream.toCharArray());
    int ch = 0;
    StringBuffer sb = new StringBuffer("");
    while ((ch = inStream.read()) != -1)
      sb.append((char) ch);
    s = sb.toString();
    System.out.println(s.length() + " characters were read");
    System.out.println("They are: " + s);
  }
}
*/
