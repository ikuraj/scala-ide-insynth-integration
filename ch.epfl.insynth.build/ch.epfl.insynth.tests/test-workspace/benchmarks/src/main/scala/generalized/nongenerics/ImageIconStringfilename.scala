package gjavaapi.ImageIconStringfilename

//http://www.java2s.com/Code/JavaAPI/javax.swing/newImageIconStringfilename.htm

import java.awt._

import javax.swing._

class Main {

  def main(args:Array[String]) {
    var imageIcon:ImageIcon = new ImageIcon("yourFile.gif") //r>5
    var image:Image = imageIcon.getImage()
  }

}


/*
import java.awt.Image;

import javax.swing.ImageIcon;

public class Main {

  public static void main(String[] a) {
    ImageIcon imageIcon = new ImageIcon("yourFile.gif");
    Image image = imageIcon.getImage();

  }

}
*/
