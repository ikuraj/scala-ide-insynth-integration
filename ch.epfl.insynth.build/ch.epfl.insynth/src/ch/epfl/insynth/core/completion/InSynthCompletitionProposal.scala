package ch.epfl.insynth.core.completion

import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal
import org.eclipse.jface.text.contentassist.{ ICompletionProposalExtension, ICompletionProposalExtension6, IContextInformation }
import org.eclipse.swt.graphics.Image
import org.eclipse.jface.text.IDocument
import org.eclipse.jface.viewers.{ISelectionProvider, StyledString}
import org.eclipse.jface.text.TextSelection
import org.eclipse.jface.text.ITextViewer
import org.eclipse.jdt.internal.ui.JavaPluginImages
import org.eclipse.jface.text.link._
import org.eclipse.jface.text.Position
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI
import org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal.ExitPolicy
import org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy
import org.eclipse.swt.events.VerifyEvent
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags
import org.eclipse.swt.SWT
import org.eclipse.jface.text.contentassist.ICompletionProposal

class InSynthCompletitionProposal (completition:String, relevance:Int)
extends IJavaCompletionProposal with ICompletionProposalExtension with ICompletionProposalExtension6
{
  def getRelevance = {
    1000 + relevance
  }
  
  def getImage = {
   null 
  }
    
  def getContextInformation(): IContextInformation ={ 
    null
  }
  def getDisplayString() = {
    completition
  }
  
  def getStyledDisplayString() = { 
    new StyledString(completition)
  }
  
  def getAdditionalProposalInfo() = {
    null
  }
  def getSelection(d: IDocument) = {
    null
  }
  def apply(d: IDocument) { 
    null
  }

  def apply(d: IDocument, trigger: Char, offset: Int){
    d.replace(offset, 0, completition)
  }
  
  def getTriggerCharacters = {
    null
  }
  def getContextInformationPosition = {
    1
  }
  
  def isValidFor(d: IDocument, pos: Int) = {
    true
  }

}