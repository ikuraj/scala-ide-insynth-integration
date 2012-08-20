package ch.epfl.insynth.core;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ch.epfl.insynth.Config;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ch.epfl.insynth.core"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	// handler for InSynth-specific (library) event logging
	private FileHandler inSynthHandler;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		// set shared instance
		plugin = this;

		// sets logger to the InSynth library
		String inSynthLibraryLoggerFilePath = getStateLocation().toOSString() +
		  java.io.File.separator + "insynth-library.log";
		//System.out.println("inSynthLibraryLoggerFilePath: " + inSynthLibraryLoggerFilePath);
		
		// create a file handler with appropriate path
		inSynthHandler = new FileHandler(inSynthLibraryLoggerFilePath, true);
		// set to log all levels
		inSynthHandler.setLevel(Level.ALL);
		// set simple text formatter
		inSynthHandler.setFormatter(new SimpleFormatter());
		
		//temporary until listener starts to work
		Config.setLoggerHandler(inSynthHandler);		
	}
	
	/**
	 * enable InSynth library (plugin-separate) logging
	 */
	public void enableInSynthLogging() {
		Config.setLoggerHandler(inSynthHandler);
	}

	/**
	 * disable InSynth library (plugin-separate) logging
	 */
	public void disableInSynthLogging() {
		Config.removeLoggerHandler(inSynthHandler);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
