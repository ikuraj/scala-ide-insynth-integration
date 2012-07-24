package javaapi.AWTPermissionStringname

//http://www.java2s.com/Code/JavaAPI/java.awt/newAWTPermissionStringname.htm

import java.awt.AWTPermission;
import java.security.AccessController;

class Main {
  def  main(args:Array[String]){
    var ap:AWTPermission = new AWTPermission("accessClipboard") //r=1
    AccessController.checkPermission(ap)
  }
}

/*
import java.awt.AWTPermission;
import java.security.AccessController;

public class Main {
  public static void main(String args[]) throws Exception {

    AWTPermission ap = new AWTPermission("accessClipboard");
    AccessController.checkPermission(ap);
  }
}
*/




