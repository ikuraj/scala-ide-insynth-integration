package ch.epfl.insynth.combinator

import ch.epfl.insynth.{env => InSynth}

abstract class Context(associatedExpr: Expression) {
	def evaluate: Double
	
	def isPruned: Boolean
	
	def isExplorable(decl: InSynth.Declaration): Boolean
}

class NameContext(name: String, parent: NameContext, expr: Expression)
  extends Context(expr) {		
	val nameFound: Boolean = parent.isFavored
	
	def isFavored = nameFound
  
	override def evaluate: Double = expr.getTraversalWeight
	
	def isPruned: Boolean = expr.isPruned
	
	def isExplorable(decl: InSynth.Declaration) =
	  // check if the weight is okay
	  expr.getAssociatedTree.checkIfPruned(expr.getTraversalWeight + decl.getWeight.getValue)
}

trait Bla {
  var shit: Boolean
  def setShit(s: Boolean) = shit = s
}