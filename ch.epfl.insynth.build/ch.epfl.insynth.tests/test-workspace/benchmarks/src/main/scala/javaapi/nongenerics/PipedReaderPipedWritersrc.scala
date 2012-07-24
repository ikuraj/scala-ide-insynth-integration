package javaapi.PipedReaderPipedWritersrc

//http://www.java2s.com/Code/JavaAPI/java.io/newPipedReaderPipedWritersrc.htm

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

class Main {
  def main() {
    var pw:PipedWriter = new PipedWriter()
//    var pr:PipedReader = new PipedReader(pw) //r=2
    var pr:PipedReader =  /*!*/ //r=2

    try {
      for (i <- 0 until 15)
        pw.write(" A" + i + '\n');
      var ch = pr.read();
      while (ch != -1)
        System.out.print(ch.toChar);
    } catch {
      case e =>
    }
  }
}


/*
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

public class Main {
  public static void main(String[] args) throws IOException {
    PipedWriter pw = new PipedWriter();
    PipedReader pr = new PipedReader(pw);
    int ch;
    try {
      for (int i = 0; i < 15; i++)
        pw.write(" A" + i + '\n');
      while ((ch = pr.read()) != -1)
        System.out.print((char) ch);
    } catch (IOException e) {
    }
  }
}
*/
