package ch.epfl.insynth.reconstruction.codegen

import ch.epfl.insynth.reconstruction.intermediate._
import ch.epfl.insynth.trees
import ch.epfl.scala.{ trees => Scala }
import ch.epfl.insynth.print._
import ch.epfl.insynth.reconstruction.combinator.{ NormalDeclaration, AbsDeclaration }
import ch.epfl.insynth.env.Declaration
import scala.text.Document
import scala.text.Document.empty
import ch.epfl.insynth.env.Declaration

/**
 * this trait deals with omitting the apply method names
 */
trait ApplyTransfromer extends CodeGenerator {
  // import methods for easier document manipulation
  import FormatHelpers._
  import Document._
  import TransformContext._
  
  /**
   * main method (recursive) for transforming a intermediate (sub)tree
   * @param tree root node of the (sub)tree to transform 
   * @return list of documents containing all combinations of available expression for
   * the given (sub)tree
   */
  abstract override def transform(tree: Node, ctx: TransformContext = Expr): List[Document] = {
        
    tree match {
      // apply parameters in the tail of params according to the head of params 
      case Application(tpe, qualifierSet :: params) => {
        // so far as we constructed, there should be only one application definition term
        // (which tells us, whether it is a function, a method...)
        assert(qualifierSet.size == 1)
        
        // get the single receiver
        val qualifier = qualifierSet.head
        
        // match the application definition term
        qualifier match {
          case Identifier(_, NormalDeclaration(decl)) 
          	if decl.isApply && decl.belongsToObject => 	        
	        // create a new delcaration
	        val newDecl = new Declaration("justForQualifier." + decl.getObjectName, decl.inSynthType, decl.scalaType)
	        newDecl.setBelongsToObject(false)
	        newDecl.setIsMethod(false)
	        assert(!newDecl.belongsToObject)
	        // transform to a function
	        val newQualifier = Identifier(tpe, NormalDeclaration(newDecl))
	        
	        //println("newDecl.getSimpleName: " + newDecl.getSimpleName)
	        	        
        	super.transform(Application(tpe, Set[Node](newQualifier) +: params.tail), ctx)
	        
          case _ => super.transform(tree, ctx)
          	
        } // params.head.head match 
      }
      case _ => super.transform(tree, ctx)
    } // tree match
  }
  
}

/**
 * this trait deals with transforming method/function names
 */
trait SimpleApplicationNamesTransfromer extends CodeGenerator {
  // import methods for easier document manipulation
  import FormatHelpers._
  import Document._
  import TransformContext._
  
  /**
   * main method (recursive) for transforming a intermediate (sub)tree
   * @param tree root node of the (sub)tree to transform 
   * @return list of documents containing all combinations of available expression for
   * the given (sub)tree
   */
  abstract override def transform(tree: Node, ctx: TransformContext = Expr): List[Document] = {
        
    tree match {      
      // identifier from the scope
      case Identifier(tpe, dec) if ctx == App =>
        List( NameTransformer(dec.getSimpleName) )
      case _ => super.transform(tree, ctx)
    } // tree match
  }
  
}