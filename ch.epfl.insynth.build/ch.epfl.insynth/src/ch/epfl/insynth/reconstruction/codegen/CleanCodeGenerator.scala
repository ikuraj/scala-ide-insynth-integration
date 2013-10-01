package ch.epfl.insynth.reconstruction.codegen

import insynth.reconstruction.stream._
import ch.epfl.insynth.scala.loader.{ ScalaDeclaration => Declaration }
import ch.epfl.insynth.{ scala => Scala }		

import insynth.util.format._

import scala.text.Document
import scala.text.Document.empty
import scala.text.DocNil

/** companion object for more convenient application */
object CleanCodeGenerator {
  def apply(tree: Node) = {
    (new CleanCodeGenerator).apply(tree)
  }
}

/**
 * this class support scala syntax without unnecessary parentheses and dots
 */
class CleanCodeGenerator extends CodeGenerator {
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
    
    // a local variable to set if parentheses are needed
    var parenthesesRequired = true

    // do parentheses for parameters if needed
    def doParenApp(appId: Document, params: Document) =
      params match {
        case _ if parenthesesRequired => (appId :: paren(params))
        case DocNil if !parenthesesRequired => appId
        case _ => (appId :/: params)
      }

    def doParenRecApp(receiverDoc: Document, appId: Document, params: Document) = {
      receiverDoc match {
        case DocNil =>
          // if receiver is empty then we always need this form
          doParenApp(appId, params)
        case _: Document =>
          if (parenthesesRequired)
            (receiverDoc :: "." :: appId :: paren(params))
          else
            (receiverDoc :/: appId :/?: params)
      }
    }        
    
    def doParen(d: Document) = if (parenthesesRequired) paren(d) else d

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
//              assert(params(1) == NullLeaf)
              // set if we need parentheses
              parenthesesRequired =
                // if there are any parameters or ctx is as receiver (App)
                params.drop(1).size > 0 || ctx == App
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
              parenthesesRequired =
	              {
	                // if we have more than one parameter or this term is a single parameter
	                // to outer application
	                (params.drop(2).size > 1 || ctx == SinglePar) &&
	                // and hasParentheses must be false
	                decl.hasParentheses
	              }
              
              // go through all combinations of parameters documents
              val paramsDoc = getParamsCombinations(params.drop(2), paramsInfo, parenthesesRequired)
              
              // TODO when to generate dot and when not??
              //group(decl.getObjectName :: "." :: doParen(appIdentifier, paramsDoc))
          		return group(doParenRecApp(decl.getObjectName, appIdentifier, paramsDoc))              
            }

            // TODO refactor - similar to the method construction 
            if (decl.isField) {
              assert(params.size == 2)
              // if the field needs this keyword
              val needsThis = decl.hasThis
              val receiver = params(1)
                
              // get documents for the receiver objects (or empty if none is needed)
              val documentsForThis = {
                if (!needsThis)
                  receiver match {
                    case Identifier(_, receiverDecl: Declaration) if receiverDecl.isThis => empty
                    case _ => transform(receiver, App)
                  }
                else transform(receiver, App)
              }
              
              group(documentsForThis :?/: appIdentifier)              
            } 
            
            else if (!decl.isMethod) {
              assert(!decl.isConstructor)
              // just a function
              //parenthesesRequired = params.tail.size >= 1 || ctx == SinglePar
              parenthesesRequired = true
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
              
              // currying will handle parentheses if needed
              parenthesesRequired = 
                (params.drop(2).size > 1 || ctx == SinglePar || ctx == App) &&
                // and hasParentheses must be false
                decl.hasParentheses
              
              // if the method needs this keyword
              val needsThis = decl.hasThis
              val receiver = params(1)
                    
              // get documents for the receiver objects (or empty if none is needed)
              val documentsForThis = {
                if (!needsThis)
                  receiver match {
                    case Identifier(_, receiverDecl: Declaration) if receiverDecl.isThis =>
                      parenthesesRequired = params.drop(2).size >= 1
                      empty
                    case _ => transform(receiver, App) // map { (_:Document) :: "." }			            
                  }
                else transform(receiver, App) // map { (_:Document) :: "." }
              }
              
              // go through all the receiver objects and add to the list              
              // go through all combinations of parameters documents
              val paramsDoc = getParamsCombinations(params.drop(2), paramsInfo, parenthesesRequired)
                // and add them to the list
              group(doParenRecApp(documentsForThis, appIdentifier, paramsDoc))              
            }
          }

          // function that is created as an argument or anything else
          case Identifier(_, d: Declaration) if d.isAbstract =>
          	firstTermFunctionTransform
          case _: Variable | _ =>
            firstTermFunctionTransform
        }
      }
      // abstraction first creates all of its arguments
      case Abstraction(tpe, vars, subtree) =>
        assert(vars.size > 0)
        // check if we need parentheses for variables
        parenthesesRequired = vars.size > 1
        // for all bodies of this abstraction
        // for all transformations of bodies
        val transformedBody = transform(subtree, Expr)

        val abstractionResult =
	        // transform argument variables
	        doParen(seqToDoc(vars, ", ", { v: Variable => transform(v, Arg) })) :/:
	        "=>" :/:
	        // transform the body
	        transformedBody
        
        // return abstraction results
        // we need brackets only if this abstraction is parameter and it will not have parentheses
        if (ctx == SinglePar)
          brackets(abstractionResult)
        else
          abstractionResult
    }
  }
  
}