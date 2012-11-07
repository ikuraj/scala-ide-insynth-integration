package gjavaapi.GridBagConstraints

//http://www.java2s.com/Code/JavaAPI/java.awt/newGridBagConstraints.htm

import java.awt._
import javax.swing._
import javax.swing.border._

class MainClass {

  def main(args:Array[String]) {
    var aWindow:JFrame = new JFrame();
    aWindow.setBounds(200, 200, 200, 200);
    aWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    var content:Container = aWindow.getContentPane();
    content.add(new GridBagLayoutPanel());
    aWindow.setVisible(true);
  }
}

class GridBagLayoutPanel extends JPanel {

  def init(){
    var gridbag:GridBagLayout = new GridBagLayout()
//    var constraints:GridBagConstraints = new GridBagConstraints() //r=1
    var constraints:GridBagConstraints =  /*!*/ //r=1
    setLayout(gridbag);

    constraints.weightx =  10.0;
    constraints.weighty = 10.0
    constraints.fill = GridBagConstraints.NONE;
    constraints.ipadx = 30;
    constraints.ipady = 10;
    addButton("Press", constraints, gridbag);

    constraints.weightx = 5.0;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.ipadx = 0
    constraints.ipady = 0
    constraints.insets = new Insets(10, 30, 10, 20);
    constraints.gridwidth = GridBagConstraints.RELATIVE;
    constraints.gridheight = 2;
    addButton("GO", constraints, gridbag);

    constraints.insets = new Insets(0, 0, 0, 0);
    constraints.gridx = 0;
    constraints.fill = GridBagConstraints.NONE;
    constraints.ipadx = 30;
    constraints.ipady = 10;
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    addButton("Push", constraints, gridbag);

  }

  private def addButton(label:String, constraints:GridBagConstraints, layout:GridBagLayout) {

    var edge:Border = BorderFactory.createRaisedBevelBorder();

    var button:JButton = new JButton(label);
    button.setBorder(edge);
    layout.setConstraints(button, constraints);
    add(button);
  }
}


/*
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class MainClass {

  public static void main(String[] args) {
    JFrame aWindow = new JFrame();
    aWindow.setBounds(200, 200, 200, 200);
    aWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Container content = aWindow.getContentPane();
    content.add(new GridBagLayoutPanel());
    aWindow.setVisible(true);
  }
}

class GridBagLayoutPanel extends JPanel {

  public GridBagLayoutPanel() {
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints constraints = new GridBagConstraints();
    setLayout(gridbag);

    constraints.weightx = constraints.weighty = 10.0;
    constraints.fill = constraints.NONE;
    constraints.ipadx = 30;
    constraints.ipady = 10;
    addButton("Press", constraints, gridbag);

    constraints.weightx = 5.0;
    constraints.fill = constraints.BOTH;
    constraints.ipadx = constraints.ipady = 0;
    constraints.insets = new Insets(10, 30, 10, 20);
    constraints.gridwidth = constraints.RELATIVE;
    constraints.gridheight = 2;
    addButton("GO", constraints, gridbag);

    constraints.insets = new Insets(0, 0, 0, 0);
    constraints.gridx = 0;
    constraints.fill = constraints.NONE;
    constraints.ipadx = 30;
    constraints.ipady = 10;
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    addButton("Push", constraints, gridbag);

  }

  private void addButton(String label, GridBagConstraints constraints,
      GridBagLayout layout) {

    Border edge = BorderFactory.createRaisedBevelBorder();

    JButton button = new JButton(label);
    button.setBorder(edge);
    layout.setConstraints(button, constraints);
    add(button);
  }
}
*/
