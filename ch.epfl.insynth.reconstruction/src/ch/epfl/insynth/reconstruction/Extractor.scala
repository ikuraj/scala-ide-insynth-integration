package ch.epfl.insynth.reconstruction

import scala.text.Document
import ch.epfl.insynth.combinator.NormalDeclaration
import ch.epfl.insynth.combinator.AbsDeclaration
import ch.epfl.insynth.reconstruction.trees._
import ch.epfl.insynth.print._
import ch.epfl.scala.{ trees => Scala }
import ch.epfl.insynth.combinator.NormalDeclaration
import ch.epfl.insynth.Config

/**
 * class that extract the needed amount of snippets from the intermediate representation
 */
// TODO so far it uses the same representation (to be compliant witht the current code-
// generation phase - change this - one snippet tree, one snippet)
object Extractor extends ((Node, Int) => List[(Node, Double)]) {
  
  val weightForLeaves = 1.5d
  
  type NodeWithWeight = (Node, Double)
  
  val logger = Config.logExtractor
  
  /**
   * apply method invokes combination of the intermediate representation tree into
   * a single-snippet trees along with their weights (as sum of all used nodes)
   * @param tree parameter tree to be worked on
   * @param numberOfCombinations number of combinations needed
   * @return numberOfCombinations snippets with lowest weight
   */
  def apply(tree:Node, numberOfCombinations: Int) = {
    if (Config.isLogging) {
      logger.entering(getClass.toString, "apply", FormatableIntermediate(tree))
    }
    
    val transformed = transform(tree)
    
    // transform the tree
    val result = transformed.sortWith
	  // sort it according to the weight value
	  { (nw1, nw2) => nw1._2 < nw2._2  } take
	  	// take only needed number
    	numberOfCombinations
    	    	
    if (Config.isLogging) {
      logger.fine("All transformed are: " +
        (transformed map { case (el, weight) => FormatableIntermediate(el) + "[" + weight + "]" }	mkString(" ", "\n", ""))
      )
      logger.exiting(getClass.toString, "apply", 
        ("" /: result) { (string, el) => string + "\n" + FormatableIntermediate(el._1) + "[" + el._2 + "]" }
	  )
    }
	  
	result
  }
  
  def transform(tree: Node): List[NodeWithWeight] = {
    // logging
    if (Config.isLogging) {
      logger.entering(getClass.toString, "transform", FormatableIntermediate(tree))
    }
    
   tree match {
      // variable (declared previously as an argument)
      case _:Variable =>
        // single pair with weight for leaves
        List( (tree, weightForLeaves) )
      // identifier defined in Scala program  
      case id:Identifier =>
        // single pair with weight from declaration
        List( (tree, id.decl.getWeight) )
      case NullLeaf => 
        List( (tree, 0d) )
      // apply parameters in the tail of params according to the head of params 
      case Application(tpe, params) => {
	    // logging
	    if (Config.isLogging && getSingleElementsParamsList(params).size < 1) {
	      logger.warning("getSingleElementsParamsList(params).size < 1")
	    }
        
        def getSingleElementsParamsList(params: List[Set[Node]]): List[(List[Node], Double)] = {
          params match {
            case List() => List[(List[Node], Double)]()
            case List(set) =>
              set flatMap { transform(_) map { pair => (List(pair._1), pair._2) } } toList
            case set :: list =>
              for (
    		    (firstParamNode, firstParamWeight) <- set flatMap { transform(_) } toList;
    		    (restParams, restOfWeight) <- getSingleElementsParamsList(list)
    		  ) yield ( (firstParamNode +: restParams, firstParamWeight + restOfWeight) )
          }
        }
        
        for (paramsList <- getSingleElementsParamsList(params))
          yield ( (Application(tpe, paramsList._1 map { Set(_) }), paramsList._2) )
      }
      // abstraction first creates all of its arguments
      case Abstraction(tpe, vars, subtrees) => {
        for ( (subtree, weight) <- subtrees flatMap { transform(_) } toList )
          yield ( (Abstraction(tpe, vars, Set(subtree)), weight) )
      }
    }
  }
  
}