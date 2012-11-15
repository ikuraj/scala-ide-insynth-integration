package ch.epfl.insynth.reconstruction.codegen

import ch.epfl.insynth.reconstruction.intermediate._
import ch.epfl.insynth.print._
import ch.epfl.insynth.reconstruction.Config
import java.util.concurrent.TimeoutException

/**
 * class that extract the needed amount of snippets from the intermediate representation
 */
// TODO so far it uses the same representation (to be compliant witht the current code-
// generation phase - change this - one snippet tree, one snippet)
object Extractor extends ((Node, Int) => List[(Node, Double)]) {
  
  // introduce a pair to work with
  type NodeWithWeight = (Node, Double)
  // set default weights for leaf nodes
  val weightForLeaves = Config.weightForLeaves
  // declare logger
  val logger = Config.logExtractor
  // caching already extracted trees to improve performance
  var cache = new scala.collection.mutable.HashMap[Node, List[NodeWithWeight]]
  
  var startTime: Long = _

  /**
   * apply method invokes combination of the intermediate representation tree into
   * a single-snippet trees along with their weights (as sum of all used nodes)
   * @param tree parameter tree to be worked on
   * @param numberOfCombinations number of combinations needed
   * @return numberOfCombinations snippets with lowest weight
   */
  def apply(tree: Node, numberOfCombinations: Int): List[NodeWithWeight] = {
    // logging
    if (Config.isLogging) {
      logger.entering(getClass.toString, "apply", Array[Object](FormatableIntermediate(tree, 100), numberOfCombinations: java.lang.Integer))
    }
    // initialize new cache instance
    cache = new scala.collection.mutable.HashMap[Node, List[NodeWithWeight]]
		// do the transformation
    
    startTime = System.currentTimeMillis
    
    val transformed = 
      try {
        transform(tree)
      } catch {
        case _ => Nil
      }
    // logging
    logger.fine("transform call done")

    // transform the tree
    val result = transformed.sortWith // sort it according to the weight value
    { (nw1, nw2) => nw1._2 < nw2._2 } take
      // take only needed number
      numberOfCombinations

    // logging
    if (Config.isLogging) {
      logger.fine("All transformed are: " +
        (transformed map { case (el, weight) => FormatableIntermediate(el) + "[" + weight + "]" } mkString (" ", "\n", "")))
      logger.exiting(getClass.toString, "apply",
        ("" /: result) { (string, el) => string + "\n" + FormatableIntermediate(el._1) + "[" + el._2 + "]" })
    }

    result
  }

  /**
   * recursive function for transforming a tree into a list of trees with corresponding weights
   * @param tree input tree parameter
   * @return list of tree, weight pairs
   */
  def transform(tree: Node): List[NodeWithWeight] = {
    if (System.currentTimeMillis - startTime > 3000)
      throw new TimeoutException

    // logging
    if (Config.isLogging) {
      logger.entering(getClass.toString, "transform" /*, FormatableIntermediate(tree)*/ )
    }

    // check if the result is cached, return immediately if it is
    if (cache contains tree) {
      // logging
      if (Config.isLogging)
        logger.fine("cache contains tree: " + FormatableIntermediate(tree, 1))
      return cache(tree)
    } else {
      // logging
      if (Config.isLogging)
        logger.fine("cache did not contain tree")
    }

    val result = tree match {
      // variable (declared previously as an argument)
      case _: Variable =>
        // single pair with weight for leaves
        List((tree, weightForLeaves))
      // identifier defined in Scala program  
      case id: Identifier =>
        // single pair with weight from declaration
        List((tree, id.decl.getWeight))
      case NullLeaf =>
        List((tree, 0d))
      // apply parameters in the tail of params according to the head of params 
      case Application(tpe, params) => {
        // logging
        //		    if (Config.isLogging && getSingleElementsParamsList(params).size < 1) {
        //		      logger.warning("getSingleElementsParamsList(params).size < 1")
        //		    }

        /**
         * for a parameters list (in terms of list of sets of nodes), return multiple parameters
         *  lists in terms of parameter list (but with single node for each parameter) and the sum
         *  weight of such list of parameters
         */
        def getSingleElementsParamsList(params: List[Set[Node]]): List[(List[Node], Double)] = {
          if (System.currentTimeMillis - startTime > 3000)
            throw new TimeoutException
          //logger.entering(getClass.toString, "getSingleElementsParamsList"/*, FormatableIntermediate(tree)*/)
          params match {
            // return empty list if no parameters are found
            case List() => Nil
            // a single parameter (set)
            case List(set) =>
              // recursively transform a single parameter set and map each result into a single-parameter
              // list and its weight
              set flatMap { transform(_) map { pair => (List(pair._1), pair._2) } } toList
            // separate a single set from the rest of the parameter list
            case set :: list =>
              // calculate the recursive result (no need to do that in the for loop)
              val resultForRest = getSingleElementsParamsList(list);
              if (System.currentTimeMillis - startTime > 3000)
                throw new TimeoutException
              for (
                // for a set of parameter get all possible single-snippet trees
                (firstParamNode, firstParamWeight) <- set flatMap { transform(_) } toList;
                // for all single-snippet trees for rest of the list recursively
                (restParams, restOfWeight) <- resultForRest
              // yield all possible combinations of concatenations of the two lists
              ) yield ((firstParamNode +: restParams, firstParamWeight + restOfWeight))
          }
        }

        if (System.currentTimeMillis - startTime > 3000)
          throw new TimeoutException
        // for all single-snippet trees representing parameters list
        for (paramsList <- getSingleElementsParamsList(params))
          // yield an appropriate application node
          yield ((Application(tpe, paramsList._1 map { Set(_) }), paramsList._2))
      }
      // abstraction first creates all of its arguments
      case Abstraction(tpe, vars, subtrees) => {
        for ((subtree, weight) <- subtrees flatMap { transform(_) } toList)
          yield ((Abstraction(tpe, vars, Set(subtree)), weight))
      }
    }

    // cache the result
    cache += tree -> result
    // logging
    if (Config.isLogging) {
      logger.exiting(getClass.toString, "transform" /*, FormatableIntermediate(tree)*/ )
    }

    result
  }
  
}