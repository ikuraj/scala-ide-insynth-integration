package gjavaapi.JCheckBoxStringtext

//http://www.java2s.com/Code/JavaAPI/javax.swing/newJCheckBoxStringtext.htm

 
import java.awt._

import javax.swing._
import javax.swing.border._

class Main {
  def main(args:Array[String]) {
    var title = if(args.length == 0) "CheckBox Sample" else args(0)
    var frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    var panel:JPanel = new JPanel(new GridLayout(0, 1));
    var border:Border = BorderFactory.createTitledBorder("Pizza Toppings");
    panel.setBorder(border);
    var check:JCheckBox = new JCheckBox("Anchovies"); //r=3
    panel.add(check);
    check = new JCheckBox("Garlic");
    panel.add(check);
    check = new JCheckBox("Onions");
    panel.add(check);
    check = new JCheckBox("Pepperoni");
    panel.add(check);
    check = new JCheckBox("Spinach");
    panel.add(check);
    var button:JButton = new JButton("Submit");
    var contentPane:Container = frame.getContentPane();
    contentPane.add(panel, BorderLayout.CENTER);
    contentPane.add(button, BorderLayout.SOUTH);
    frame.setSize(300, 200);
    frame.setVisible(true);
  }
}


/*
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class Main {
  public static void main(String args[]) {
    String title = (args.length == 0 ? "CheckBox Sample" : args[0]);
    JFrame frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel panel = new JPanel(new GridLayout(0, 1));
    Border border = BorderFactory.createTitledBorder("Pizza Toppings");
    panel.setBorder(border);
    JCheckBox check = new JCheckBox("Anchovies");
    panel.add(check);
    check = new JCheckBox("Garlic");
    panel.add(check);
    check = new JCheckBox("Onions");
    panel.add(check);
    check = new JCheckBox("Pepperoni");
    panel.add(check);
    check = new JCheckBox("Spinach");
    panel.add(check);
    JButton button = new JButton("Submit");
    Container contentPane = frame.getContentPane();
    contentPane.add(panel, BorderLayout.CENTER);
    contentPane.add(button, BorderLayout.SOUTH);
    frame.setSize(300, 200);
    frame.setVisible(true);
  }
}
*/
