package ch.epfl.insynth.loader

import scala.tools.nsc.interactive.Global
import ch.epfl.insynth.env.InitialEnvironmentBuilder
import ch.epfl.insynth.InSynth

import ch.epfl.insynth.Config
import ch.epfl.insynth.Config.inSynthLogger

trait TCollector extends TData {
  self: InSynth =>

  import compiler._

  class Collector {

    private var lastVisitedTree: Tree = null
    private var info: RawData = null

    def gather(tree: Tree, pos: Position) = {
      info = new RawData()
      traverse(pos, tree)
      lastVisitedTree = null

      //Transform raw data into data that can be used by the Loader
      Transform(info)
    }

    private def traverse(pos: Position, tree: Tree) {
      if (tree.pos.includes(pos)) {
        lastVisitedTree = tree
        tree match {
          case PackageDef(pid, stats) =>
            this.extractImports(stats)
            this.info.setPackage(tree.symbol.moduleClass)
            this.traverseTrees(pos, stats)

          case ClassDef(mods, name, tparams, impl) =>
            this.info.addOwnerTypes(tree.symbol)
            this.traverse(pos, impl)

          case ModuleDef(mods, name, impl) =>
            this.info.addOwnerTypes(tree.symbol.moduleClass)
            this.traverse(pos, impl)

          case DefDef(mods, name, tparams, vparamss, tpt, rhs) =>
            vparamss.flatten.foreach(param => this.info.addToLocalContext(param.symbol))
            this.traverseBlock(pos, rhs)

          case TypeDef(mods, name, tparams, rhs) =>
            this.traverse(pos, rhs)

          case imp @ Import(expr, selectors) =>
            this.info.addImport(imp)
            this.traverse(pos, expr)

          case Template(parents, self, body) =>
            this.traverseStats(pos, body, tree.symbol)

          case t => /*throw new Exception */
            println("Case not covered in Collector.traverse: " + t.getClass.getName)
            Config.inSynthLogger.fine("Case not covered in Collector.traverse: " + t.getClass.getName)
        }
      } else {
        if (this.isCompletition(tree))
          lastVisitedTree match {
            case ValDef(mods, name, tpt: TypeTree, rhs) =>
              this.info.setFieldToComplete(lastVisitedTree.symbol)
              this.info.setDesiredType(tpt.tpe)

            case DefDef(mods, name, tparams, vparamss, tpt: TypeTree, rhs) if (rhs.tpe.typeSymbol.tpe.equals(definitions.NullClass.tpe)
              || rhs.tpe.typeSymbol.tpe.equals(NoType)) =>

              vparamss.flatten.foreach(param => this.info.addToLocalContext(param.symbol))
              this.info.setMethodToComplete(lastVisitedTree.symbol)
              this.info.setDesiredType(tpt.tpe)

            case _ =>
              this.info.setLocalToComplete(definitions.UnitClass)
              this.info.setDesiredType(definitions.UnitClass.tpe)

            //throw new Exception("Case not covered in Collector.traverse: "+t.getClass.getName) 
          }
        else {
          println("lastVisitedTree case")
          lastVisitedTree = tree
        }
      }
    }

    private def traverseBlock(pos: Position, tree: Tree) {
      if (tree.pos.includes(pos)) {
        tree match {
          case DefDef(mods, name, tparams, vparamss, tpt, rhs) =>
            if (isBlock(rhs)) {
              this.traverseBlock(pos, rhs)
            }
          case TypeDef(mods, name, tparams, rhs) =>
            this.traverseBlock(pos, rhs)

          case LabelDef(name, params, rhs) =>
            this.traverseBlockTrees(pos, params)
            this.traverseBlock(pos, rhs)

          case imp @ Import(expr, selectors) =>
            this.info.addImport(imp)
            this.traverseBlock(pos, expr)

          case Block(stats, expr) =>
            val list: List[Tree] = stats ::: List[Tree](expr)
            val length = list.size
            var i = 0
            while (i < length && (list(i).pos.startOrPoint < pos.startOrPoint || list(i).pos.includes(pos))) {
              val statement = list(i)
              if (statement.pos.includes(pos)) {
                traverseBlock(pos, statement)
              } else {
                if (i + 1 < length && this.isCompletition(list(i + 1))) {
                  statement match {
                    case ValDef(_, _, tpt: TypeTree, rhds) if (rhds.tpe.typeSymbol.tpe.equals(definitions.NullClass.tpe)
                      || rhds.tpe.typeSymbol.tpe.equals(NoType)) =>
                      this.info.setLocalToComplete(statement.symbol)
                      this.info.setDesiredType(tpt.tpe)

                    case If(cond, thenp, elsep) if (cond.symbol == null) =>
                      this.info.setLocalToComplete(definitions.BooleanClass)
                      this.info.setDesiredType(definitions.BooleanClass.tpe)

                    case Apply(fun, args) =>

                      val length = args.length
                      var i = 0
                      var cond = true
                      while (i < length && cond) {
                        val x = args(i)
                        try {
                          if (x.tpe.typeSymbol.tpe.equals(definitions.NullClass.tpe)) {
                            val param = fun.symbol.tpe.params(i)
                            this.info.setLocalToComplete(definitions.NullClass)
                            this.info.setDesiredType(param.tpe)
                            cond = false
                          } else {
                            x match {
                              case If(cond1, thenp, elsep) if (cond1.symbol == null) =>
                                this.info.setLocalToComplete(definitions.BooleanClass)
                                this.info.setDesiredType(definitions.BooleanClass.tpe)
                                cond = false
                              case _ =>
                            }
                          }
                        } catch {
                          case ex =>
                            inSynthLogger.fine("exception " + ex)
                            inSynthLogger.fine(ex.getStackTrace.mkString("\n"))
                        }
                        i += 1
                      }

                    /*
		               args.foreach{x => 
		                 
		                 x match {
			             case If(cond, thenp, elsep) if(cond.symbol == null) =>
			               this.info.setLocalToComplete(definitions.BooleanClass)
			               this.info.setDesiredType(definitions.BooleanClass.tpe)
			             }
		               }
			           */
                    case _ =>
                      this.info.setLocalToComplete(definitions.UnitClass)
                      this.info.setDesiredType(definitions.UnitClass.tpe)
                  }
                } else {
                  if (isValDef(statement)) {
                    this.info.addToLocalContext(statement.symbol)
                  }
                }
              }
              i += 1
            }
          case CaseDef(pat, guard, body) =>
            if (body.pos.includes(pos)) {
              pat match {
                case Apply(fun, args) =>
                  args.foreach { x =>
                    x match {
                      case Bind(name, body) =>
                        this.info.addToLocalContext(x.symbol)
                      case _ =>
                    }
                  }
                case _ =>
              }
            }
            this.traverseBlock(pos, body)
          case Alternative(trees) =>
            this.traverseBlockTrees(pos, trees)
          case Star(elem) =>
            this.traverseBlock(pos, elem)
          case Bind(name, body) =>
            this.traverseBlock(pos, body)
          case UnApply(fun, args) =>
            this.traverseBlock(pos, fun)
            this.traverseBlockTrees(pos, args)
          case ArrayValue(elemtpt, trees) =>
            this.traverseBlock(pos, elemtpt)
            this.traverseBlockTrees(pos, trees)
          case Function(vparams, body) =>
            this.traverseBlockTrees(pos, vparams)
            this.traverseBlock(pos, body)
          case Assign(lhs, rhs) =>
            this.traverseBlock(pos, lhs)
            this.traverseBlock(pos, rhs)
          case If(cond, thenp, elsep) =>
            this.traverseBlock(pos, cond)
            this.traverseBlock(pos, thenp)
            this.traverseBlock(pos, elsep)
          case Match(selector, cases) =>
            this.traverseBlock(pos, selector)
            this.traverseBlockTrees(pos, cases)
          case Return(expr) =>
            this.traverseBlock(pos, expr)
          case Try(block, catches, finalizer) =>
            this.traverseBlock(pos, block)
            this.traverseBlockTrees(pos, catches)
            this.traverseBlock(pos, finalizer)
          case Throw(expr) =>
            this.traverseBlock(pos, expr)
          case New(tpt) =>
            this.traverseBlock(pos, tpt)
          case Typed(expr, tpt) =>
            this.traverseBlock(pos, expr)
            this.traverseBlock(pos, tpt)
          case TypeApply(fun, args) =>
            this.traverseBlock(pos, fun)
            this.traverseBlockTrees(pos, args)
          case Apply(fun, args) =>
            this.traverseBlock(pos, fun)
            this.traverseBlockTrees(pos, args)
          case ApplyDynamic(qual, args) =>
            this.traverseBlock(pos, qual)
            this.traverseBlockTrees(pos, args)
          case Select(qualifier, selector) =>
            this.traverseBlock(pos, qualifier)
          case SingletonTypeTree(ref) =>
            this.traverseBlock(pos, ref)
          case SelectFromTypeTree(qualifier, selector) =>
            this.traverseBlock(pos, qualifier)
          case CompoundTypeTree(templ) =>
            this.traverseBlock(pos, templ)
          case AppliedTypeTree(tpt, args) =>
            this.traverseBlock(pos, tpt)
            this.traverseBlockTrees(pos, args)
          case TypeBoundsTree(lo, hi) =>
            this.traverseBlock(pos, lo)
            this.traverseBlock(pos, hi)
          case ExistentialTypeTree(tpt, whereClauses) =>
            this.traverseBlock(pos, tpt)
            this.traverseBlockTrees(pos, whereClauses)
          case SelectFromArray(qualifier, selector, erasure) =>
            this.traverseBlock(pos, qualifier)

          case _ =>
        }
      }
    }

    private def traverseBlockTrees(pos: Position, trees: List[Tree]) {
      trees.foreach { traverseBlock(pos, _) }
    }

    private def traverseBlockTreess(pos: Position, treess: List[List[Tree]]) {
      treess.foreach { traverseBlockTrees(pos, _) }
    }

    private def traverseBlockStats(pos: Position, stats: List[Tree], exprOwner: Symbol) {
      stats foreach { traverseBlock(pos, _) }
    }

    private def traverseTrees(pos: Position, trees: List[Tree]) {
      trees.foreach { traverse(pos, _) }
    }

    private def traverseTreess(pos: Position, treess: List[List[Tree]]) {
      treess.foreach { traverseTrees(pos, _) }
    }

    private def traverseStats(pos: Position, stats: List[Tree], exprOwner: Symbol) {
      stats foreach { traverse(pos, _) }
    }

    private def isCompletition(tree: Tree) = tree match {
      case t @ Apply(_, _) => {
        println(t.toString)
        t.toString match {
          case "scala.this{type}.Predef.exit{()Nothing}(){Nothing}" => true
          // added for Scala 2.10 compatibility
          case "scala.this{scala.type}.Predef.exit{()Nothing}(){Nothing}" => true
          case "scala.this.Predef.exit()" => true
          case _ => false
        }
      }
//      case t: ValDef if t.toString.contains("= null") =>
//        true
      case t => {
        println("t is not Apply, it is: " + t.getClass)
        println("t is" + t)
        println("***")
        false
      }
    }

    private def isBlock(tree: Tree) = tree match {
      case Block(_, _) => true
      case _ => false
    }

    private def isMethod(tree: Tree) = tree match {
      case DefDef(_, _, _, _, _, _) => true
      case _ => false
    }

    private def isValDef(tree: Tree) = tree match {
      case ValDef(_, _, _, _) => true
      case _ => false
    }

    private def isMatch(tree: Tree) = tree match {
      case Match(_, _) => true
      case _ => false
    }

    private def isTry(tree: Tree) = tree match {
      case Try(_, _, _) => true
      case _ => false
    }

    private def extractImports(trees: List[Tree]) {
      trees.foreach {
        _ match {
          case imp @ Import(_, _) =>
            this.info.addImport(imp)
          case _ =>
        }
      }
    }
  }
}