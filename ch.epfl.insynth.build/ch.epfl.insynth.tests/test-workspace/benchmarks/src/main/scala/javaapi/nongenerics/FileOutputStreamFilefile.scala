package javaapi.FileOutputStreamFilefile

//http://www.java2s.com/Code/JavaAPI/java.io/newFileOutputStreamFilefile.htm

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

class MainClass {
  def main(args:Array[String]) {
    try {
      var tempFile:File = File.createTempFile("myfile", ".tmp");
      var fout:FileOutputStream = new FileOutputStream(tempFile) //r=1
      var fout:FileOutputStream =  /*!*/ //r=1
      
      var out:PrintStream = new PrintStream(fout)
      
      out.println("some text");
    } catch {
      case ex =>
      System.out.println("There was a problem creating/writing to the temp file");
      ex.printStackTrace();
    }
  }
}

/*
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class MainClass {
  public static void main(String[] args) {
    try {
      File tempFile = File.createTempFile("myfile", ".tmp");
      FileOutputStream fout = new FileOutputStream(tempFile);
      PrintStream out = new PrintStream(fout);
      out.println("some text");
    } catch (IOException ex) {
      System.out.println("There was a problem creating/writing to the temp file");
      ex.printStackTrace();
    }
  }
}
*/
