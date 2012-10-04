package rbtree

import scala.collection.immutable.Set
import leon.Annotations._
import leon.Utils._

// note moved outside of the object to be recognized by InSynth 
sealed abstract class Color
case class Red() extends Color
case class Black() extends Color

sealed abstract class Tree
case class Empty() extends Tree
case class Node(color: Color, left: Tree, value: Int, right: Tree) extends Tree

sealed abstract class OptionInt
case class Some(v: Int) extends OptionInt
case class None() extends OptionInt

object RedBlackTree { 

  // INSYNTH: not good example
  def content(t: Tree) : Set[Int] = t match {
    case Empty() => Set.empty
    case Node(_, l, v, r) => content(l) ++ Set(v) ++ content(r)
  }

  // INSYNTH: InSynth does not work here
  def size(t: Tree) : Int = {t match {
    case Empty() =>
      //0
      val result: Int =  /*!*/
      return result
    case Node(_, l, v, r) =>
      //size(l) + 1 + size(r)
      val result: Int =  /*!*/
      return result
  }} ensuring(_ >= 0)

  // INSYNTH: not good example
  /* We consider leaves to be black by definition */
  def isBlack(t: Tree) : Boolean = t match {
    case Empty() => true
    case Node(Black(),_,_,_) => true
    case _ => false
  }

  // INSYNTH: not good example
  def redNodesHaveBlackChildren(t: Tree) : Boolean = t match {
    case Empty() => true
    case Node(Black(), l, _, r) => redNodesHaveBlackChildren(l) && redNodesHaveBlackChildren(r)
    case Node(Red(), l, _, r) => isBlack(l) && isBlack(r) && redNodesHaveBlackChildren(l) && redNodesHaveBlackChildren(r)
  }

  // INSYNTH: not good example
  def redDescHaveBlackChildren(t: Tree) : Boolean = t match {
    case Empty() => true
    case Node(_,l,_,r) => redNodesHaveBlackChildren(l) && redNodesHaveBlackChildren(r)
  }

  // INSYNTH: not good example
  def blackBalanced(t : Tree) : Boolean = t match {
    case Node(_,l,_,r) => blackBalanced(l) && blackBalanced(r) && blackHeight(l) == blackHeight(r)
    case Empty() => true
  }
  
  // INSYNTH: InSynth works here!
  def blackHeight(t : Tree) : Int = t match {
    case Empty() => 
      //1
      val result: Int =  /*!*/
      return result
    case Node(Black(), l, _, _) =>
      //blackHeight(l) + 1
      val result: Int =  /*!*/
      return result
    case Node(Red(), l, _, _) =>
      //blackHeight(l)
      val result: Int =  /*!*/
      return result
  }

  // INSYNTH: works here
  // <<insert element x into the tree t>>
  def ins(x: Int, t: Tree): Tree = {
    require(redNodesHaveBlackChildren(t) && blackBalanced(t))
    t match {
      case Empty() => 
        //Node(Red(),Empty(),x,Empty())
        val nodeToReturn: Tree =  /*!*/        
        nodeToReturn
      case Node(c,a,y,b) =>
        if      (x < y)  balance(c, ins(x, a), y, b)
        else if (x == y) Node(c,a,y,b)
        else             balance(c,a,y,ins(x, b))
    }
  } ensuring (res => content(res) == content(t) ++ Set(x) 
                   && size(t) <= size(res) && size(res) <= size(t) + 1
                   && redDescHaveBlackChildren(res)
                   && blackBalanced(res))

  // INSYNTH: works here!
  def makeBlack(n: Tree): Tree = {
    require(redDescHaveBlackChildren(n) && blackBalanced(n))
    n match {
      case Node(Red(),l,v,r) =>
        //Node(Black(),l,v,r)
        val nodeToReturn: Tree =  /*!*/        
        nodeToReturn
      case _ =>
        //n
        val nodeToReturn: Tree =  /*!*/        
        nodeToReturn
    }
  } ensuring(res => redNodesHaveBlackChildren(res) && blackBalanced(res))

  // INSYNTH: not good example
  def add(x: Int, t: Tree): Tree = {
    require(redNodesHaveBlackChildren(t) && blackBalanced(t))
    makeBlack(ins(x, t))
  } ensuring (res => content(res) == content(t) ++ Set(x) && redNodesHaveBlackChildren(res) && blackBalanced(res))
  
  // INSYNTH: not good example
  def buggyAdd(x: Int, t: Tree): Tree = {
    require(redNodesHaveBlackChildren(t))
    ins(x, t)
  } ensuring (res => content(res) == content(t) ++ Set(x) && redNodesHaveBlackChildren(res))
  
  // INSYNTH: InSynth does not see everything here (inned matching)
  def balance(c: Color, a: Tree, x: Int, b: Tree): Tree = {
    Node(c,a,x,b) match {
      case Node(Black(),Node(Red(),Node(Red(),a,xV,b),yV,c),zV,d) => {
    	  //Node(Red(),Node(Black(),a,xV,b),yV,Node(Black(),c,zV,d))
        val nodeToReturn: Node =  /*!*/        
        nodeToReturn
      }
      case Node(Black(),Node(Red(),a,xV,Node(Red(),b,yV,c)),zV,d) => 
        //Node(Red(),Node(Black(),a,xV,b),yV,Node(Black(),c,zV,d))
        val nodeToReturn: Node =  /*!*/        
        nodeToReturn
      case Node(Black(),a,xV,Node(Red(),Node(Red(),b,yV,c),zV,d)) => 
        //Node(Red(),Node(Black(),a,xV,b),yV,Node(Black(),c,zV,d))
        val nodeToReturn: Node =  /*!*/        
        nodeToReturn
      case Node(Black(),a,xV,Node(Red(),b,yV,Node(Red(),c,zV,d))) => 
        //Node(Red(),Node(Black(),a,xV,b),yV,Node(Black(),c,zV,d))
        val nodeToReturn: Node =  /*!*/        
        nodeToReturn
      case Node(c,a,xV,b) => 
        //Node(c,a,xV,b)
        val nodeToReturn: Node =  /*!*/        
        nodeToReturn
    }
  } ensuring (res => content(res) == content(Node(c,a,x,b)))// && redDescHaveBlackChildren(res))

  // INSYNTH: InSynth does not see everything here
  def buggyBalance(c: Color, a: Tree, x: Int, b: Tree): Tree = {
    Node(c,a,x,b) match {
      case Node(Black(),Node(Red(),Node(Red(),a,xV,b),yV,c),zV,d) => 
        //Node(Red(),Node(Black(),a,xV,b),yV,Node(Black(),c,zV,d))
        val nodeToReturn: Node =  /*!*/        
        nodeToReturn
      case Node(Black(),Node(Red(),a,xV,Node(Red(),b,yV,c)),zV,d) => 
        //Node(Red(),Node(Black(),a,xV,b),yV,Node(Black(),c,zV,d))
        val nodeToReturn: Node =  /*!*/        
        nodeToReturn
      case Node(Black(),a,xV,Node(Red(),Node(Red(),b,yV,c),zV,d)) => 
        //Node(Red(),Node(Black(),a,xV,b),yV,Node(Black(),c,zV,d))
        val nodeToReturn: Node =  /*!*/        
        nodeToReturn
      case Node(Black(),a,xV,Node(Red(),b,yV,Node(Red(),c,zV,d))) => 
        //Node(Red(),Node(Black(),a,xV,b),yV,Node(Black(),c,zV,d))
        val nodeToReturn: Node =  /*!*/        
        nodeToReturn
      // case Node(c,a,xV,b) => Node(c,a,xV,b)
    }
  } ensuring (res => content(res) == content(Node(c,a,x,b)))// && redDescHaveBlackChildren(res))
}
