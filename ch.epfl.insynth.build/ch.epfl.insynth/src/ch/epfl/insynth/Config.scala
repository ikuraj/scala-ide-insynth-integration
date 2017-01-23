package ch.epfl.insynth

import java.util.logging._

import java.io.{ FileWriter, BufferedWriter, PrintWriter, File }

object Config {
  
  final val getTimeOutSlot = 500
  
  val inSynthLogger = Logger.getLogger("insynth.library")
  inSynthLogger.setUseParentHandlers(false)
  inSynthLogger.setLevel(Level.ALL)
  
  // temporary hakc
  val handler = new java.util.logging.ConsoleHandler
  handler.setFormatter(new java.util.logging.SimpleFormatter)
  handler.setLevel(java.util.logging.Level.ALL)
  inSynthLogger.addHandler(handler)
  
  def setLoggerHandler(handler: Handler) {
    inSynthLogger.addHandler(handler)
  }
    
  def removeLoggerHandler(handler: Handler) {
    inSynthLogger.removeHandler(handler)
  }
  
  // variable declaring number of levels of proof trees to log
  private var _proofTreeLevelToLog = 1
  // getter and setter
  def proofTreeLevelToLog_=(lvl: Int) = _proofTreeLevelToLog = lvl  
  def proofTreeLevelToLog = _proofTreeLevelToLog
  
  // proof tree has to be written directly because it can be huge
  val proofTreeFileName = "proof_tree.xml"
  val proofTreeWritterBufferSize = 30 * 1024 * 1024
    
  lazy val proofTreeOutput = {    
//    val file = new File(proofTreeFileName)
//    if (file.exists) {
//      file.delete
//    }
    val writer = new FileWriter(proofTreeFileName)
    val bufferedWriter = new BufferedWriter(writer, proofTreeWritterBufferSize)
    new PrintWriter(bufferedWriter, false)
  }
}