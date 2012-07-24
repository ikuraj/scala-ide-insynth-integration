package javaapi.JTextAreaStringtext

//http://www.java2s.com/Code/JavaAPI/javax.swing/newJTextAreaStringtext.htm

import javax.swing.JTextArea;

class Main {
  def main(argv:Array[String]) {
    // Create a text area with some initial text
    var textarea:JTextArea = new JTextArea("Initial Text") //r=3

    var rows = 20;
    var cols = 30;
    textarea = new JTextArea("Initial Text", rows, cols);
  }
}



/*
import javax.swing.JTextArea;

public class Main {
  public static void main(String[] argv) {
    // Create a text area with some initial text
    JTextArea textarea = new JTextArea("Initial Text");

    int rows = 20;
    int cols = 30;
    textarea = new JTextArea("Initial Text", rows, cols);

  }
}
*/
