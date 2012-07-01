package insynth

import java.util.logging._

object Config {
  // logging
    
  val outputFilename = "/home/ivcha/git/scala-ide-insynth-integration/InSynth_CompilerPlugin/out/output.txt"
  
  // defines loggers
  val logger = Logger.getLogger("insynth.plugin")
  val loggerAppAnalyzer = Logger.getLogger("insynth.plugin.ApplicationAnalyzer")
  val loggerAppInfo = Logger.getLogger("insynth.plugin.ApplicationArguments")
  val loggerReturnTypeAnalyzer = Logger.getLogger("insynth.plugin.ReturnTypeAnalyzer")
  
  
  
  val isLogging = true
  
  // static code for loggers setup
  {  
    val array = Array(logger, loggerAppInfo, loggerAppAnalyzer, loggerReturnTypeAnalyzer)
    
    // remove all handlers
    for (logger <- array)
    for (handler <- logger.getHandlers)
      logger.removeHandler(handler)
      
    logger.setLevel(Level.FINEST)
    loggerAppInfo.setLevel(Level.FINEST)
    loggerAppAnalyzer.setLevel(Level.FINEST)
    loggerReturnTypeAnalyzer.setLevel(Level.FINEST)
    
      
    val handler = new FileHandler("%h/insynthplugin%u.log");
    handler.setFormatter(new SimpleFormatter)
    // PUBLISH this level
    handler.setLevel(Level.FINEST);
    //loggerAppAnalyzer.addHandler(handler);
    logger.addHandler(handler);
  }
}