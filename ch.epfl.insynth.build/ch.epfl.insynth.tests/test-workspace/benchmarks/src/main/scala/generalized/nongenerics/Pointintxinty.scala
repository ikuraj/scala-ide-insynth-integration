package gjavaapi.Pointintxinty

//http://www.java2s.com/Code/JavaAPI/java.awt/newPointintxinty.htm
 
import java.awt._

class MainClass {
  def main(args:Array[String]) {
    var aPoint:Point = new Point();
    //var bPoint:Point = new Point(50,25) //r>5
    var bPoint:Point =  /*!*/ //r>5
    var cPoint:Point = new Point(bPoint);
    
    System.out.println("cPoint is located at: " + cPoint);
    
    System.out.println("aPoint is located at: " + aPoint);
    aPoint.move(100, 50);

    bPoint.x = 110;
    bPoint.y = 70;

    aPoint.translate(10, 20);
    System.out.println("aPoint is now at: " + aPoint);

    if (aPoint.equals(bPoint))
      System.out.println("aPoint and bPoint are at the same location.");
  }
}


/*

import java.awt.Point;

public class MainClass {
  public static void main(String[] args) {
    Point aPoint = new Point();
    Point bPoint = new Point(50, 25);
    Point cPoint = new Point(bPoint);
    
    System.out.println("cPoint is located at: " + cPoint);
    
    System.out.println("aPoint is located at: " + aPoint);
    aPoint.move(100, 50);

    bPoint.x = 110;
    bPoint.y = 70;

    aPoint.translate(10, 20);
    System.out.println("aPoint is now at: " + aPoint);

    if (aPoint.equals(bPoint))
      System.out.println("aPoint and bPoint are at the same location.");
  }
}

*/
