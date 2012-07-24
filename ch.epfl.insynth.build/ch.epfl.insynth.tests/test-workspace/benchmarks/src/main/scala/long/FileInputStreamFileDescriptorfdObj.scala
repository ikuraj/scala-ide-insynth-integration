package long.javaapi.FileInputStreamFileDescriptorfdObj

//http://www.java2s.com/Code/JavaAPI/java.io/newFileInputStreamFileDescriptorfdObj.htm

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
class Main {
  def main(argv: Array[String]) {
    var inputFile1:FileInputStream = null; 
    var fd:FileDescriptor = null 
    try {
      var inputFile2:FileInputStream = new FileInputStream(new File("?")) //r=3
      inputFile1 = inputFile2
      fd = inputFile1.getFD(); 
    } catch {
      case e =>
	e.printStackTrace(System.err);
	System.exit(1);
    }
    var inputFile2:FileInputStream = new FileInputStream(fd);
  }
}

/*
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
public class Main {
  public static void main(String[] a) {
    File aFile = new File("C:/myFile.text");
    FileInputStream inputFile1 = null; 
    FileDescriptor fd = null; 
    try {
      inputFile1 = new FileInputStream(aFile);
      fd = inputFile1.getFD(); 
    } catch (IOException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
    FileInputStream inputFile2 = new FileInputStream(fd);
  }
}
*/
