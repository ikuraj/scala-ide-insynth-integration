package ch.epfl.insynth.core.preferences

object InSynthConstants {
  
  private final val Prefix = "ch.epfl.insynth.properties." //$NON-NLS-1$
  final val OfferedSnippetsPropertyString = Prefix + "snippets" //$NON-NLS-1$
  final val MaximumTimePropertyString = Prefix + "maximumTime" //$NON-NLS-1$
  final val DoSeparateLoggingPropertyString = Prefix + "doInSynthLogging" //$NON-NLS-1$
  final val CodeStyleParenthesesPropertyString = Prefix + "codeStyleParentheses" //$NON-NLS-1$
  final val CodeStyleApplyOmittingPropertyString = Prefix + "codeStyleApply" //$NON-NLS-1$
  final val CodeStyleSimpleApplicationNameTransformPropertyString = Prefix + "codeStyleSimpleApplicationNameTransform" //$NON-NLS-1$    
    
  final val CodeStyleParenthesesClean = "clean" //$NON-NLS-1$
  final val CodeStyleParenthesesClassic = "classic" //$NON-NLS-1$
  
  final val NumberOfOfferedSnippets = 5   
  final val MaximumTime = 500
  final val DoSeparateLogging = false
  final val CodeStyleParenthesesDefault = CodeStyleParenthesesClassic
  final val CodeStyleApplyOmittingPropertyDefault = true
  final val CodeStyleSimpleApplicationNameTransformPropertyDefault = true

}