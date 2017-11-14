package ch.epfl.insynth.reconstruction.combinator

import ch.epfl.insynth.trees.Type
import ch.epfl.insynth.trees.FormatType

/**
 * can return the type of the (sub)tree
 */
trait Typable {
  def getType: Type
}

/**
 * abstract tree node
 */
abstract class Node(tpe: Type) extends Typable {
  def getType: Type = tpe
}

case class AbsNode(tpe: Type) extends Node(tpe)

case class SimpleNode(decls:List[Declaration], tpe: Type, params:Map[Type, ContainerNode])
extends Node(tpe) {
  def getDecls = decls
  def getParams = params

  //Determine if two SimpleNodes are equal
//  override def equals(that: Any): Boolean = {
//    println("Checking Node Equality");
//    that match {
//      case SimpleNode(tDecls, tTpe, tParams) =>
//        if (tDecls.size == decls.size) {
//          var dEqual = false
//          for (d1 <- decls) {
//            dEqual = false
//            for (d2 <- tDecls) {
//              println("Anbout to call equals")
//              if (d2 equals d1) dEqual = true
//              //    	  if (d2.equals(d1)) dEqual = true
//            }
//            if (!dEqual) return false
//          }
//
//          val s1Params = params
//          val s2Params = tParams
//
////          return false
//
//          //Check that params are the same
//          //Check that set of keys are equal
//
//          println("Decals are equal Checking Params now");
//          if (s1Params.keys == s2Params.keys) {
//            //Compare children equality
//            for ((k, v1) <- s1Params) {
//              val v2 = s2Params(k)
//              val s1Nodes = v1.getNodes
//              val s2Nodes = v2.getNodes
//              for ((n1, n2) <- (s1Nodes zip s2Nodes)) {
//                if (!equals(n1, n2)) return false
//              }
//            }
//            return true
//          }
//          return false
//
//        } else false
//      case _ =>
//        false
//    }
//
//  }

}

/**
 * container for tree nodes
 */
case class ContainerNode(var nodes:Set[Node]) {
  def addNode(node:Node) {
    nodes += node
  }
  def getNodes = nodes
}

object FormatPrNode {
    def apply(node: Node) = new FormatPrNode(node: Node)
}

class FormatPrNode(node: Node) extends ch.epfl.insynth.print.Formatable {
  def toDocument = toDocument(node)

  def toDocument(node: Any): scala.text.Document = {
    import ch.epfl.insynth.print.FormatHelpers._

    node match {
      case AbsNode(tpe) => "Leaf" :: paren(FormatType(tpe).toDocument)
      case SimpleNode(decls, tpe, map) =>
        "SimpleNode" :: paren(FormatType(tpe).toDocument) :: nestedBrackets(
            seqToDoc(decls, ",", { d:Declaration => strToDoc(d.getSimpleName) })
            :/:
            seqToDoc(map.toList, ",",
              {
            	p:(Type, ContainerNode) => paren(FormatType(p._1).toDocument) :: "->" ::
            	nestedBrackets(toDocument(p._2))
              }
            )
        )
      case ContainerNode(nodes) =>
        nestedBrackets(seqToDoc(nodes.toList, ",", toDocument(_:Node)))
        //"Container"
      // should not happen
      case _ => throw new RuntimeException
    }
  }
}
