package ch.epfl.insynth.core.preferences

import scala.tools.eclipse.logging.Level
import scala.tools.eclipse.logging.LogManager
import scala.tools.eclipse.ui.OpenExternalFile
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer
import org.eclipse.jface.preference.BooleanFieldEditor
import org.eclipse.jface.preference.ComboFieldEditor
import org.eclipse.jface.preference.FieldEditorPreferencePage
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control
import org.eclipse.swt.widgets.Link
import org.eclipse.swt.SWT
import org.eclipse.ui.IWorkbench
import org.eclipse.ui.IWorkbenchPreferencePage
import org.eclipse.jface.preference.IntegerFieldEditor
import InSynthConstants._
import scala.tools.eclipse.logging.HasLogger
import org.eclipse.jface.preference.IPreferenceStore
import ch.epfl.insynth.core.Activator

class InSynthPreferences extends FieldEditorPreferencePage with IWorkbenchPreferencePage {

  setPreferenceStore(Activator.getDefault.getPreferenceStore)

  setDescription("Setting for the InSynth plugin.")
  
  override def createFieldEditors() {
    addField(new IntegerFieldEditor(OfferedSnippetsPropertyString, "Number of snippets", getFieldEditorParent))
    addField(new IntegerFieldEditor(MaximumTimePropertyString, "Maximum computation time (ms)", getFieldEditorParent))
  }

  override def createContents(parent: Composite): Control = {
    val control = super.createContents(parent)
    
    control
  }

  def init(workbench: IWorkbench) {}
    
}

class InSynthPreferencePageInitializer extends AbstractPreferenceInitializer {
    
  override def initializeDefaultPreferences() {    
    println("initializeDefaultPreferences")
    
    val store = Activator.getDefault.getPreferenceStore

    store.setDefault(OfferedSnippetsPropertyString, NumberOfOfferedSnippets)
    store.setDefault(MaximumTimePropertyString, MaximumTime)
  }
}