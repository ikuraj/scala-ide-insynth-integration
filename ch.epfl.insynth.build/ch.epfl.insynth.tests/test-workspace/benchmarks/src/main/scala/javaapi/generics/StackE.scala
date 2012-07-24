package javaapi.StackE

//http://www.java2s.com/Code/JavaAPI/java.util/newStackE.htm

import java.util.EmptyStackException;
import java.util.Stack;

object MainClass {
  def showpush(st:Stack[Int], a:Int) { 
    st.push(a); 
    System.out.println("push(" + a + ")"); 
    System.out.println("stack: " + st); 
  } 
 
  def showpop(st:Stack[Int]) { 
    System.out.print("pop -> "); 
    var a = st.pop(); 
    System.out.println(a); 
    System.out.println("stack: " + st); 
  } 
 
  def main(args:Array[String]) { 
    var st:Stack[Int] = new Stack[Int]() //r=1
 
    System.out.println("stack: " + st); 
    showpush(st, 2); 
    showpush(st, 6); 
    showpush(st, 9); 
    showpop(st); 
 
    try {
      showpop(st); 
    } catch {
      case _ => System.out.println("empty stack"); 
    } 
  } 
}

/*
import java.util.EmptyStackException;
import java.util.Stack;
  
public class MainClass {
  static void showpush(Stack<Integer> st, int a) { 
    st.push(a); 
    System.out.println("push(" + a + ")"); 
    System.out.println("stack: " + st); 
  } 
 
  static void showpop(Stack<Integer> st) { 
    System.out.print("pop -> "); 
    Integer a = st.pop(); 
    System.out.println(a); 
    System.out.println("stack: " + st); 
  } 
 
  public static void main(String args[]) { 
    Stack<Integer> st = new Stack<Integer>(); 
 
    System.out.println("stack: " + st); 
    showpush(st, 2); 
    showpush(st, 6); 
    showpush(st, 9); 
    showpop(st); 
 
    try { 
      showpop(st); 
    } catch (EmptyStackException e) { 
      System.out.println("empty stack"); 
    } 
  } 
}
*/
