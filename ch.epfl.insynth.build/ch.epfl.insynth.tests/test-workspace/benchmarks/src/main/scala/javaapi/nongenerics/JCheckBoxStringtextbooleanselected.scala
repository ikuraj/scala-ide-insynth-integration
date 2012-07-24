package javaapi.JCheckBoxStringtextbooleanselected

//http://www.java2s.com/Code/JavaAPI/javax.swing/newJCheckBoxStringtextbooleanselected.htm
 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

class Main extends JFrame with ActionListener {

  def init() {
    var checkbox:JCheckBox = new JCheckBox("Show Title", true); //r>5
    checkbox.addActionListener(this);
    add(checkbox);

    setSize(280, 200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  def main(args:Array[String]) {
    new Main();
  }

  def actionPerformed(e:ActionEvent) {
    if (this.getTitle() == "") {
      this.setTitle("Checkbox example");
    } else {
      this.setTitle("");
    }
  }
}


/*
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

public class Main extends JFrame implements ActionListener {

  public Main() {
    JCheckBox checkbox = new JCheckBox("Show Title", true);
    checkbox.addActionListener(this);
    add(checkbox);

    setSize(280, 200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  public static void main(String[] args) {
    new Main();
  }

  public void actionPerformed(ActionEvent e) {
    if (this.getTitle() == "") {
      this.setTitle("Checkbox example");
    } else {
      this.setTitle("");
    }
  }
}
*/
