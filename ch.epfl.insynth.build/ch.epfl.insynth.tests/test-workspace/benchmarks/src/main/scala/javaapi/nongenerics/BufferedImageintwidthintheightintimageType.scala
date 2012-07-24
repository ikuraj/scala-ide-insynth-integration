package javaapi.BufferedImageintwidthintheightintimageType

//http://www.java2s.com/Code/JavaAPI/java.awt.image/newBufferedImageintwidthintheightintimageType.htm

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

class MainClass extends JPanel {

  override def paint(g:Graphics) {
    var img = createImage();
    g.drawImage(img, 20,20,this);
  }

  def main(args:Array[String]) {
    var frame:JFrame = new JFrame();
    frame.getContentPane().add(new MainClass());

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(200, 200);
    frame.setVisible(true);
  }
  
  def createImage():Image = {
    var bufferedImage:BufferedImage = new BufferedImage(200,200,BufferedImage.TYPE_INT_RGB) //r>5
    var g:Graphics = bufferedImage.getGraphics();
    g.drawString("www.java2s.com", 20,20);
    
    return bufferedImage;
  }
}




/*
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainClass extends JPanel {

  public void paint(Graphics g) {
    Image img = createImage();
    g.drawImage(img, 20,20,this);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.getContentPane().add(new MainClass());

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(200, 200);
    frame.setVisible(true);
  }
  
  private Image createImage(){
    BufferedImage bufferedImage = new BufferedImage(200,200,BufferedImage.TYPE_INT_RGB);
    Graphics g = bufferedImage.getGraphics();
    g.drawString("www.java2s.com", 20,20);
    
    return bufferedImage;
  }
}
*/
