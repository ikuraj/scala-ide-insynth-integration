package javaapi.FileStringname

//http://www.java2s.com/Code/JavaAPI/java.io/newFileStringname.htm

import java.io.File;

class MainClass {
  def main(args:Array[String]) {
    //var f1:File = new File("MainClass.java") //r=1
    var f1:File =  /*!*/ //r=1
    System.out.println("File Name:" + f1.getName());
    System.out.println("Path:" + f1.getPath());
    System.out.println("Abs Path:" + f1.getAbsolutePath());
    System.out.println("Parent:" + f1.getParent());
    System.out.println(if (f1.exists()) "exists" else "does not exist");
    System.out.println(if (f1.canWrite()) "is writeable" else "is not writeable");
    System.out.println(if (f1.canRead()) "is readable" else "is not readable");
    System.out.println("is a directory" + f1.isDirectory() );
    System.out.println(if (f1.isFile()) "is normal file" else "might be a named pipe");
    System.out.println(if (f1.isAbsolute()) "is absolute" else "is not absolute");
    System.out.println("File last modified:" + f1.lastModified());
    System.out.println("File size:" + f1.length() + " Bytes");
  }
}


/*
import java.io.File;

public class MainClass {
    public static void main(String args[]) {
  File f1 = new File("MainClass.java");
  System.out.println("File Name:" + f1.getName());
  System.out.println("Path:" + f1.getPath());
  System.out.println("Abs Path:" + f1.getAbsolutePath());
  System.out.println("Parent:" + f1.getParent());
  System.out.println(f1.exists() ? "exists" : "does not exist");
  System.out.println(f1.canWrite() ? "is writeable" : "is not writeable");
  System.out.println(f1.canRead() ? "is readable" : "is not readable");
  System.out.println("is a directory" + f1.isDirectory() );
  System.out.println(f1.isFile() ? "is normal file" : "might be a named pipe");
  System.out.println(f1.isAbsolute() ? "is absolute" : "is not absolute");
  System.out.println("File last modified:" + f1.lastModified());
  System.out.println("File size:" + f1.length() + " Bytes");
    }
}
*/
