package gjavaapi.JToggleButtonStringtext

//http://www.java2s.com/Code/JavaAPI/javax.swing/newJToggleButtonStringtext.htm

import java.awt._

import javax.swing._

class MainClass {
  def  main(args:Array[String]) {
    var f:JFrame = new JFrame("JToggleButton Sample"); //r=2
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.add(new JToggleButton("North"), BorderLayout.NORTH);
    f.add(new JToggleButton("East"), BorderLayout.EAST);
    f.add(new JToggleButton("West"), BorderLayout.WEST);
    f.add(new JToggleButton("Center"), BorderLayout.CENTER);
    f.add(new JToggleButton("South"), BorderLayout.SOUTH);
    f.setSize(300, 200);
    f.setVisible(true);
  }
}

/*
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JToggleButton;

public class MainClass {
  public static void main(String args[]) {
    JFrame f = new JFrame("JToggleButton Sample");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.add(new JToggleButton("North"), BorderLayout.NORTH);
    f.add(new JToggleButton("East"), BorderLayout.EAST);
    f.add(new JToggleButton("West"), BorderLayout.WEST);
    f.add(new JToggleButton("Center"), BorderLayout.CENTER);
    f.add(new JToggleButton("South"), BorderLayout.SOUTH);
    f.setSize(300, 200);
    f.setVisible(true);
  }
}
*/
