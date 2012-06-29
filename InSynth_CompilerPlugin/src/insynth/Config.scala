package insynth

import java.util.logging._

object Config {
  // logging
    
  val outputFilename = "/home/ivcha/development/workspace_gsoc/InSynth_CompilerPlugin/out/output.txt"
  
  // defines loggers
  val logger = Logger.getLogger("insynth.plugin")
  
  val isLogging = true
  
  // static code for loggers setup
  {  
    val array = Array(logger)
    
    // remove all handlers
    for (logger <- array)
    for (handler <- logger.getHandlers)
      logger.removeHandler(handler)
      
    logger.setLevel(Level.FINEST)
      
    val handler = new FileHandler("%h/insynthplugin%u.log");
    handler.setFormatter(new SimpleFormatter)
    // PUBLISH this level
    handler.setLevel(Level.FINEST);
    logger.addHandler(handler);
  }
}