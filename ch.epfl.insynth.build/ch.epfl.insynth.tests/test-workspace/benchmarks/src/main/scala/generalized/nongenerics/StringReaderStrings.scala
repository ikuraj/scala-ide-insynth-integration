package gjavaapi.StringReaderStrings

//http://www.java2s.com/Code/JavaAPI/java.io/newStringReaderStrings.htm

import java.io._

class Main {
  def main(args:Array[String]) {
    var in2:StringReader = new StringReader("a bc ddd") //r=1
    var c = in2.read()
    while (c != -1) {
      System.out.print(c.toChar);
      c = in2.read()
    }
  }
}
 

/*
import java.io.IOException;
import java.io.StringReader;

public class Main {
  public static void main(String[] args) throws IOException {


    StringReader in2 = new StringReader("a bc ddd");
    int c;
    while ((c = in2.read()) != -1)
      System.out.print((char) c);

  }
}
*/
