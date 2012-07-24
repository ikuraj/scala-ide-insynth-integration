package long.javaapi.bufferedreaderreaderin

//http://www.java2s.com/Code/JavaAPI/java.io/newBufferedReaderReaderin.htm

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

class Main {

  def main(args:Array[String]){
    var in:BufferedReader = new BufferedReader(new InputStreamReader(new URL("?").openStream())) //r=3

    var line = in.readLine()
    while (line != null) {
      System.out.println(line);
      line = in.readLine()
    }
    in.close();
  }
}

/*
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Main {

  public static void main(String[] args) throws Exception {
    URL url = new URL("http://localhost:1776");
    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
    String line;
    while ((line = in.readLine()) != null) {
      System.out.println(line);
    }
    in.close();
  }
}
*/

