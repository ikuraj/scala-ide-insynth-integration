package gjavaapi.StreamTokenizerFileReaderfileReader

//http://www.java2s.com/Code/JavaAPI/java.io/newStreamTokenizerFileReaderfileReader.htm

import java.io._

class MainClass {

  def main(args:Array[String]) {

    try {
      var fr:FileReader = new FileReader(args(0))
      var br:BufferedReader = new BufferedReader(fr)
//      var st:StreamTokenizer = new StreamTokenizer(br) //r=1
      var st:StreamTokenizer =  /*!*/ //r=1

      st.ordinaryChar('.');

      st.wordChars('\'', '\'');

      while (st.nextToken() != StreamTokenizer.TT_EOF) {
        st.ttype match {
        case StreamTokenizer.TT_WORD =>
          System.out.println(st.lineno() + ") " + st.sval);
        case StreamTokenizer.TT_NUMBER =>
          System.out.println(st.lineno() + ") " + st.nval);
        case _ =>
          System.out.println(st.lineno() + ") " + st.ttype.toChar);
        }
      }

      fr.close();
    } catch {
      case e => System.out.println("Exception: " + e);
    }
  }
}



/*
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StreamTokenizer;

public class MainClass {

  public static void main(String args[]) {

    try {
      FileReader fr = new FileReader(args[0]);
      BufferedReader br = new BufferedReader(fr);
      StreamTokenizer st = new StreamTokenizer(br);

      st.ordinaryChar('.');

      st.wordChars('\'', '\'');

      while (st.nextToken() != StreamTokenizer.TT_EOF) {
        switch (st.ttype) {
        case StreamTokenizer.TT_WORD:
          System.out.println(st.lineno() + ") " + st.sval);
          break;
        case StreamTokenizer.TT_NUMBER:
          System.out.println(st.lineno() + ") " + st.nval);
          break;
        default:
          System.out.println(st.lineno() + ") " + (char) st.ttype);
        }
      }

      fr.close();
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
  }
}
*/
