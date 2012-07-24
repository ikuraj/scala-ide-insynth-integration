package gjavaapi.JViewport

//http://www.java2s.com/Code/JavaAPI/javax.swing/newJViewport.htm

import java.awt._

import javax.swing._

class MainClass {

  def main(args:Array[String]) {
    var rows:Array[Array[Object]] = Array(Array[Object]("one", "1"), Array[Object]( "two", "2"), Array[Object]("three", "3"))
    var headers:Array[Object] = Array[Object]("English", "Digit")
    
    var fname = "Scrollless Table"
    var frame:JFrame = new JFrame(fname)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    var table:JTable = new JTable(rows,headers)
    
    var scrollPane:JScrollPane = new JScrollPane(table)
    var viewport:JViewport = new JViewport() //r=3
    viewport.setView(table);
    scrollPane.setColumnHeaderView(new JLabel("table header here"));
    scrollPane.setRowHeaderView(viewport);
    
    frame.add(scrollPane, BorderLayout.CENTER);
    frame.setSize(300, 150);
    frame.setVisible(true);
  }
}

/*
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;

public class MainClass {

  public static void main(final String args[]) {
    final Object rows[][] = { { "one", "1" }, { "two", "2" },
        { "three", "3" } };
    final Object headers[] = { "English", "Digit" };
    
    JFrame frame = new JFrame("Scrollless Table");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JTable table = new JTable(rows,headers);
    
    JScrollPane scrollPane = new JScrollPane(table);
    JViewport viewport = new JViewport();
    viewport.setView(table);
    scrollPane.setColumnHeaderView( new JLabel("table header here"));
    scrollPane.setRowHeaderView(viewport);
    
    frame.add(scrollPane, BorderLayout.CENTER);
    frame.setSize(300, 150);
    frame.setVisible(true);
  }
}

*/
