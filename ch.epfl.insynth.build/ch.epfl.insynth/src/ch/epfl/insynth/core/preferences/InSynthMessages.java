package ch.epfl.insynth.core.preferences;

import org.eclipse.osgi.util.NLS;

public class InSynthMessages extends NLS {
  
	private static final String BUNDLE_NAME = "ch.epfl.insynth.core.preferences.messages"; //$NON-NLS-1$

	// set private constructor
	private InSynthMessages() {	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, InSynthMessages.class);
	}
  
	public static String InSynthPreferences_Description;
	public static String InSynthPreferences_OfferedSnippetsLabelText;
	public static String InSynthPreferences_MaximumTimeLabelText;
	public static String InSynthPreferences_DoSeparateLoggingLabelText;
	public static String InSynthPreferences_CodeStyleParenthesesLabelText;
	public static String InSynthPreferences_CodeStyleParentheses_CleanStyle_LabelText;
	public static String InSynthPreferences_CodeStyleParentheses_ClassicStyle_LabelText;
	
}