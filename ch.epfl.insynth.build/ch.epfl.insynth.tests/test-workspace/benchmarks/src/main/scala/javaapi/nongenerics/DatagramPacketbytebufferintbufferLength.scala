package javaapi.DatagramPacketbytebufferintbufferLength

//http://www.java2s.com/Code/JavaAPI/java.net/newDatagramPacketbytebufferintbufferLength.htm

import java.net.DatagramPacket;
import java.net.DatagramSocket;

class MainClass {
  private final val BUFSIZE:Int = 20;

  def main(args:Array[String]) {
    try {
      var port:Int = 80;
      var ds:DatagramSocket = new DatagramSocket(port)

      while (true) {
        var buffer = new Array[Byte](BUFSIZE)

        //var dp:DatagramPacket = new DatagramPacket(buffer, buffer.length) //r>5
        var dp:DatagramPacket =  /*!*/ //r>5

        ds.receive(dp);

        var str:String = new String(dp.getData());

        System.out.println(str);
      }
    } catch {
      case e => e.printStackTrace();
    }
  }
}



/*
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MainClass {
  private final static int BUFSIZE = 20;

  public static void main(String args[]) {
    try {

      int port = 80;

      DatagramSocket ds = new DatagramSocket(port);

      while (true) {
        byte buffer[] = new byte[BUFSIZE];

        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

        ds.receive(dp);

        String str = new String(dp.getData());

        System.out.println(str);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
*/
