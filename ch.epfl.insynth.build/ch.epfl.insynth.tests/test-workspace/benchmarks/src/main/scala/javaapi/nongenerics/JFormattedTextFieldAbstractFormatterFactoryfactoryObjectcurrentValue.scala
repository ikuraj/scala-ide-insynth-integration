package javaapi.JFormattedTextFieldAbstractFormatterFactoryfactoryObjectcurrentValue

//http://www.java2s.com/Code/JavaAPI/javax.swing/newJFormattedTextFieldAbstractFormatterFactoryfactoryObjectcurrentValue.htm
 
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

class MainClass {
  def main(args:Array[String]){
    var frame = new JFrame("Formatted Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    var displayFormat = new SimpleDateFormat("yyyy--MMMM--dd");
    var displayFormatter = new DateFormatter(displayFormat);
    var editFormat = new SimpleDateFormat("MM/dd/yy");
    var editFormatter = new DateFormatter(editFormat);
    var factory = new DefaultFormatterFactory(displayFormatter, displayFormatter, editFormatter);
//    var date2TextField:JFormattedTextField = new JFormattedTextField(factory) //r=4
    var date2TextField:JFormattedTextField =  /*!*/ //r=4
    
    frame.add(date2TextField, BorderLayout.NORTH);

    var actionListener = new ActionListener() {
      override def actionPerformed(actionEvent:ActionEvent) {
        var source = actionEvent.getSource().asInstanceOf[JFormattedTextField];
        var value = source.getValue();
        System.out.println("Class: " + value.getClass());
        System.out.println("Value: " + value);
      }
    };
    date2TextField.addActionListener(actionListener);
    frame.add(new JTextField(), BorderLayout.SOUTH);

    frame.setSize(250, 100);
    frame.setVisible(true);
  }
}

/*
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

public class MainClass {
  public static void main(String args[]) throws Exception {
    JFrame frame = new JFrame("Formatted Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    DateFormat displayFormat = new SimpleDateFormat("yyyy--MMMM--dd");
    DateFormatter displayFormatter = new DateFormatter(displayFormat);
    DateFormat editFormat = new SimpleDateFormat("MM/dd/yy");
    DateFormatter editFormatter = new DateFormatter(editFormat);
    DefaultFormatterFactory factory = new DefaultFormatterFactory(displayFormatter,
        displayFormatter, editFormatter);
    JFormattedTextField date2TextField = new JFormattedTextField(factory, new Date());
    frame.add(date2TextField, BorderLayout.NORTH);

    ActionListener actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        JFormattedTextField source = (JFormattedTextField) actionEvent.getSource();
        Object value = source.getValue();
        System.out.println("Class: " + value.getClass());
        System.out.println("Value: " + value);
      }
    };
    date2TextField.addActionListener(actionListener);
    frame.add(new JTextField(), BorderLayout.SOUTH);

    frame.setSize(250, 100);
    frame.setVisible(true);
  }
}
*/
