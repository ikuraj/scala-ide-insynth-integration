package insynth

import scala.tools.nsc._
import scala.tools.nsc.plugins.PluginComponent
import scala.collection.mutable
import insynth.format.XMLable
import insynth.format.Utility
import scala.collection.mutable.MutableList

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
  def treeToString(tree: Tree) = tree.toString
  def applicationToString(tree: Tree) = fullName(tree) 
  implicit def typeToString(tree: Type) = tree.toString
  
  //var methodTypeMap: mutable.Map[TreeMapType, mutable.Map[TypeMapType, ReturnTypeAnalyzer.ValueType]] = null
    
  def newPhase(prev: Phase): Phase = new TraverserPhase(prev)
  class TraverserPhase(prev: Phase) extends StdPhase(prev) {
    
    def apply(unit: CompilationUnit) {
      // initialize maps
      methodMap = mutable.Map.empty
      //methodTypeMap = mutable.Map.empty
      
      // commence the traversal
      newTraverser().traverse(unit.body)
      
      // write statistics to files
      import Utility._
      for ( (method, appArgs) <- methodMap) {
        val xmlToWrite = 
          appArgs.toXML
        /*<method name= { treeToString(appArgs.fun) }>
          <returnTypes>
        {
          for ((tpe, occurences) <- methodTypeMap(method)) yield {        
            <returnType tpe = { tpe } occurences = { occurences.occurences.toString }/>
          }
        }
          </returnTypes>
          <arguments>
      	{
      	  for ((tpe, arg) <- appArgs.argsOccurences) yield
      		<argument type= { tpe }>
      		{ for ( (par, value) <- arg) yield
      		  <parameter name= { par.toString }
      			type= { value.tpe }
      			occurences= { value.occurences.toString }/>
  		    }
      		</argument>  
      	}
      	  </arguments>
      	</method>*/
      	
        appendToFile(Config.outputFilename, XMLable(xmlToWrite))
      }
    }
    
  }

  def newTraverser(): Traverser = new ForeachTreeTraverser(check)
 
  def fullName(tree: Tree) = 
    if (tree.symbol != null) {
	  val sym = tree.symbol
//	  sym.fullName + sym.tpe.paramTypes.map(x => x.typeSymbol.fullName).mkString("(",",",")")
	  sym.fullName +
      (for (list <- ApplicationArguments.getApplicationInfo(tree.tpe)) yield {
      	(for (tpe <- list) yield {
  		  typeToString(tpe)
      	}).mkString("(", ",", ")")
      }).mkString
    }
    else "null"
      
  def getType(tree: Tree): TypeMapType = {
	tree.tpe.typeSymbol.fullName
  }
  
  // names that we want to filter
  val forbidden = List("examples.tralalala", "super.","java.lang.Object.<init>",
      "scala.Predef.exit", "$plus", "$minus", "$less", "$eq", "$greater", "$div",
      "$percent")
  def checkName(name: String) = 
    // for the sake of testing
    name.startsWith("plugintemplate.examples") ||
    // normal conditions
    (name.startsWith("scala.") || name.startsWith("java.") ||
    name.startsWith("javax.")|| name.startsWith("swing.")||
    name.startsWith("org.omg.") || name.startsWith("org.w3c.") ||
    name.startsWith("org.xml.") || name.startsWith("org.ietf.")) &&
    !name.startsWith("scala.tools.") && !forbidden.exists(x => name.contains(x))
  
  type Tree = TraverseComponent.this.global.Tree
    
  def check(tree: Tree): Unit = tree match {
    case Apply(fun, args) => {
      if (Config.isLogging) {
        logger.info("application of "+ fun + " with params: " + args.mkString(" ", ",", ""))
        logger.info("method full name: " + fullName(fun))
      }
      logger.fine("checking name: " + applicationToString(ApplicationAnalyzer.getInnermostApplicationRec(fun, 0)._1))
      if (checkName(applicationToString(ApplicationAnalyzer.getInnermostApplicationRec(fun, 0)._1))) {        
	    ApplicationAnalyzer(fun, args)
      //ReturnTypeAnalyzer(fun)
      }
    }
    case ApplyDynamic(qual, args) => {
      // not yet covered
      throw new RuntimeException
    }
    case _ => ()
  }
  
  object ApplicationAnalyzer {
    
    lazy val logger = Config.loggerAppAnalyzer    
    
    def getInnermostApplicationRec(tree: Tree, level: Int): (Tree, Int) = tree match {
	  case Apply(fun, args) => {
	  	logger.fine("found an inner application " + fun + " within an application " + tree)
	  	getInnermostApplicationRec(fun, level + 1)
	  }
	  case _: ApplyDynamic => throw new RuntimeException
	  case Select(qualifier, name) if "apply" == name.toString.trim => {
	  	logger.fine("Select(qualifier, name), name: " + name + ")")
	  	getInnermostApplicationRec(qualifier, level)
	  }
	  case t => {
	  	logger.fine("case t => (t: " + t + ")")
	    (t, level)
	  }
    }
    
    def apply(fun: Tree, args: List[Tree]) {
      logger.entering("ApplicationAnalyzer", "apply", Array(fun, args.mkString(" ", ",", "")))
      
      val (innerApp, pos) = getInnermostApplicationRec(fun, 0)
      logger.fine("(innerApp, pos) = " + innerApp + ", " + pos)
      
      val innerAppKey = applicationToString(innerApp)
      
      // get an ApplicationArguments object from the map or create a new one
      // record a call with given arguments
      //logger.info("methodMap.contains(" + fun + "): " + methodMap.contains(fun))
      methodMap getOrElseUpdate(innerAppKey, new ApplicationArguments(innerApp)) recordCall (pos, args)
      //logger.info("methodMap.contains(" + fun + "): " + methodMap.contains(fun))
    }
  }
  
//  object ReturnTypeAnalyzer {
//    
//    lazy val logger = Config.loggerReturnTypeAnalyzer
//    
//    private def onlyReturnType(rawReturn: Type): Type = rawReturn match {
//      case MethodType(_, resultType) => onlyReturnType(resultType)
//	  case t => t
//    }
//    
//    private def getReturnType(tpe:Type): TypeMapType = {
//	  val returnType = onlyReturnType(tpe.resultType)
//      
//	  traverse(returnType)
//    }
//
//  private def traverse(tpe:Type): TypeMapType = {
//    tpe match {
//	  //Polymorphic
//	  case PolyType(typeParams: List[Symbol], resultType: Type) =>
//        traverse(resultType)
//	    
//      //Function type
//	  case TypeRef(pre: Type, sym: Symbol, args: List[Type])
//	    if(definitions.isFunctionType(tpe)) => {
//	    logger.finest("case TypeRef matched")
//	    val list = args.init.map(traverse)
//	    val result = traverse(args.last)	    
//	      
//	    result
//      }
//	      
//	  //TODO: => Type  
//	  /*case TypeRef(pre: Type, sym: Symbol, args: List[Type])
//	    if (!sym.isMonomorphicType && args.length == 1 && sym.fullName == "scala.<byname>")=>
//	      traverse(args(0))
//	  */
//	    
//	  //Polymorphic instantiated types
//	  case TypeRef(pre: Type, sym: Symbol, args: List[Type])
//	    if (!sym.isMonomorphicType && !args.isEmpty) => {
//    	  logger.finest("case TypeRef matched - if (!sym.isMonomorphicType && !args.isEmpty)")
//	      sym.fullName + (args.map(traverse) mkString("[", ",", "]"))
//	    }
//	     
//	  //Base types
//	  case TypeRef(pre: Type, sym: Symbol, args: List[Type]) => {
//    	logger.finest("case TypeRef matched")
//	    if (!sym.isTypeParameter) sym.fullName	      
//	      else throw new Exception("<<Parametrized types not supported: "+tpe.getClass.getName+">>")
//	  }
//	    
//	  case _ => throw new Exception("<<Not supported: "+tpe.getClass.getName+">>") 
//    }
//  }  
//    
//    case class ValueType {
//      var occurences = 0      
//      def incOccurences = {
//        occurences = occurences + 1
//        this
//      }
//    }
//    
//    def apply(fun: Tree) {
//      logger.entering("ReturnTypeAnalyzer", "apply", fun )
//      val methodType = fun.symbol.tpe
//      val typeString: String = getReturnType(methodType) 
//      logger.info("getType(fun)" + typeString )
//      val funKey = applicationToString(fun)
//      methodTypeMap.getOrElseUpdate(funKey, mutable.Map.empty ) getOrElseUpdate
//      	(typeString, ValueType()) incOccurences
//      	//{ case (tpe, value) => tpe == typeString } map { _._2.incOccurences }      
//    }
//  }
  
  object ApplicationArguments {
    
    def getApplicationInfo(methodType: Type): List[List[Type]] = {
      logger.entering("ApplicationArguments", "getApplicationInfo", methodType)
      
      def getApplicationInfoMethodRec(methodType: Type, collectedList: List[List[Type]]): (List[List[Type]], Type) = methodType match {
        case MethodType(params, resultType) => getApplicationInfoMethodRec( resultType, collectedList :+ (params map { _.tpe }) )
        case t => (collectedList, t)
      }
      
      def getApplicationInfoFunctionRec(functionType: Type, collectedList: List[List[Type]]): List[List[Type]] = functionType match {
        case TypeRef(pre: Type, sym: Symbol, args: List[Type]) if(definitions.isFunctionType(functionType)) =>
          getApplicationInfoFunctionRec(args.last, collectedList :+ args.init)
        case t => collectedList
      }
      
      val (types, resultType) = getApplicationInfoMethodRec(methodType, List.empty)
      val typesFunction = getApplicationInfoFunctionRec(resultType, List.empty)
      
      val result = types ++ typesFunction
      logger.exiting("ApplicationArguments", "getApplicationInfo", result)
      result
    }
    
    def getReturnType(methodType: Type): Type = {
      logger.entering("ApplicationArguments", "getReturnType", methodType)
      
      def getApplicationInfoMethodRec(methodType: Type): Type = methodType match {
        case MethodType(params, resultType) => getApplicationInfoMethodRec( resultType )
        case t => t
      }
      
      def getApplicationInfoFunctionRec(functionType: Type): Type = functionType match {
        case TypeRef(pre: Type, sym: Symbol, args: List[Type]) if(definitions.isFunctionType(functionType)) =>
          getApplicationInfoFunctionRec(args.last)
        case t => t
      }
      
      val result = getApplicationInfoFunctionRec(getApplicationInfoMethodRec(methodType))
      logger.exiting("ApplicationArguments", "getApplicationInfo", result)
      result
    }
    
  }
  
  class ApplicationArguments(val fun: Tree) extends XMLable {  
    import ApplicationArguments._
    
    lazy val logger = Config.loggerAppInfo
    
    case class ValueType(tpe: String) {
      var occurences = 0      
      def incOccurences = {
        occurences = occurences + 1
        this
      }
    }
    
    // number of occurence of this application
    var numberOfApplications = 0
    
    // array of statistics for parameters, each map corresponds to an argument (and
    // its type) and stores number of occurrences of that parameter in place of given
    // argument
    // TODO add declared type of argument
    var argsOccurences: Array[Array[(TypeMapType, mutable.Map[TreeMapType, ValueType])]] =
      //new Array(fun.tpe.paramTypes.size)
      (for (list <- getApplicationInfo(fun.tpe)) yield
      	(for (tpe <- list) yield
  		  (typeToString(tpe), mutable.Map[TreeMapType, ValueType]())
	    ).toArray
      ).toArray
        
//    for (ind <- 0 until argsOccurences.size) {
//      argsOccurences(ind) = (fun.tpe.paramTypes(ind).toString, mutable.Map.empty)
//    }
    
    def recordCall(posGroup: Int, params: List[Tree]) {
      logger.entering("ApplicationArguments", "recordCall", params)
      logger.info("Param size is: " + params.size)
      
      // increase number of occurences
      numberOfApplications += 1
      
      assert(argsOccurences(posGroup).size == params.size)
      // iterate over collection of recorded arguments and given parameters
      for (((tpe, argMap), par) <- argsOccurences(posGroup) zip params) {
        // into appropriate argument map, increase or insert new occurrence of given parameter
        assert(argMap!=null)
        assert(par!=null)
        argMap += (
            treeToString(par) -> 
            ((argMap.getOrElse(treeToString(par), ValueType(getType(par)))) incOccurences)
        )
      }
    }

    override def toXML = {
      logger.entering("ApplicationArguments", "toXML")
//  	  for ((tpe, arg) <- argsOccurences)
//        for ( (par, occurences) <- arg) {
//    	  logger.fine("par in argsOccurences: " + par + " " + occurences)
//          assert(par!=null)
//          //assert(par.symbol!=null)
//        }
      	    
      <method name= { applicationToString(fun) }
      	returnType= { typeToString(getReturnType(fun.tpe)) }
      	occurences= { numberOfApplications.toString }
      >
      	{ 
      	  for (argsOccurencesList <- argsOccurences) yield
      	    <argumentGroup>
      	  {
      	  for ((tpe, arg) <- argsOccurencesList) yield
      		<argument type= { tpe }>
      		{ for ( (par, value) <- arg) yield
      		  <parameter name= { par.toString }
      			type= { value.tpe }
      			occurences= { value.occurences.toString }/>
  		    }
      		</argument>
      	  }
      	    </argumentGroup>
      	}
      </method>
    }
  
  }
  
}
