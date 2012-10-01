package list

import leon.Utils._

object List {

  abstract class List
  case class Cons(head: Int, tail: List) extends List
  case class Nil() extends List

  def size(l: List): Int = waypoint(1, (l match {
    case Cons(_, tail) => {
     val returnValue: Int = sizeTail(tail, 1)
     //val returnValue: Int =  // 5th       
     returnValue
    }
    case Nil() => {
     val returnValue: Int = 0
     //val returnValue: Int = // 7th
     returnValue
    }
  })) ensuring(_ >= 0)


  def sizeTail(l2: List, acc: Int): Int = l2 match {
    case Cons(_, tail) => {
     //val returnValue: Int = sizeTail(tail, acc+1)
     val returnValue: Int =  /*!*/ // 4th
     returnValue
    }
    case Nil() => acc
  }

}
