package javaapi.DisplayModeintwidthintheightintbitDepthintrefreshRate

//http://www.java2s.com/Code/JavaAPI/java.awt/newDisplayModeintwidthintheightintbitDepthintrefreshRate.htm

 
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

class Main {
  def main(argv: Array[String]){
    var ge:GraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    var gs:GraphicsDevice = ge.getDefaultScreenDevice();

    var canChg = gs.isDisplayChangeSupported();
    if (canChg) {
      var displayMode:DisplayMode = gs.getDisplayMode();//r=1
      var displayMode:DisplayMode =  /*!*/;//r=1
      var screenWidth = 640;
      var screenHeight = 480;
      var bitDepth = 8;
      displayMode = new DisplayMode(screenWidth, screenHeight, bitDepth, displayMode
          .getRefreshRate());
      try {
        gs.setDisplayMode(displayMode);
      } catch {
	case e => gs.setFullScreenWindow(null);
      }
    }
  }
}


/*
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class Main {
  public static void main(String[] argv) throws Exception {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gs = ge.getDefaultScreenDevice();

    boolean canChg = gs.isDisplayChangeSupported();
    if (canChg) {
      DisplayMode displayMode = gs.getDisplayMode();
      int screenWidth = 640;
      int screenHeight = 480;
      int bitDepth = 8;
      displayMode = new DisplayMode(screenWidth, screenHeight, bitDepth, displayMode
          .getRefreshRate());
      try {
        gs.setDisplayMode(displayMode);
      } catch (Throwable e) {
        gs.setFullScreenWindow(null);
      }
    }
  }
}
*/
