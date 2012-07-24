package javaapi.FileWriterStringfileNamebooleanappend

//http://www.java2s.com/Code/JavaAPI/java.io/newFileWriterStringfileNamebooleanappend.htm

import java.io.BufferedWriter;
import java.io.FileWriter;

class Main {
  def main(args:Array[String]) {
    var out:BufferedWriter = new BufferedWriter(new FileWriter("filename", true)); //r>5
    out.write("aString");
    out.close();
  }
}



/*
import java.io.BufferedWriter;
import java.io.FileWriter;

public class Main {
  public static void main(String[] argv) throws Exception {
    BufferedWriter out = new BufferedWriter(new FileWriter("filename", true));
    out.write("aString");
    out.close();
  }
}
*/

