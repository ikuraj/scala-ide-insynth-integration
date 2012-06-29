package insynth

import scala.tools.nsc._
import scala.tools.nsc.plugins.PluginComponent
import scala.collection.mutable
import insynth.format.XMLable
import insynth.format.Utility

/** This class implements a plugin component using a tree
 *  traverser */
class TraverseComponent (val global: Global) extends PluginComponent {
  import global._
  import global.definitions._
    
  lazy val logger = Config.logger

  val runsAfter = List[String]("refchecks")
  /** The phase name of the compiler plugin
   *  @todo Adapt to specific plugin.
   */
  val phaseName = "plugintemplatetraverse"

  // TODO Map based on Tree.toString  
  var methodMap: mutable.Map[String, ApplicationArguments] = null
  
  type TreeMapType = String
  type TypeMapType = String
  implicit def treeToString(tree: Tree) = tree.toString
  
  var methodTypeMap: mutable.Map[TreeMapType, Int] = null
    
  def newPhase(prev: Phase): Phase = new TraverserPhase(prev)
  class TraverserPhase(prev: Phase) extends StdPhase(prev) {
    
    def apply(unit: CompilationUnit) {
      // initialize maps
      methodMap = mutable.Map.empty
      methodTypeMap = mutable.Map.empty
      
      // commence the traversal
      newTraverser().traverse(unit.body)
      
      // write statistics to files
      import Utility._
      for ( (method, appArgs) <- methodMap)
        appendToFile(Config.outputFilename, appArgs.toString)
    }
    
  }

  def newTraverser(): Traverser = new ForeachTreeTraverser(check)

  def fullName(sym:Symbol) = "method " + sym.fullName + sym.tpe.paramTypes.map(x => x.typeSymbol.fullName).mkString(" ",",","")
  def fullName(tree: Tree) = 
    if (tree.symbol != null) tree.symbol.fullName
    else "null"
  def getType(tree: Tree): TypeMapType = {
	if (tree.symbol != null) tree.symbol.tpe.toString
	else "NullType"
  }
  
  type Tree = TraverseComponent.this.global.Tree
    
  def check(tree: Tree): Unit = tree match {
    case Apply(fun, args) => {
      if (Config.isLogging) {
        logger.info("application of "+ fun + " with params: " + args.mkString(" ", ",", ""))
        logger.info("method full name: " + fullName(fun.symbol))
      }
      ApplicationAnalyzer(fun, args)
      
      fun match {
        case _:Apply | _: ApplyDynamic =>
    	  logger.info("found an application " + fun + " within an application " + tree)
        case _ =>
      }
    }
    case ApplyDynamic(qual, args) => {
      if (Config.isLogging) {
        logger.info("application of "+ qual + " with params: " + args.mkString(" ", ",", ""))
      }
      ApplicationAnalyzer(qual, args)
    }
    case _ => ()
  }
  
  object ApplicationAnalyzer {
    def apply(fun: Tree, args: List[Tree]) {
      logger.entering("ApplicationAnalyzer", "apply", Array(fun, args.mkString(" ", ",", "")))
      // get an ApplicationArguments object from the map or create a new one
      // record a call with given arguments
      logger.info("methodMap.contains(" + fun + "): " + methodMap.contains(fun))
      methodMap getOrElseUpdate(fun, new ApplicationArguments(fun)) recordCall args
      logger.info("methodMap.contains(" + fun + "): " + methodMap.contains(fun))
    }
  }
  
  class ApplicationArguments(val fun: Tree) extends XMLable {  
    
    case class ValueType(tpe: String) {
      var occurences = 0      
      def incOccurences = {
        occurences = occurences + 1
        this
      }
    }
    
    // array of statistics for parameters, each map corresponds to an argument (and
    // its type) and stores number of occurrences of that parameter in place of given
    // argument
    // TODO add declared type of argument
    var argsOccurences: Array[mutable.Map[TreeMapType, ValueType]] = new Array(fun.tpe.paramTypes.size)
    
    for (ind <- 0 until argsOccurences.size)
      argsOccurences(ind) = mutable.Map.empty
    
    def recordCall(params: List[Tree]) {
      logger.entering("ApplicationArguments", "recordCall", params)
      logger.info("Param size is: " + params.size)
      assert(argsOccurences.size == params.size)
      // iterate over collection of recorded arguments and given parameters
      for ((argMap, par) <- argsOccurences zip params) {
        // into appropriate argument map, increase or insert new occurrence of given parameter
        assert(argMap!=null)
        assert(par!=null)
        argMap += (
            treeToString(par) -> 
            ((argMap.getOrElse(par, ValueType(getType(par)))) incOccurences)
        )
      }
    }

    override def toXML = {
      logger.entering("ApplicationArguments", "toXML")
      logger.fine("argsOccurences.size = " + argsOccurences.size)
  	  for (arg <- argsOccurences)
        for ( (par, occurences) <- arg) {
    	  logger.fine("par in argsOccurences: " + par + " " + occurences)
          assert(par!=null)
          //assert(par.symbol!=null)
        }
      	    
      <method name= { treeToString(fun) }>
      	{ 
      	  for (arg <- argsOccurences) yield
      		<argument>
      		{ for ( (par, value) <- arg) yield
      		  <parameter name= { par.toString }
      			type= { value.tpe }
      			occurences= { value.occurences.toString }/>
  		    }
      		</argument>  
      	}
      </method>
    }
  
  }
  
}
