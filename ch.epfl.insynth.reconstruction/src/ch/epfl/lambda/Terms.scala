package ch.epfl.lambda

/** Abstract Syntax Trees for terms. */
abstract class Term

case class Variable(name: String) extends Term {
  override def toString = name
}

case class Lambda(variable: Variable, T: Type, t: Term) extends Term {
  override def toString = "(\\" + variable.name + ": " + T + ". " + t + ")"
}

case class Let(variable: Variable, T: Type, t1: Term, t2: Term) extends Term {
  override def toString = "(let " + variable.name + ":" + T + "=" + t1 + " in " + t2
}

case class App(t1: Term, t2: Term) extends Term {
  override def toString = (
      t2 match {
        case t:App => t1 + " (" + t2 + ")"
        case _ => t1 + " " + t2
      }
  )
}

/** Abstract Syntax Trees for types. */
abstract class Type extends Term

/**
 * set of primitive types
 * @param name name of the type
 */
case class TypeConst(name: String) extends Type {
  override def toString() = name
}

/**
 * arrow type (T1→T2) 
 */
case class TypeComplex(T1 : Type, T2 : Type) extends Type {
  override def toString() =
      T1 match {
        case t:TypeComplex => "(" + T1 + ")" + "->" + T2
        case _ => T1 + "->" + T2        
      }
}