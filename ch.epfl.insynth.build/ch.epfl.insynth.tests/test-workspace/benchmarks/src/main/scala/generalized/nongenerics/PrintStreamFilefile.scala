package gjavaapi.PrintStreamFilefile

//http://www.java2s.com/Code/JavaAPI/java.io/newPrintStreamFilefile.htm

import java.io._

class Main {
  def main(args:Array[String]) {
    var file:File = new File("C:\\a.txt")
    //var ps:PrintStream = new PrintStream(file) //r>5
    var ps:PrintStream =  /*!*/ //r>5

    System.setOut(ps);
    System.out.println("To File");
  }
}

/*
import java.io.File;
import java.io.PrintStream;

public class Main {
  public static void main(String[] args) throws Exception {
    File file = new File("C:\\a.txt");
    PrintStream ps = new PrintStream(file);
    System.setOut(ps);
    System.out.println("To File");
  }
}
*/


