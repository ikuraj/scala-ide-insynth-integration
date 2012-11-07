package gjavaapi.DefaultBoundedRangeModel

//http://www.java2s.com/Code/JavaAPI/javax.swing/newDefaultBoundedRangeModel.htm

import javax.swing._
import javax.swing.event._

class MainClass {
  def main(args:Array[String]) {
    try {
//      var model:DefaultBoundedRangeModel = new DefaultBoundedRangeModel() //r=1
      var model:DefaultBoundedRangeModel =  /*!*/ //r=1
      var myListener:ChangeListener  = new MyChangeListener();
      model.addChangeListener(myListener);

      System.out.println(model.toString());
      System.out.println("Now setting minimum to 50 . . .");
      model.setMinimum(50);
      System.out.println(model.toString());
      System.out.println("Now setting maximum to 40 . . .");
      model.setMaximum(40);
      System.out.println(model.toString());
      System.out.println("Now setting maximum to 50 . . .");
      model.setMaximum(50);
      System.out.println(model.toString());
      System.out.println("Now setting extent to 30 . . .");
      model.setExtent(30);
      System.out.println(model.toString());

      System.out.println("Now setting several properties . . .");
      if (!model.getValueIsAdjusting()) {
        model.setValueIsAdjusting(true);
        System.out.println(model.toString());
        model.setMinimum(0);
        model.setMaximum(100);
        model.setExtent(20);
        model.setValueIsAdjusting(false);
      }
      System.out.println(model.toString());
    } catch {
      case e => e.printStackTrace();
    }
  }
}

class MyChangeListener extends ChangeListener {
  def stateChanged(e:ChangeEvent) {
    System.out.println("A ChangeEvent has been fired!");
  }
}



/*
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainClass {
  public static void main(String args[]) {
    try {
      DefaultBoundedRangeModel model = new DefaultBoundedRangeModel();
      ChangeListener myListener = new MyChangeListener();
      model.addChangeListener(myListener);

      System.out.println(model.toString());
      System.out.println("Now setting minimum to 50 . . .");
      model.setMinimum(50);
      System.out.println(model.toString());
      System.out.println("Now setting maximum to 40 . . .");
      model.setMaximum(40);
      System.out.println(model.toString());
      System.out.println("Now setting maximum to 50 . . .");
      model.setMaximum(50);
      System.out.println(model.toString());
      System.out.println("Now setting extent to 30 . . .");
      model.setExtent(30);
      System.out.println(model.toString());

      System.out.println("Now setting several properties . . .");
      if (!model.getValueIsAdjusting()) {
        model.setValueIsAdjusting(true);
        System.out.println(model.toString());
        model.setMinimum(0);
        model.setMaximum(100);
        model.setExtent(20);
        model.setValueIsAdjusting(false);
      }
      System.out.println(model.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

class MyChangeListener implements ChangeListener {
  public void stateChanged(ChangeEvent e) {
    System.out.println("A ChangeEvent has been fired!");
  }
}
*/
