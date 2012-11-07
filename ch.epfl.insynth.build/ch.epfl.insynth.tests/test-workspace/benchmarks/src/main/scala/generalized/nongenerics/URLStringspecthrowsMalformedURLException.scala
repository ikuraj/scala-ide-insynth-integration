package gjavaapi.URLStringspecthrowsMalformedURLException

//http://www.java2s.com/Code/JavaAPI/java.net/newURLStringspecthrowsMalformedURLException.htm

import java.io._
import java.net._

class Main {
  def main(args:Array[String]){
//    var conn:URLConnection = new URL("http://www.yourserver.com").openConnection() //r=1
    var conn:URLConnection =  /*!*/ //r=1
    conn.setDoInput(true);
    conn.setRequestProperty("Authorization", "asdfasdf");
    conn.connect();

    var in:InputStream = conn.getInputStream()
  }
}

/*
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Main {
  public static void main(String[] argv) throws Exception {
    
    URLConnection conn = new URL("http://www.yourserver.com").openConnection();
    conn.setDoInput(true);
    conn.setRequestProperty("Authorization", "asdfasdf");
    conn.connect();

    InputStream in = conn.getInputStream();
  }
}
*/
