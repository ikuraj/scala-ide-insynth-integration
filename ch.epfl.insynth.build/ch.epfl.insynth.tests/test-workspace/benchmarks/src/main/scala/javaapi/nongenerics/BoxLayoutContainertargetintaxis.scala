package javaapi.BoxLayoutContainertargetintaxis

//http://www.java2s.com/Code/JavaAPI/javax.swing/newBoxLayoutContainertargetintaxis.htm

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

class MainClass {

  def main(arg:Array[String]) {
    var frame:JFrame = new JFrame("Alignment Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    var labels = List[String]( "--", "----", "--------", "------------" ).toArray

    var container:JPanel = new JPanel();
    //var layout:BoxLayout = new BoxLayout(container, BoxLayout.Y_AXIS) //r>5
    var layout:BoxLayout =  /*!*/ //r>5
    container.setLayout(layout);

    for (i <- 0 to labels.length) {
      var button = new JButton(labels(i));
      container.add(button);
    }

    frame.add(container, BorderLayout.CENTER);

    frame.setSize(300, 200);
    frame.setVisible(true);
  }
}
 


/*
import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainClass {

  public static void main(String[] a) {
    JFrame frame = new JFrame("Alignment Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    String labels[] = { "--", "----", "--------", "------------" };

    JPanel container = new JPanel();
    BoxLayout layout = new BoxLayout(container, BoxLayout.Y_AXIS);
    container.setLayout(layout);

    for (int i = 0; i < labels.length; i++) {
      JButton button = new JButton(labels[i]);
      container.add(button);
    }

    frame.add(container, BorderLayout.CENTER);

    frame.setSize(300, 200);
    frame.setVisible(true);
  }
}
*/
