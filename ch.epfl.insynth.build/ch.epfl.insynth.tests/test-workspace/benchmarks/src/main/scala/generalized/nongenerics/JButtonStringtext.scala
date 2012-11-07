package gjavaapi.JButtonStringtext

//http://www.java2s.com/Code/JavaAPI/javax.swing/newJButtonStringtext.htm

import javax.swing._

class MainClass extends JPanel {

  def init() {
//    var btn1:JButton = new JButton("Button1"); //r>5
    var btn1:JButton =  /*!*/; //r>5
    var btn2:JButton = new JButton("Button2");
    var btn3:JButton = new JButton("Button3");
    var btn4:JButton = new JButton("Button4");
    var btn5:JButton = new JButton("Button5");
    var btn6:JButton = new JButton("Button6");

    add(btn1);
    add(btn2);
    add(btn3);
    add(btn4);
    add(btn5);
    add(btn6);

  }

  def main(args:Array[String]) {
    var frame:JFrame = new JFrame();
    frame.getContentPane().add(new MainClass());

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(200, 200);
    frame.setVisible(true);
  }
}


/*
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainClass extends JPanel {

  public MainClass() {
    JButton btn1 = new JButton("Button1");
    JButton btn2 = new JButton("Button2");
    JButton btn3 = new JButton("Button3");
    JButton btn4 = new JButton("Button4");
    JButton btn5 = new JButton("Button5");
    JButton btn6 = new JButton("Button6");

    add(btn1);
    add(btn2);
    add(btn3);
    add(btn4);
    add(btn5);
    add(btn6);

  }

  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.getContentPane().add(new MainClass());

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(200, 200);
    frame.setVisible(true);
  }
}
*/
