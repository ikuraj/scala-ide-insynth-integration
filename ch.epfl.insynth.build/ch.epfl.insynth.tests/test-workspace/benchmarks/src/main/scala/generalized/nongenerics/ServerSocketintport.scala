package gjavaapi.ServerSocketintport

//http://www.java2s.com/Code/JavaAPI/java.net/newServerSocketintport.htm

import java.io._
import java.net._

class MainClass {

  def main(args:Array[String]) {
    try {
      var port = 5555
      var ss:ServerSocket = new ServerSocket(port)//r=1

      while (true) {
        // Accept incoming requests
        var s:Socket = ss.accept()

        // Write result to client
        var os:OutputStream = s.getOutputStream()
        var dos:DataOutputStream = new DataOutputStream(os)
        dos.writeInt(100);

        s.close();
      }
    } catch {
      case e => System.out.println("Exception: " + e);
    }
  }
}

/*
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainClass {

  public static void main(String args[]) {
    try {

      int port = 5555;
      ServerSocket ss = new ServerSocket(port);

      while (true) {
        // Accept incoming requests
        Socket s = ss.accept();

        // Write result to client
        OutputStream os = s.getOutputStream();
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(100);

        s.close();
      }
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
  }
}
*/
