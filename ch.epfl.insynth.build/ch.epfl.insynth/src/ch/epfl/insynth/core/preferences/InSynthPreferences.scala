package ch.epfl.insynth.core.preferences

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
import org.eclipse.jface.preference.IPreferenceStore
import ch.epfl.insynth.core.Activator
import InSynthConstants._
import org.eclipse.jface.util.IPropertyChangeListener
import org.eclipse.jface.util.PropertyChangeEvent
import java.lang.Boolean
import scala.tools.eclipse.logging.HasLogger
import org.eclipse.jface.preference.RadioGroupFieldEditor

class InSynthPreferences extends FieldEditorPreferencePage with IWorkbenchPreferencePage {

  setPreferenceStore(Activator.getDefault.getPreferenceStore)

  setDescription("Setting for the InSynth plugin.")
  
  override def createFieldEditors() {
    addField(new IntegerFieldEditor(OfferedSnippetsPropertyString, "Number of snippets", getFieldEditorParent))
    addField(new IntegerFieldEditor(MaximumTimePropertyString, "Maximum computation time (ms)", getFieldEditorParent))
    
    val doLoggingFieldEditor = new BooleanFieldEditor(DoSeparateLoggingPropertyString, "Log InSynth-specific events to a separate log", getFieldEditorParent)
    //doLoggingFieldEditor.setPropertyChangeListener(DoLoggingChangeListener)
    addField(doLoggingFieldEditor)
    
    // add radio buttons to choose code style
    val codeStyleFieldEditor = new RadioGroupFieldEditor(
  		CodeStyleParenthesesPropertyString, "Snippets code style for parentheses output", 2,
      Array[Array[String]](
      		Array[String]( "Clean style", CodeStyleParenthesesClean ),
      		Array[String]( "Classic style", CodeStyleParenthesesClassic )
      ),
      getFieldEditorParent()
    )
    // TODO why this thing does not work?
//    val labelControl = codeStyleFieldEditor.getLabelControl( getFieldEditorParent() )
//    labelControl.setToolTipText("Choose style of generated Scala code");
    addField(codeStyleFieldEditor)
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
    store.setDefault(DoSeparateLoggingPropertyString, DoSeparateLogging)
    store.setDefault(CodeStyleParenthesesPropertyString, CodeStyleParenthesesClean)
    store.setDefault(CodeStyleApplyOmittingPropertyString, CodeStyleApplyOmittingPropertyDefault)
    store.setDefault(CodeStyleSimpleApplicationNameTransformPropertyString, CodeStyleSimpleApplicationNameTransformPropertyDefault)
  }
}