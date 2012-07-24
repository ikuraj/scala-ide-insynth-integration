package long.javaapi.bufferedreaderfilereaderfilereader

//http://www.java2s.com/Code/JavaAPI/java.io/newBufferedReaderFileReaderfileReader.htm

import java.io.BufferedReader;
import java.io.FileReader;

class MainClass {

  def main(args:Array[String]) {
    try {
      var br:BufferedReader = new BufferedReader(new FileReader(args(0))) //r=2
 
      var s:String = br.readLine()
      while(s != null)
        System.out.println(s);
    } catch{
      case e => System.out.println("Exception: " + e);
    }
  }
}

/*

import java.io.BufferedReader;
import java.io.FileReader;

public class MainClass {

  public static void main(String args[]) {
    try {
      FileReader fr = new FileReader(args[0]);
      BufferedReader br = new BufferedReader(fr);

      String s;
      while((s = br.readLine()) != null)
        System.out.println(s);

      fr.close();
    }
    catch(Exception e) {
      System.out.println("Exception: " + e);
    }
  }
}

*/
