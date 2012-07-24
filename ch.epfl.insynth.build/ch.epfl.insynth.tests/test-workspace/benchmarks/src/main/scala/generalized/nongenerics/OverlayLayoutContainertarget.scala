package gjavaapi.OverlayLayoutContainertarget

//http://www.java2s.com/Code/JavaAPI/javax.swing/newOverlayLayoutContainertarget.htm

import java.awt._

import javax.swing._

class Main {
  def main(args:Array[String]) {
    var frame:JFrame = new JFrame("Overlay Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    var panel:JPanel = new JPanel() {
      override def isOptimizedDrawingEnabled() = {
        false
      }
    }
    var overlay:LayoutManager = new OverlayLayout(panel); //r>5
    panel.setLayout(overlay);

    var button:JButton = new JButton("Small");
    button.setMaximumSize(new Dimension(25, 25));
    button.setBackground(Color.white);
    panel.add(button);
    
    button = new JButton("Medium");
    button.setMaximumSize(new Dimension(50, 50));
    button.setBackground(Color.gray);
    panel.add(button);
    
    button = new JButton("Large");
    button.setMaximumSize(new Dimension(100, 100));
    button.setBackground(Color.black);
    panel.add(button);    

    frame.add(panel, BorderLayout.CENTER);

    frame.setSize(400, 300);
    frame.setVisible(true);
  }
}


/*
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

public class Main {
  public static void main(String args[]) {
    JFrame frame = new JFrame("Overlay Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel panel = new JPanel() {
      public boolean isOptimizedDrawingEnabled() {
        return false;
      }
    };
    LayoutManager overlay = new OverlayLayout(panel);
    panel.setLayout(overlay);

    JButton button = new JButton("Small");
    button.setMaximumSize(new Dimension(25, 25));
    button.setBackground(Color.white);
    panel.add(button);
    
    button = new JButton("Medium");
    button.setMaximumSize(new Dimension(50, 50));
    button.setBackground(Color.gray);
    panel.add(button);
    
    button = new JButton("Large");
    button.setMaximumSize(new Dimension(100, 100));
    button.setBackground(Color.black);
    panel.add(button);    

    frame.add(panel, BorderLayout.CENTER);

    frame.setSize(400, 300);
    frame.setVisible(true);
  }
}
*/
