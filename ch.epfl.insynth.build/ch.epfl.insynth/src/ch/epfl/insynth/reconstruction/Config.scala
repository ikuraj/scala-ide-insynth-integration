package ch.epfl.insynth.reconstruction

import java.util.logging._
import ch.epfl.insynth.core.preferences.LogManager

object Config {  
  // flag which marks whether logging is enabled  
  val isLogging = false
  // default weight for leave nodes (used in extraction phase)
  val weightForLeaves = 1.5d
  
  // logging for code generation phase
  val logReconstructor = Logger.getLogger("reconstruction.reconstructor")
  // log of found solutions
  val logSolutions = Logger.getLogger("reconstruction.solutions")
  
  // combinator loggers
  val loggerCombinator = Logger.getLogger("reconstruction.combination")  
  val logStructures = Logger.getLogger("reconstruction.combination.structures")
  val logApply = Logger.getLogger("reconstruction.combination.apply")  
  val logPQAdding = Logger.getLogger("reconstruction.combination.apply.pqadding")
  // log of input proof tree level
  val logCombinatorInputProofTreeLevel = 6  
  // extractor logging
  val logExtractor = Logger.getLogger("reconstruction.extractor")
  // intermediate transformer logging
  val logIntermediate = Logger.getLogger("reconstruction.intermediate")
  
  // static code for loggers setup  
  val array = Array(loggerCombinator, logStructures, logApply,
    logExtractor, logPQAdding, logIntermediate, logReconstructor, logSolutions)
  
  // root logger of the reconstruction phase should not use parent handlers
  Logger.getLogger("reconstruction").setUseParentHandlers(false)
    
  // set level for all loggers
  for (logger <- array) {
  	logger.setLevel(Level.INFO)      
  }
  // add InSynth handler to loggers
  logReconstructor.addHandler(LogManager.inSynthHandler)
  
  logExtractor.setLevel(Level.ALL)
  logExtractor.addHandler(LogManager.inSynthHandler)
  
//  loggerCombinator.setLevel(Level.INFO)
//  loggerCombinator.addHandler(LogManager.inSynthHandler)
   
//    Logger.getLogger("reconstruction.combination").setLevel(Level.FINEST)
//    Logger.getLogger("reconstruction.combination.apply").setLevel(Level.FINEST)
//    Logger.getLogger("reconstruction.combination.structures").setLevel(Level.FINEST)
//    logPQAdding.setLevel(Level.FINEST)
//    logReconstructor.setLevel(Level.FINEST)
//    
//    logExtractor.setLevel(Level.FINEST)
//      
//    val handler = new FileHandler("%h/combinator%u.log");
//    val handlerEx = new FileHandler("%h/extractor%u.log");
//    val handlerInt = new FileHandler("%h/intermediate%u.log");
//    val handlerSol = new FileHandler("%h/solutions%u.log");
//    handler.setFormatter(new SimpleFormatter)
//    handlerEx.setFormatter(new SimpleFormatter)
//    handlerInt.setFormatter(new SimpleFormatter)
//    handlerSol.setFormatter(new SimpleFormatter)
//    // PUBLISH this level
//    handler.setLevel(Level.FINEST);
//    handlerSol.setLevel(Level.FINEST)
//    //logger.addHandler(handler);
//    //logStructures.addHandler(handler);
//    logExtractor.addHandler(handlerEx)
    //logReconstructor.addHandler(handlerInt)
    //logSolutions.addHandler(handlerSol)
//    Logger.getLogger("reconstruction.combination.apply").addHandler(handler);
//    Logger.getLogger("reconstruction.combination.structures").addHandler(handler);
  
}