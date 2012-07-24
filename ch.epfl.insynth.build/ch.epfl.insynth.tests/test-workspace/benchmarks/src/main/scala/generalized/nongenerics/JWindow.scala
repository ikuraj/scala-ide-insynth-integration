package gjavaapi.JWindow

//http://www.java2s.com/Code/JavaAPI/javax.swing/newJWindow.htm

import javax.swing._

class MainClass {

  def main(args:Array[String]) {
    var w:JWindow = new JWindow() //r=1
    w.setSize(300, 300);
    w.setLocation(500, 100);

    w.setVisible(true);
  }
}

/*
import javax.swing.JWindow;

public class MainClass {

  public static void main(String[] args) {
    JWindow w = new JWindow(  );
    w.setSize(300, 300);
    w.setLocation(500, 100);

    w.setVisible(true);
  }
}
*/
