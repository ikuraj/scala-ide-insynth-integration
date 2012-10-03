package list

import leon.Utils._

object List {

  abstract class List
  case class Cons(head: Int, tail: List) extends List
  case class Nil() extends List

  def size(l: List): Int = {l match {
    case Cons(_, tail) => {
     //val returnValue: Int = sizeTail(tail, 1) // 5th
     val returnValue: Int =  /*!*/       
     returnValue
    }
    case Nil() => {
     //val returnValue: Int = 0 // 7th
     val returnValue: Int =  /*!*/
     returnValue
    }
  }} ensuring(_ >= 0)


  def sizeTail(l2: List, acc: Int): Int = {l2 match {
    case Cons(_, tail) => {
     //val returnValue: Int = sizeTail(tail, acc+1) // 4th
     val returnValue: Int =  /*!*/
     returnValue
    }
    case Nil() => {
      acc
    }
  }} ensuring(_ >= 0)

}
