package search

import scala.tools.nsc._
import scala.tools.nsc.plugins.PluginComponent
import scala.collection.mutable
import search.format.XMLable
import search.format.Utility
import scala.collection.mutable.MutableList

/**
 * This class implements a plugin component using a tree
 *  traverser
 */
class TraverseComponent(val global: Global) extends PluginComponent {
  import global._
  import global.definitions._

  lazy val logger = Config.logger

  val runsAfter = List[String]("refchecks")
  /**
   * The phase name of the compiler plugin
   *  @todo Adapt to specific plugin.
   */
  val phaseName = "plugintemplatetraverse"

  def newPhase(prev: Phase): Phase = new TraverserPhase(prev)
  class TraverserPhase(prev: Phase) extends StdPhase(prev) {

    def apply(unit: CompilationUnit) {
      import Utility._

      logger.fine("Compilation unit path: " + unit.source.path)

      // commence the traversal
      newTraverser().traverse(unit.body)
      //writeToFile(Config.outputFilename, "hello")

    }

  }

  def newTraverser(): Traverser = new ForeachTreeTraverser(check)

  // names that we want to filter
  val forbidden = List("examples.tralalala", "super.", "java.lang.Object.<init>",
    "scala.Predef.exit", "$plus", "$minus", "$less", "$eq", "$greater", "$div",
    "$percent")
  def checkName(name: String) =
    // for the sake of testing
    name.startsWith("plugintemplate.examples") ||
      // normal conditions
      (name.startsWith("scala.") || name.startsWith("java.") ||
        name.startsWith("javax.") || name.startsWith("swing.") ||
        name.startsWith("org.omg.") || name.startsWith("org.w3c.") ||
        name.startsWith("org.xml.") || name.startsWith("org.ietf.")) &&
        !name.startsWith("scala.tools.") && !forbidden.exists(x => name.contains(x))

  type Tree = TraverseComponent.this.global.Tree

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

  def isPolymorphic(tpe: Type): Boolean = tpe match {
    case TypeRef(pre: Type, sym: Symbol, args: List[Type]) if (!sym.isMonomorphicType && !args.isEmpty) =>
      true
    case TypeRef(pre: Type, sym: Symbol, args: List[Type]) if (sym.isTypeParameter) =>
      true
    case _ => false
  }

  def checkIfFunctionType(tpe: Type): Boolean = {
    logger.entering(getClass.getName, "checkIfFunction", tpe)

    def getApplicationInfoMethodRec(methodType: Type): Boolean = methodType match {
      case MethodType(params, resultType) => true
      case t => false
    }

    def getApplicationInfoFunctionRec(functionType: Type): Boolean = functionType match {
      case TypeRef(pre: Type, sym: Symbol, args: List[Type]) if (definitions.isFunctionType(functionType)) || (definitions.isFunctionType(pre)) =>
        true
      case t => false
    }

    val result = getApplicationInfoMethodRec(tpe) || getApplicationInfoFunctionRec(tpe)
    logger.exiting(getClass.getName, "checkIfFunction", result)
    result
  }

  def check(tree: Tree): Unit = {
    //logger.entering(getClass.getName, "check")
    //logger.fine(tree.getClass.getName)

    tree match {
      case Apply(fun, args) => {
        import Utility._
        //        logger.fine("fun.symbol.isAnonymousFunction = " + fun.symbol.isAnonymousFunction)
        //        logger.fine("fun.tpe = " + fun.tpe)
        //        logger.fine("fun.symbol.tpe = " + fun.symbol.tpe)
        //        logger.fine("definitions.isFunctionType(fun.tpe) " + definitions.isFunctionType(fun.tpe))
        //        logger.fine("definitions.isFunctionType(fun.symbol.tpe) " + definitions.isFunctionType(fun.symbol.tpe))
        logger.fine("App tree: " + fun + " tree position " + tree.pos)

        val isPolymorphicRes = (false /: args) { (res, arg) => res || isPolymorphic(arg.tpe) }
        logger.info("isPolymorphic =" + isPolymorphicRes)

        val isCandidate = (false /: args) {
          (result, arg) => result || checkIfFunctionType(arg.tpe)
        }
        if (isCandidate)
          appendToFile(Config.outputFilename,
            tree.symbol.name + "...=" + tree.pos.source.path + ":" + tree.pos.line)
      }
      case ApplyDynamic(qual, args) => {
      }
      case _ => ()
    }
  }

}
