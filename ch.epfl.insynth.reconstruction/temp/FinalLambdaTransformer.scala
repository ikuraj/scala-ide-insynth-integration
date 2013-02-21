package ch.epfl.insynth.reconstruction

import ch.epfl.insynth.reconstruction.trees.
	{ Application => ApplicationNode, Abstraction => AbstractionNode, Variable => VariableNode, _}
import ch.epfl.lambda._
import ch.epfl.scala.trees.{ Variable => _, _}

object FinalLambdaTransformer extends (Node => Term) {
      
  object Unit extends Term
  
  def apply(root: Node) =
    transform(root)  
    
  private def transform(node: Node): Term = {
        
    // NOTE can be solved with a functional solution (stream of variables, not so pretty) 
    object variableGenerator {
      private var counter = 0
      
      def getFreshVariableName = "var_" + { counter+=1; counter }
    }
    
    node match {
      case VariableNode(_, name) => Variable(name)
      case Identifier(_, decl) => Variable(decl.fullName)
      case ApplicationNode(tpe, params) =>
        generateApplication(params map transform)
      case AbstractionNode(_, vars, body) =>
        generateAbstraction(vars, transform(body))
    }
    
    
  }
  
  /**
   * given a list of terms returns an application term in the form x1 x2 ... xn
   * @param list of terms
   * @return application term
   */
  private def generateApplication(nodes :List[Term]): App = {    
    def generateApplicationRec(list :List[Term]): App =
      list match {
        // if we have only one element, do the application without a recursive call, apply Unit
        case List(term1, term2) =>
          App(term1, term2)
        // for a list, get recursive application terms for all elements except the last
        // and then plug them to the top level App
        case _: List[Term] =>
          val innerApplication = generateApplicationRec(list.init)
          App(innerApplication, list.head)
        // we cannot have an empty list, because there are no parameters to apply to term
        case _ => throw new RuntimeException
	  }
    
    nodes match {
	  // we cannot have an empty list, because there are no parameters to apply to term
	  case List() => throw new RuntimeException
	  // if we have only one element, do the application without a recursive call, apply Unit
	  case List(term) => 
	    App(term, Unit)
      // for a list, get recursive application terms for all elements except the last
	  // and then plug them to the top level App
	  case list:List[Set[Term]] =>
	    generateApplicationRec(list)
  	}
  }
  
  private def generateAbstraction(list :List[VariableNode], body: Term): Term = {
    implicit def scalaTypeToLambdaType(st: ScalaType): Type = {
	  st match {
	    case Method(receiver, params, returnType) =>
	      Function(receiver +: params.flatten, returnType)
	    case Function(params, returnType) =>
	      TypeComplex(params.head, Function(params.tail, returnType))
        case Const(name) => TypeConst(name)
        case _ => throw new RuntimeException
	  }
    }
    
    list match {
	  case List() => body
	  case List(v) => 
	    Lambda(Variable(v name), v getType, body)
	  case list:List[VariableNode] =>
	    val v = list.head
	    Lambda(
          Variable(v name), v getType,
          generateAbstraction(list.tail, body)
        )
  	}
  }

  
}