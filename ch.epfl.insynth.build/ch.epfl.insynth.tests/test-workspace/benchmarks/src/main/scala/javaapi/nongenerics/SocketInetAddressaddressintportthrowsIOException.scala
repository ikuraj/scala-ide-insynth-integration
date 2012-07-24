package javaapi.SocketInetAddressaddressintportthrowsIOException

//http://www.java2s.com/Code/JavaAPI/java.net/newSocketInetAddressaddressintportthrowsIOException.htm

import java.net.InetAddress;
import java.net.Socket;

class Main {
  def main(args:Array[String]){
    var addr:InetAddress = InetAddress.getByName("java.sun.com"); // r=2
    var port:Int = 80;

    var socket:Socket = new Socket(addr, port);
  }
}

/*
import java.net.InetAddress;
import java.net.Socket;

public class Main {
  public static void main(String[] argv) throws Exception {
    InetAddress addr = InetAddress.getByName("java.sun.com");
    int port = 80;

    Socket socket = new Socket(addr, port);
  }
}
*/




