package gjavaapi.JTableObjectnameObjectdata

//http://www.java2s.com/Code/JavaAPI/javax.swing/newJTableObjectnameObjectdata.htm
 
import java.awt._

import javax.swing._

class MainClass {
  def main(args:Array[String]) {
    var f = new JFrame("JTable Sample");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    var rows:Array[Array[Object]] = Array(Array[Object]("A", "Name 1", "38.94"),
					  Array[Object]("B", "Name 2", "7.70"), 
					  Array[Object]("C", "Name 3", "112.65"))
    
    
    var columns:Array[Object] = Array[Object]("Symbol", "Name", "Price");
//    var table:JTable  = new JTable(rows, columns) //r>5
    var table:JTable  =  /*!*/ //r>5
    var scrollPane:JScrollPane = new JScrollPane(table);
    f.add(scrollPane, BorderLayout.CENTER);
    f.setSize(300, 200);
    f.setVisible(true);
  }
}


/*
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class MainClass {
  public static void main(String args[]) {
    JFrame f = new JFrame("JTable Sample");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Object rows[][] = { { "A", "Name 1", "38.94" },
                        { "B", "Name 2", "7.70" }, 
                        { "C", "Name 3", "112.65" } };
    
    
    Object columns[] = { "Symbol", "Name", "Price" };
    JTable table = new JTable(rows, columns);
    JScrollPane scrollPane = new JScrollPane(table);
    f.add(scrollPane, BorderLayout.CENTER);
    f.setSize(300, 200);
    f.setVisible(true);
  }
}
*/
