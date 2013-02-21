package org.ensime.examplecollector

import java.io._
import org.ensime.server.RichPresentationCompiler

/*
 *
 * Main ExampleCollector class.
 * Learns examples.
 *
 */
trait ExampleCollector {
 self: RichPresentationCompiler=>

  def loadFiles(){
    map = Map[String, Long]()
    appl = 0
    val files = loadFiles(new File(config.root.getAbsolutePath()+"\\src\\main\\scala"))
    files.foreach {
      x =>
	try {
	  val sourceFile = this.getSourceFile(x.getAbsolutePath())	
	  typedTree(sourceFile, true)
	  val unit = this.unitOf(sourceFile)
	  //println(x.getAbsolutePath())
	  traverse(unit.body)
	} catch {
	  case _ =>
	}
    }

    //println(map.mkString("\n"))

    val fw = new FileWriter(config.root.getAbsolutePath()+"\\Examples.txt", false)
    map.foreach {x => fw.write(x._1+";"+x._2+"\n")}
    fw.flush
    fw.close
  }

  def loadFiles(dir:File):List[File] = {
    var files = List[File]()
    dir.listFiles.foreach {
      x => 
	if (x.isDirectory()){
	  files = loadFiles(x) ::: files
	} else if (x.getName().endsWith(".scala")) files = x :: files
     }
    files
  }

  var appl:Long = 0
  var map = Map[String, Long]()

  def collect(sym:Symbol) {
    if(sym != null && sym.toString != "<none>" && sym.isMethod && checkName(sym.fullName)){
      val name = fullName(sym)
      if (map.contains(name)){
	map += name -> (map(name)+1)
      } else {
	map += name -> 1
      }
      appl+=1
    }
  }

  val forbidden = List("examples.tralalala", "super.","java.lang.Object.<init>","scala.Predef.exit", "$plus", "$minus", "$less", "$eq", "$greater", "$div", "$percent")

  def checkName(name:String) = (name.startsWith("scala.") || name.startsWith("java.") || name.startsWith("javax.")|| name.startsWith("swing.")|| name.startsWith("org.omg.") || name.startsWith("org.w3c.") || name.startsWith("org.xml.") || name.startsWith("org.ietf.")) && (!name.startsWith("scala.tools.")) && !forbidden.exists(x => name.contains(x))

  def fullName(sym:Symbol) = "method " + sym.fullName + sym.tpe.paramTypes.map(x => x.typeSymbol.fullName).mkString(" ",",","")

  protected var currentOwner: Symbol = null
  def traverse(tree: Tree): Unit = {
/*    println("Class  "+tree.getClass().getName())
    println("--------------------------------------------------------")
    println(tree)
    println("--------------------------------------------------------")
*/
    tree match {
      case EmptyTree =>
        ;
      case PackageDef(pid, stats) =>
        traverse(pid)
        atOwner(tree.symbol.moduleClass) {
          traverseTrees(stats)
        }
      case ClassDef(mods, name, tparams, impl) =>
        atOwner(tree.symbol) {
          traverseTrees(mods.annotations); traverseTrees(tparams); traverse(impl)
        }
      case ModuleDef(mods, name, impl) =>
        atOwner(tree.symbol.moduleClass) {
          traverseTrees(mods.annotations); traverse(impl)
        }
      case ValDef(mods, name, tpt, rhs) =>
        atOwner(tree.symbol) {
          traverseTrees(mods.annotations); traverse(tpt); traverse(rhs)
        }
      case DefDef(mods, name, tparams, vparamss, tpt, rhs) =>
        atOwner(tree.symbol) {
          traverseTrees(mods.annotations); traverseTrees(tparams); traverseTreess(vparamss); traverse(tpt); traverse(rhs)
        }
      case TypeDef(mods, name, tparams, rhs) =>
        atOwner(tree.symbol) {
          traverseTrees(mods.annotations); traverseTrees(tparams); traverse(rhs)
        }
      case LabelDef(name, params, rhs) =>
        traverseTrees(params); traverse(rhs)
      case Import(expr, selectors) =>
        traverse(expr)
      case Annotated(annot, arg) =>
        traverse(annot); traverse(arg)
      case Template(parents, self, body) =>
        traverseTrees(parents)
        if (!self.isEmpty) traverse(self)
        traverseStats(body, tree.symbol)
      case Block(stats, expr) =>
        traverseTrees(stats); traverse(expr)
      case CaseDef(pat, guard, body) =>
        traverse(pat); traverse(guard); traverse(body)
      case Alternative(trees) =>
        traverseTrees(trees)
      case Star(elem) =>
        traverse(elem)
      case Bind(name, body) =>
	print("Bind "+ name)
        traverse(body)
      case UnApply(fun, args) =>
	print("UnApply "+ fun)
        traverse(fun); traverseTrees(args)
      case ArrayValue(elemtpt, trees) =>
        traverse(elemtpt); traverseTrees(trees)
      case Function(vparams, body) =>
        atOwner(tree.symbol) {
          traverseTrees(vparams); traverse(body)
        }
      case Assign(lhs, rhs) =>
        traverse(lhs); traverse(rhs)
      case If(cond, thenp, elsep) =>
        traverse(cond); traverse(thenp); traverse(elsep)
      case Match(selector, cases) =>
        traverse(selector); traverseTrees(cases)
      case Return(expr) =>
        traverse(expr)
      case Try(block, catches, finalizer) =>
        traverse(block); traverseTrees(catches); traverse(finalizer)
      case Throw(expr) =>
        traverse(expr)
      case New(tpt) =>
        traverse(tpt)
      case Typed(expr, tpt) =>
        traverse(expr); traverse(tpt)
      case TypeApply(fun, args) =>
//	println("TypeApply "+ fun)
        traverse(fun); traverseTrees(args)
      case a @ Apply(fun, args) =>
	//println("fun = "+ fun +"  sym = "+fun.symbol)
	try{
	  this.collect(fun.symbol)
	} catch{
	  case _=>
	}
        traverse(fun); traverseTrees(args)
      case a @ ApplyDynamic(qual, args) =>
	//println("qual = "+ qual +"  sym = "+qual.symbol)
	try{
	  this.collect(qual.symbol)
	} catch{
	  case _=>
	}
        traverse(qual); traverseTrees(args)
      case Super(_, _) =>
        ;
      case This(_) =>
        ;
      case Select(qualifier, selector) =>
	traverse(qualifier)
      case Ident(_) =>
        ;
      case Literal(_) =>
        ;
      case TypeTree() =>
        ;
      case SingletonTypeTree(ref) =>
        traverse(ref)
      case SelectFromTypeTree(qualifier, selector) =>
        traverse(qualifier)
      case CompoundTypeTree(templ) =>
        traverse(templ)
      case AppliedTypeTree(tpt, args) =>
        traverse(tpt); traverseTrees(args)
      case TypeBoundsTree(lo, hi) =>
        traverse(lo); traverse(hi)
      case ExistentialTypeTree(tpt, whereClauses) =>
        traverse(tpt); traverseTrees(whereClauses)
      case SelectFromArray(qualifier, selector, erasure) =>
        traverse(qualifier)
    }

    def traverseTrees(trees: List[Tree]) {
      trees foreach traverse
    }

    def traverseTreess(treess: List[List[Tree]]) {
      treess foreach traverseTrees
    }

    def traverseStats(stats: List[Tree], exprOwner: Symbol) {
      stats foreach (stat =>
        if (exprOwner != currentOwner) atOwner(exprOwner)(traverse(stat))
        else traverse(stat)
      )
    }

    def atOwner(owner: Symbol)(traverse: => Unit) {
      val prevOwner = currentOwner
      currentOwner = owner
      traverse
      currentOwner = prevOwner
    }
  }
}
