package gjavaapi.bufferedreaderinputstreamreader

//http://www.java2s.com/Code/JavaAPI/java.io/newBufferedReaderInputStreamReaderinput.htm

import java.io._

class MainClass {

  def main(args:Array[String]) {
    try {
      var isr:InputStreamReader = new InputStreamReader(System.in);
//      var br:BufferedReader = new BufferedReader(isr); //r=1
      var br:BufferedReader =  /*!*/ // r=1

      var cond = false
      while (!cond) {

        System.out.print("Radius? ");

        var str:String = br.readLine();
        var radius:Double = 0.;
	try {
          var radius:Double = java.lang.Double.valueOf(str).doubleValue();
	  cond = true
        } catch{
	  case nfe =>
          System.out.println("Incorrect format!");
        }

	if(cond){
          if (radius <= 0) {
            System.out.println("Radius must be positive!");
            cond = false;
          }

	  if(cond){
            var area:Double = java.lang.Math.PI * radius * radius;
            System.out.println("Area is " + area);
	  }
	}
      }
    } catch {
      case e => e.printStackTrace();
    }
  }
}

/*
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainClass {

  public static void main(String args[]) {
    try {

      InputStreamReader isr = new InputStreamReader(System.in);
      BufferedReader br = new BufferedReader(isr);

      while (true) {

        System.out.print("Radius? ");

        String str = br.readLine();
        double radius;
        try {
          radius = Double.valueOf(str).doubleValue();
        } catch (NumberFormatException nfe) {
          System.out.println("Incorrect format!");
          continue;
        }

        if (radius <= 0) {
          System.out.println("Radius must be positive!");
          continue;
        }

        double area = Math.PI * radius * radius;
        System.out.println("Area is " + area);
        return;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
*/
