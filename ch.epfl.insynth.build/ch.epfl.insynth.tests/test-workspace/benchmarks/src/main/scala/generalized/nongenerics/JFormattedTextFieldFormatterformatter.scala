package gjavaapi.JFormattedTextFieldFormatterformatter

//http://www.java2s.com/Code/JavaAPI/javax.swing/newJFormattedTextFieldFormatterformatter.htm

import java.awt._
import java.text._

import javax.swing._
import javax.swing.text._

class MainClass {
  def main(args:Array[String]) {
    var f = new JFrame("JFormattedTextField Sample");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    var rowOne = Box.createHorizontalBox();
    rowOne.add(new JLabel("SSN:"));
    try {
//      var mf1:MaskFormatter = new MaskFormatter("###-##-####")//r=2
      var mf1:MaskFormatter =  /*!*///r=2
      rowOne.add(new JFormattedTextField(mf1));
    } catch {
      case _ =>
    }
    f.add(rowOne, BorderLayout.NORTH);

    f.setSize(300, 100);
    f.setVisible(true);
  }
}



/*
import java.awt.BorderLayout;
import java.text.ParseException;

import javax.swing.Box;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.text.MaskFormatter;

public class MainClass {
  public static void main(String args[]) {
    JFrame f = new JFrame("JFormattedTextField Sample");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Box rowOne = Box.createHorizontalBox();
    rowOne.add(new JLabel("SSN:"));
    try {
      MaskFormatter mf1 = new MaskFormatter("###-##-####");
      rowOne.add(new JFormattedTextField(mf1));
    } catch (ParseException e) {
    }
    f.add(rowOne, BorderLayout.NORTH);

    f.setSize(300, 100);
    f.setVisible(true);
  }
}
*/
