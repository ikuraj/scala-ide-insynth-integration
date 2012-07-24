package javaapi.StreamTokenizerReaderr

//http://www.java2s.com/Code/JavaAPI/java.io/newStreamTokenizerReaderr.htm

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;

class Main {

  def main(args:Array[String]) {
    var tf:StreamTokenizer = new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in))) //r=3
    var i:Int = tf.nextToken() 
    while (i != StreamTokenizer.TT_EOF) {
      i match {
      case StreamTokenizer.TT_EOF =>
        System.out.println("End of file");
      case StreamTokenizer.TT_EOL =>
        System.out.println("End of line");
      case StreamTokenizer.TT_NUMBER =>
        System.out.println("Number " + tf.nval);
      case StreamTokenizer.TT_WORD =>
        System.out.println("Word, length " + tf.sval.length() + "->" + tf.sval);
      case _=>
        System.out.println("What is it? i = " + i);
      }
      i = tf.nextToken()
    }
  }
}


/*
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;

public class Main {

  public static void main(String[] av) throws IOException {
    StreamTokenizer tf = new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)));
    int i;

    while ((i = tf.nextToken()) != StreamTokenizer.TT_EOF) {
      switch (i) {
      case StreamTokenizer.TT_EOF:
        System.out.println("End of file");
        break;
      case StreamTokenizer.TT_EOL:
        System.out.println("End of line");
        break;
      case StreamTokenizer.TT_NUMBER:
        System.out.println("Number " + tf.nval);
        break;
      case StreamTokenizer.TT_WORD:
        System.out.println("Word, length " + tf.sval.length() + "->" + tf.sval);
        break;
      default:
        System.out.println("What is it? i = " + i);
      }
    }

  }
}
*/
