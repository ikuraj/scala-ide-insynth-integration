package gjavaapi.bytearrayinputstreambytebufintoffsetintlength

//http://www.java2s.com/Code/JavaAPI/java.io/newByteArrayInputStreambytebufintoffsetintlength.htm

import java.io._

class Main {
  def main(args:Array[String]) {
    var tmp:String = "abcdefghijklmnopqrstuvwxyz";
    var b:Array[Byte] = tmp.getBytes();
    var input1:ByteArrayInputStream = new ByteArrayInputStream(b)
    var input2:ByteArrayInputStream = new ByteArrayInputStream(b,0,0) //r>5
  }
}

/*
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Main {
  public static void main(String args[]) throws IOException {
    String tmp = "abcdefghijklmnopqrstuvwxyz";
    byte b[] = tmp.getBytes();
    ByteArrayInputStream input1 = new ByteArrayInputStream(b);
    ByteArrayInputStream input2 = new ByteArrayInputStream(b, 0, 3);
  }
}
*/




