package gjavaapi.pipedwriter

//http://www.java2s.com/Code/JavaAPI/java.io/newPipedWriter.htm

import java.io._

class Main {
  def main(){
    var pw:PipedWriter = new PipedWriter() //r=1
    var pr:PipedReader = new PipedReader(pw)
    var ch:Int = 0;
    try {
      for (i <- 0 until 15)
        pw.write(" A" + i + '\n');
      ch = pr.read()
      while (ch != -1){
        System.out.print(ch.toChar)
	ch = pr.read()
      }
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
