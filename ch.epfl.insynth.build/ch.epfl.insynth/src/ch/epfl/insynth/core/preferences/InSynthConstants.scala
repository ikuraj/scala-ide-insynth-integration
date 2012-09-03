package ch.epfl.insynth.core.preferences

object InSynthConstants {
  
  private final val Prefix = "ch.epfl.insynth.properties."
  final val OfferedSnippetsPropertyString = Prefix + "snippets"  
  final val MaximumTimePropertyString = Prefix + "maximumTime"  
  final val DoSeparateLoggingPropertyString = Prefix + "doInSynthLogging"
  final val CodeStyleParenthesesPropertyString = Prefix + "codeStyleParentheses" 
  final val CodeStyleApplyOmittingPropertyString = Prefix + "codeStyleApply"    
    
  final val CodeStyleParenthesesClean = "clean"
  final val CodeStyleParenthesesClassic = "classic"
  
  final val NumberOfOfferedSnippets = 5   
  final val MaximumTime = 500
  final val DoSeparateLogging = false
  final val CodeStyleParenthesesDefault = CodeStyleParenthesesClean
  final val CodeStyleApplyOmittingPropertyDefault = false

}