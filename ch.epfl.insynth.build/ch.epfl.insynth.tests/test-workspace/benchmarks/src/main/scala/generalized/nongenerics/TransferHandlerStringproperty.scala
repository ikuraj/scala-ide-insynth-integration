package gjavaapi.TransferHandlerStringproperty

//http://www.java2s.com/Code/JavaAPI/javax.swing/newTransferHandlerStringproperty.htm
 
import java.awt._
import java.awt.event._

import javax.swing._
import javax.swing.event._

class MainClass extends JFrame {
  def main(args:Array[String]) {
    new MainClass().setVisible(true);
  }

  private var tf:JTextField = null;

  private var l:JLabel = null;

  private var propertyComboBox:JComboBox = null;

  def init() {
    var cp = new Box(BoxLayout.X_AXIS);
    setContentPane(cp);
    var firstPanel = new JPanel();
    propertyComboBox = new JComboBox();
    propertyComboBox.addItem("text");
    propertyComboBox.addItem("font");
    propertyComboBox.addItem("background");
    propertyComboBox.addItem("foreground");
    firstPanel.add(propertyComboBox);
    cp.add(firstPanel);
    cp.add(Box.createGlue());

    tf = new JTextField("Hello");
    tf.setForeground(Color.RED);
    tf.setDragEnabled(true);
    cp.add(tf);

    cp.add(Box.createGlue());

    l = new JLabel("Hello");
    l.setBackground(Color.YELLOW);
    cp.add(l);

    cp.add(Box.createGlue());

    var stryder = new JSlider(SwingConstants.VERTICAL);
    stryder.setMinimum(10);
    stryder.setValue(14);
    stryder.setMaximum(72);
    stryder.setMajorTickSpacing(10);
    stryder.setPaintTicks(true);

    cp.add(stryder);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(500, 300);

    setMyTransferHandlers(propertyComboBox.getSelectedItem().asInstanceOf[String]);

    var myDragListener = new MouseAdapter() {
      override def mousePressed(e:MouseEvent) {
        var c = e.getSource().asInstanceOf[JComponent];
        var handler = c.getTransferHandler();
        handler.exportAsDrag(c, e, TransferHandler.COPY);
      }
    };
    l.addMouseListener(myDragListener);

    propertyComboBox.addActionListener(new ActionListener() {
      override def actionPerformed(ce:ActionEvent) {
        var bx =  ce.getSource().asInstanceOf[JComboBox];
        var prop = bx.getSelectedItem().asInstanceOf[String];
        setMyTransferHandlers(prop);
      }
    });

    tf.addActionListener(new ActionListener() {
      override def actionPerformed(evt:ActionEvent) {
        var jtf = evt.getSource().asInstanceOf[JTextField];
        var fontName = jtf.getText();
        var font = new Font(fontName, Font.BOLD, 18);
        tf.setFont(font);
      }
    });

    stryder.addChangeListener(new ChangeListener() {
      override def stateChanged(evt:ChangeEvent) {
        var sl = evt.getSource().asInstanceOf[JSlider];
        var oldf = tf.getFont();
        var newf = oldf.deriveFont(sl.getValue().asInstanceOf[Float]);
        tf.setFont(newf);
      }
    });
  }

  private def setMyTransferHandlers(s:String) {
    var th:TransferHandler = new TransferHandler(s) //r>5
    tf.setTransferHandler(th);
    l.setTransferHandler(th);
  }
}

/*
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainClass extends JFrame {
  public static void main(String[] args) {
    new MainClass().setVisible(true);
  }

  private JTextField tf;

  private JLabel l;

  private JComboBox propertyComboBox;

  public MainClass() {
    Container cp = new Box(BoxLayout.X_AXIS);
    setContentPane(cp);
    JPanel firstPanel = new JPanel();
    propertyComboBox = new JComboBox();
    propertyComboBox.addItem("text");
    propertyComboBox.addItem("font");
    propertyComboBox.addItem("background");
    propertyComboBox.addItem("foreground");
    firstPanel.add(propertyComboBox);
    cp.add(firstPanel);
    cp.add(Box.createGlue());

    tf = new JTextField("Hello");
    tf.setForeground(Color.RED);
    tf.setDragEnabled(true);
    cp.add(tf);

    cp.add(Box.createGlue());

    l = new JLabel("Hello");
    l.setBackground(Color.YELLOW);
    cp.add(l);

    cp.add(Box.createGlue());

    JSlider stryder = new JSlider(SwingConstants.VERTICAL);
    stryder.setMinimum(10);
    stryder.setValue(14);
    stryder.setMaximum(72);
    stryder.setMajorTickSpacing(10);
    stryder.setPaintTicks(true);

    cp.add(stryder);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(500, 300);

    setMyTransferHandlers((String) propertyComboBox.getSelectedItem());

    MouseListener myDragListener = new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        JComponent c = (JComponent) e.getSource();
        TransferHandler handler = c.getTransferHandler();
        handler.exportAsDrag(c, e, TransferHandler.COPY);
      }
    };
    l.addMouseListener(myDragListener);

    propertyComboBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ce) {
        JComboBox bx = (JComboBox) ce.getSource();
        String prop = (String) bx.getSelectedItem();
        setMyTransferHandlers(prop);
      }
    });

    tf.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        JTextField jtf = (JTextField) evt.getSource();
        String fontName = jtf.getText();
        Font font = new Font(fontName, Font.BOLD, 18);
        tf.setFont(font);
      }
    });

    stryder.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
        JSlider sl = (JSlider) evt.getSource();
        Font oldf = tf.getFont();
        Font newf = oldf.deriveFont((float) sl.getValue());
        tf.setFont(newf);
      }
    });
  }

  private void setMyTransferHandlers(String s) {
    TransferHandler th = new TransferHandler(s);
    tf.setTransferHandler(th);
    l.setTransferHandler(th);
  }
}
*/
