package ch.epfl.insynth.core.preferences

// InSynth
import ch.epfl.insynth.core.Activator

// java logging
import java.util.logging._

// eclipse
import org.eclipse.jface.util.PropertyChangeEvent

// Scala IDE
import scala.tools.eclipse.logging.HasLogger
import scala.tools.eclipse.util.SWTUtils

/**
 * manager for InSynth-specific (library) event logging
 */
object LogManager extends HasLogger {
    
	// sets logger to the InSynth library
	val inSynthLibraryLoggerFilePath = Activator.getDefault.getStateLocation.toOSString +
	  java.io.File.separator + "insynth-library.log";
	// log to Scala IDE log
	eclipseLog.info("InSynth library logger configured to file path: " + inSynthLibraryLoggerFilePath);
	
	// create a file handler with appropriate path (no appending)
	val inSynthHandler = new FileHandler(inSynthLibraryLoggerFilePath, false)
	// this causes issues on jenkins
	//val inSynthHandler = new FileHandler(inSynthLibraryLoggerFilePath, LogFileMaxSize, NumberOfLogFiles, true);

	inSynthHandler.setLevel(Level.INFO);
	// set simple text formatter
	inSynthHandler.setFormatter(new SimpleFormatter);
	
	
	/**
	 * method to be used as a listener
	 * @param event
	 */
	private def updatedLogging(event: PropertyChangeEvent): Unit = {
	  // import preferences constants
	  import InSynthConstants._
	  
	  // check property of the event
    if (event.getProperty == DoSeparateLoggingPropertyString) {
		  // get new value as boolean
	    val enable = event.getNewValue.asInstanceOf[Boolean]
	    // set logging accordingly
	    setLogging(enable)
    }
	}
	
  /**
   * configure InSynth logging facility
   */
  def configure = {    
    // import listener transformations
    import SWTUtils.fnToPropertyChangeListener
    // get plugin store
    val store = Activator.getDefault.getPreferenceStore
    // add property change listener
    store.addPropertyChangeListener(updatedLogging _)
    
	  // import preferences constants
	  import InSynthConstants._
	  // check current setting and set logging
    setLogging(store.getBoolean(DoSeparateLoggingPropertyString))
  }
  
  /** method for setting logging (on/off) */
  def setLogging(enable: Boolean) = 
		if (enable) {
      // set logger handler
//    	Config.setLoggerHandler(inSynthHandler);
			// log to Scala IDE log
			eclipseLog.info("InSynth library logger enabled.");
    }
    else {
      // remove logger handler
//    	Config.removeLoggerHandler(inSynthHandler);
    	// log to Scala IDE log
			eclipseLog.info("InSynth library logger disabled.");
    }
      
  // log file size in bytes
  val LogFileMaxSize = 5 * 1024 * 1024 
  val NumberOfLogFiles = 1
  
}