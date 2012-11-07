package gjavaapi.JTree

//http://www.java2s.com/Code/JavaAPI/javax.swing/newJTree.htm

import java.awt._

import javax.swing._

class MainClass {
  def main(args:Array[String]) {
    var f:JFrame = new JFrame("JTree Sample")
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    var tree:JTree = new JTree() //r=1
    var tree:JTree =  /*!*/ //r=1
    var scrollPane:JScrollPane = new JScrollPane(tree);
    f.add(scrollPane, BorderLayout.CENTER);
    f.setSize(300, 200);
    f.setVisible(true);
  }
}


/*
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

public class MainClass {
  public static void main(String args[]) {
    JFrame f = new JFrame("JTree Sample");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JTree tree = new JTree();
    JScrollPane scrollPane = new JScrollPane(tree);
    f.add(scrollPane, BorderLayout.CENTER);
    f.setSize(300, 200);
    f.setVisible(true);
  }
}
*/
