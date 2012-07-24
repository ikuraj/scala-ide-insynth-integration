package long.javaapi.PrintStreamFilefile

//http://www.java2s.com/Code/JavaAPI/java.io/newPrintStreamFilefile.htm

import java.io.File;
import java.io.PrintStream;

class Main {
  def main(args:Array[String]) {
    var ps:PrintStream = new PrintStream(new File("C:\\a.txt")) //r=2

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


