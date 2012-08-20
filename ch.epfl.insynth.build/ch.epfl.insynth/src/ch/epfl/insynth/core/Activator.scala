package ch.epfl.insynth.core

// eclipse
import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.ui.plugin.AbstractUIPlugin
import org.osgi.framework.BundleContext

// InSynth
import ch.epfl.insynth.core.preferences.LogManager

/**
 * The activator class for the InSynth plugin, it controls the plug-in life cycle
 */
class Activator extends AbstractUIPlugin {
		
  /** This method is called upon plug-in activation. */
	override def start(context: BundleContext) {
		super.start(context);
		
		// set shared instance
		Activator.plugin = this;
		
		// configure InSynth log manager
		LogManager.configure
	}
	
  /** This method is called when the plug-in is stopped */
	override def stop(context: BundleContext) {
	  // unset the plugin variable
		Activator.plugin = null;
		
		super.stop(context);
	}

}

object Activator {
  
	// The plug-in ID
	final val PLUGIN_ID = "ch.epfl.insynth.core" //$NON-NLS-1$
	  
	// The shared instance (accessible with getDefault)
	var plugin: Activator	= null
  
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	def getImageDescriptor(path: String) = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path)
	
	/** Returns the shared instance */
	def getDefault = plugin
}
