package long.javaapi.JButtonStringtextIconicon

//http://www.java2s.com/Code/JavaAPI/javax.swing/newJButtonStringtextIconicon.htm

 
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
class Main {
  def main(args:Array[String]) {
    var frame:JFrame = new JFrame("DefaultButton");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    var button3:JButton = new JButton("?",new ImageIcon("?")) //r>5
    frame.add(button3);
    frame.setSize(300, 200);
    frame.setVisible(true);
  }
}


/*
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
public class Main {
  public static void main(String args[]) {
    JFrame frame = new JFrame("DefaultButton");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Icon warnIcon = new ImageIcon("Warn.gif");
    JButton button3 = new JButton("Warning", warnIcon);
    frame.add(button3);
    frame.setSize(300, 200);
    frame.setVisible(true);
  }
}
*/
