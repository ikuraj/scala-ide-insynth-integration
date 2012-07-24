package javaapi.LineNumberReaderReaderin

//http://www.java2s.com/Code/JavaAPI/java.io/newLineNumberReaderReaderin.htm

import java.io.InputStreamReader;
import java.io.LineNumberReader;


class MainClass {

  def main(args:Array[String]) {
    var lineCounter:LineNumberReader = new LineNumberReader(new InputStreamReader(System.in)) //r=1

    var nextLine:String = null;
    System.out.println("Type any text and press return. Type 'exit' to quit the program.");
    try {
      nextLine = lineCounter.readLine()
      var cond = false
      while (nextLine.indexOf("exit") == -1 && !cond) {
        if (nextLine == null)
          cond = true;
        System.out.print(lineCounter.getLineNumber());
        System.out.print(": ");
        System.out.println(nextLine);
	nextLine = lineCounter.readLine()
      }
    } catch {
      case done => done.printStackTrace();
    }
  }
}



/*
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class MainClass {

  public static void main(String[] args) throws Exception {
    LineNumberReader lineCounter = new LineNumberReader(new InputStreamReader(System.in));

    String nextLine = null;
    System.out.println("Type any text and press return. Type 'exit' to quit the program.");
    try {
      while ((nextLine = lineCounter.readLine()).indexOf("exit") == -1) {
        if (nextLine == null)
          break;
        System.out.print(lineCounter.getLineNumber());
        System.out.print(": ");
        System.out.println(nextLine);
      }
    } catch (Exception done) {
      done.printStackTrace();
    }
  }
}
*/
