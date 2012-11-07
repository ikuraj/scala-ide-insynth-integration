package gjavaapi.GroupLayoutContainerhost

//http://www.java2s.com/Code/JavaAPI/javax.swing/newGroupLayoutContainerhost.htm
 
import javax.swing._

class Main {
  def main(args:Array[String]) {
    var frame:JFrame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    var panel = new JPanel();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
//    var layout:GroupLayout  = new GroupLayout(panel) //r=1
    var layout:GroupLayout =  /*!*/ //r=1

    panel.setLayout(layout);

    var buttonD:JButton  = new JButton("D");
    var buttonR:JButton  = new JButton("R");
    var buttonY:JButton = new JButton("Y");
    var buttonO:JButton  = new JButton("O");
    var buttonT:JButton  = new JButton("T");

    var leftToRight = layout.createSequentialGroup();

    leftToRight.addComponent(buttonD);
    var columnMiddle = layout.createParallelGroup();
    columnMiddle.addComponent(buttonR);
    columnMiddle.addComponent(buttonO);
    columnMiddle.addComponent(buttonT);
    leftToRight.addGroup(columnMiddle);
    leftToRight.addComponent(buttonY);

    var topToBottom = layout.createSequentialGroup();
    var rowTop = layout.createParallelGroup();
    rowTop.addComponent(buttonD);
    rowTop.addComponent(buttonR);
    rowTop.addComponent(buttonY);
    topToBottom.addGroup(rowTop);
    topToBottom.addComponent(buttonO);
    topToBottom.addComponent(buttonT);

    layout.setHorizontalGroup(leftToRight);
    layout.setVerticalGroup(topToBottom);

    frame.add(panel);
    frame.pack();
    frame.setVisible(true);
  }
}


/*
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {
  public static void main(String[] args) {
    JFrame frame = new JFrame();
    JPanel panel = new JPanel();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    GroupLayout layout = new GroupLayout(panel);
    panel.setLayout(layout);

    JButton buttonD = new JButton("D");
    JButton buttonR = new JButton("R");
    JButton buttonY = new JButton("Y");
    JButton buttonO = new JButton("O");
    JButton buttonT = new JButton("T");

    GroupLayout.SequentialGroup leftToRight = layout.createSequentialGroup();

    leftToRight.addComponent(buttonD);
    GroupLayout.ParallelGroup columnMiddle = layout.createParallelGroup();
    columnMiddle.addComponent(buttonR);
    columnMiddle.addComponent(buttonO);
    columnMiddle.addComponent(buttonT);
    leftToRight.addGroup(columnMiddle);
    leftToRight.addComponent(buttonY);

    GroupLayout.SequentialGroup topToBottom = layout.createSequentialGroup();
    GroupLayout.ParallelGroup rowTop = layout.createParallelGroup();
    rowTop.addComponent(buttonD);
    rowTop.addComponent(buttonR);
    rowTop.addComponent(buttonY);
    topToBottom.addGroup(rowTop);
    topToBottom.addComponent(buttonO);
    topToBottom.addComponent(buttonT);

    layout.setHorizontalGroup(leftToRight);
    layout.setVerticalGroup(topToBottom);

    frame.add(panel);
    frame.pack();
    frame.setVisible(true);
  }
}
*/
