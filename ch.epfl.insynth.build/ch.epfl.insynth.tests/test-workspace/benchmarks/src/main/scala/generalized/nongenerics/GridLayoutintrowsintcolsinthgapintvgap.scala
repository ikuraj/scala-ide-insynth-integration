package gjavaapi.GridLayoutintrowsintcolsinthgapintvgap

//http://www.java2s.com/Code/JavaAPI/java.awt/newGridLayoutintrowsintcolsinthgapintvgap.htm

 
import java.awt._

import javax.swing._

class Main {
  def main(args:Array[String]) {
    var aWindow:JFrame = new JFrame("This is a Grid Layout");
    aWindow.setBounds(30, 30, 300, 300);
    aWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    var grid:GridLayout = new GridLayout(3, 4, 30, 20) //r>5
    var content:Container = aWindow.getContentPane();
    content.setLayout(grid);
    var button:JButton = null;
    for (i <- 1 to 10) {
      button = new JButton(" Press " + i)
      content.add(button);
    }
    aWindow.pack();
    aWindow.setVisible(true);
  }
}

/*
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Main {
  public static void main(String[] args) {
    JFrame aWindow = new JFrame("This is a Grid Layout");
    aWindow.setBounds(30, 30, 300, 300);
    aWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    GridLayout grid = new GridLayout(3, 4, 30, 20);
    Container content = aWindow.getContentPane();
    content.setLayout(grid);
    JButton button = null;
    for (int i = 1; i <= 10; i++) {
      content.add(button = new JButton(" Press " + i));
    }
    aWindow.pack();
    aWindow.setVisible(true);
  }
}
*/
