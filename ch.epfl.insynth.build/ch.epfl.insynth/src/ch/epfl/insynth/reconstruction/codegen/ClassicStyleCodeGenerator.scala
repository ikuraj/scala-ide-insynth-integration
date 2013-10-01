package ch.epfl.insynth.reconstruction.codegen

import insynth.reconstruction.stream._
import ch.epfl.insynth.scala.loader.{ ScalaDeclaration => Declaration }
import ch.epfl.insynth.{ scala => Scala }		

import insynth.util.format._

import scala.text.Document
import scala.text.Document.empty
import scala.text.DocNil

/** companion object for more convenient application */
object ClassicStyleCodeGenerator {
  def apply(tree: Node) = {
    (new ClassicStyleCodeGenerator).apply(tree)
  }
}

/**
 * this class support scala syntax without omitting any unnecessary parentheses and dots
 */
class ClassicStyleCodeGenerator extends CodeGenerator {
  // import methods for easier document manipulation
  import FormatHelpers._
  import Document._
  import TransformContext._
  import DocumentHelper._
  
  /**
   * main method (recursive) for transforming a intermediate (sub)tree
   * @param tree root node of the (sub)tree to transform 
   * @return list of documents containing all combinations of available expression for
   * the given (sub)tree
   */
  override def transform(tree: Node, ctx: TransformContext = Expr): Document = {
    // we want ternary operator
    import Bool._
    
    tree match {
      // variable (declared previously as arguments)
      // NOTE case when it is an argument (type is not needed)
      //case Variable(tpe, name) if ctx==Arg => List ( group(name :: ": " :: transform(tpe) ) )
      case Variable(tpe, name) => name
      // identifier from the scope
      case Identifier(tpe, dec) => 
        dec.getSimpleName
      // apply parameters in the tail of params according to the head of params 
      case Application(tpe, params) => {
        // import ternary operator
        import Bool._

        // most usual transformation in which the head is a function
        def firstTermFunctionTransform = {
          // set the recursive transformation context
          val recCtx = if (params.tail.size == 1) SinglePar else Par
          // go through all possible transformations of functions
          val appIdentifier = transform(params.head, App)
          val paramsDoc = getParamsCombinations(params.tail, recCtx)
          
        	group(doParenApp(appIdentifier, paramsDoc))          
        }

        // match the application definition term
        params.head match {
          case Identifier(_, decl: Declaration) if !decl.isAbstract => {
            // transform the application identifier in an application context
            val appIdentifier = transform(params.head, App)

            /* check what the declaration says about the application */

            // if inheritance function, just recursively transform
            if (decl.isInheritanceFun) {
              assert(params.size == 2)
              return transform(params(1), ctx)
            }

            // if literal just return simple name
            if (decl.isLiteral) {
              assert(params.size == 1)
              return decl.getSimpleName
            }

          // constructor call
          // NOTE cannot be curried?
          if (decl.isConstructor) {
            //assert(params(1) == NullLeaf)

            // go through all combinations of parameters documents
            val paramsDoc = getParamsCombinations(params.drop(1))
            
        		return group("new" :/: doParenApp(appIdentifier, paramsDoc))       
          }

          // method is on some object
          if (decl.belongsToObject) {	
          	assert(params(1) == NullLeaf)   

          	// get info about parameters
          	val paramsInfo = decl.scalaType match {
          	case Scala.Method(_, params, _) => params
          	case _ => throw new RuntimeException("Declared method but scala type is not")
          	}
          	
          	// set if we need parentheses
          	val parenthesesRequired = decl.hasParentheses

          	// go through all combinations of parameters documents
          	return group(
      	    		// TODO when to generate dot and when not??
      					//group(decl.getObjectName :: "." :: doParen(appIdentifier, paramsDoc))          				
    				    parenthesesRequired ?
    				    (decl.getObjectName :: "." :: appIdentifier :: paren(getParamsCombinations(params.drop(2), paramsInfo, true))) |
    				    decl.getObjectName :: "." :: appIdentifier        				    
  				    )
          }

          // TODO refactor - similar to the method construction 
          if (decl.isField) {
            assert(params.size == 2)
            
            // if the field needs this keyword
            val needsThis = decl.hasThis            
            val receiver = params(1)
            
            // get documents for the receiver objects (or empty if none is needed)
            val documentForThis = {
              if (!needsThis)
                receiver match {
                  case Identifier(_, receiverDecl: Declaration) if receiverDecl.isThis => empty
                  case _ => transform(receiver, App) :: "."
                }
              else transform(receiver, App):: "."
            }
            
            group(documentForThis :: appIdentifier)
          } 
	          	        
	        else if (!decl.isMethod) {
	          assert(!decl.isConstructor)
	          
        	  // just a function
        	  firstTermFunctionTransform
	        }
          
	        else // if (decl.isMethod)
            {
              // TODO solve parentheses here (with currying)
              // get info about parameters
              val paramsInfo = decl.scalaType match {
                case Scala.Method(_, params, _) => params
                case _ => throw new RuntimeException("Declared method but scala type is not")
              }
              
	          	// set if we need parentheses
	          	val parenthesesRequired = decl.hasParentheses

              // if the method needs this keyword
              val needsThis = decl.hasThis
              val receiver = params(1)

              // get documents for the receiver objects (or empty if none is needed)
              val documentsForThis = {
                if (!needsThis)
                  receiver match {
                    case Identifier(_, receiverDecl: Declaration) if receiverDecl.isThis =>
                      empty
                    case _ => transform(receiver, App) // map { (_:Document) :: "." }			            
                  }
                else transform(receiver, App) // map { (_:Document) :: "." }
              }

              // go through all the receiver objects and add to the list
              val receiverDoc = documentsForThis
              // go through all combinations of parameters documents
              val paramsDoc = getParamsCombinations(params.drop(2), paramsInfo, true)
              // and add them to the list
              
              group(
            			parenthesesRequired ?
                  (receiverDoc ?:: "." :: appIdentifier :: paren(paramsDoc)) |
                  receiverDoc ?:: "." :: appIdentifier
              )
            }
          } // case Identifier
          
          // function that is created as an argument or anything else
          case Identifier(_, _: Declaration) | _:Variable | _ =>
          	firstTermFunctionTransform
          	
        } // params.head.head match 
      }
      
      // abstraction first creates all of its arguments
      case Abstraction(tpe, vars, subtree) =>
        assert(vars.size > 0)
        
        // check if we need parentheses for variables
        val parenthesesRequired = vars.size > 1

        // for all bodies of this abstraction
        val abstractionResults =
          // transform argument variables
          (
        		parenthesesRequired ? 
          		paren(seqToDoc(vars, ", ", { v: Variable => transform(v, Arg) })) |
          		seqToDoc(vars, ",", { v: Variable => transform(v, Arg) })
      		) :/: "=>" :/:
          // transform the body
          transform(subtree, Expr)
          

        // return abstraction results
        // we need brackets only if this abstraction is parameter and it will not have parentheses
        if (ctx == SinglePar)
          brackets(abstractionResults)
        else
          abstractionResults
    } // tree match
  }
  
  // helper method for application, output parentheses always
  def doParenApp(appId: Document, params: Document) = appId :: paren(params)
    
}