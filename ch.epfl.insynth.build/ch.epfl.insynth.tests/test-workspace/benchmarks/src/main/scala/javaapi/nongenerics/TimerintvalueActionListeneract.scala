package javaapi.TimerintvalueActionListeneract

//http://www.java2s.com/Code/JavaAPI/javax.swing/newTimerintvalueActionListeneract.htm

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer

class MainClass {
  def main(args:Array[String]) {
    var actionListener:ActionListener = new ActionListener() {
      def actionPerformed(actionEvent:ActionEvent) {
        System.out.println("Hello World Timer");
      }
    }

//    var timer:Timer = new Timer(500,actionListener) //r=1
    var timer:Timer =  /*!*/ //r=1
    timer.start();
  }
}

/*
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class MainClass {
  public static void main(String args[]) {
    ActionListener actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        System.out.println("Hello World Timer");
      }
    };
    Timer timer = new Timer(500, actionListener);
    timer.start();
  }
}
*/
