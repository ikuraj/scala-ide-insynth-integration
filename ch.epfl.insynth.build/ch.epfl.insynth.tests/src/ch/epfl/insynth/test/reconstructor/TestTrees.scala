package ch.epfl.insynth.test.reconstructor

//import ch.epfl.insynth.env._
import ch.epfl.insynth.env.Node
import ch.epfl.insynth.env.Declaration

import ch.epfl.scala.trees._
import ch.epfl.insynth.trees.{ Const=>InSynthConst, Type=>InSynthType, _ }
import scala.collection.mutable.{ Map => MutableMap }
import scala.collection.mutable.{ Set => MutableSet }
import scala.collection.immutable.{ Map => ImmutableMap}
import scala.collection.immutable.{ Set => ImmutableSet}


import ch.epfl.insynth.reconstruction.combinator.SimpleNode
import ch.epfl.insynth.reconstruction.combinator.ContainerNode
import ch.epfl.insynth.reconstruction.combinator.AbsDeclaration
import ch.epfl.insynth.reconstruction.combinator.NormalDeclaration
import ch.epfl.insynth.reconstruction.combinator.{Declaration => cDeclaration}

import ch.epfl.insynth.trees.BottomType


//import ch.epfl.insynth.reconstruction.combinator.{Declaration => cDeclaration}



object TestTrees {
	val fullNameClassA = "some.package.A"

	val typeInt = Const("Int")
	val typeLong = Const("Long")
	val typeString = Const("String")
	val typeBoolean = Const("Boolean")
	val typeChar = Const("Char")
	val typeFloat = Const("Float")
	val typeDouble = Const("Double")
	val typeUnit = Const("Unit")
	
	val typeBottom = Const("$Bottom_Type_Just_For_Resolution$")
	
	private implicit def parameterToList(t: ScalaType): List[ScalaType] = List(t)
	private implicit def parameterToList(t: List[ScalaType]): List[List[ScalaType]] = List(t)
	private implicit def declarationToList(t: Declaration): List[Declaration] = List(t)
	
	import ch.epfl.insynth.trees.TypeTransformer.transform
	
	private implicit def scalaTypeToInSynthType(t: ScalaType): InSynthType = transform(t)
	
	// this **** can't work....
	type NodeMap = scala.collection.mutable.Map[ScalaType, Node]

	def main(args: Array[String]): Unit = {
	  buildCombinedSimpleTree
	}
  
	/**
	 * Constructs a simple tree (only one trivial method application).
	 * Based on the example we had when discussing.
	 */
	def buildCombinedSimpleTree = {
	  //************************************
	  // Should look like: SimpleNode(?){ query (String)->{ { SimpleNode(String){ m4 (A)->{ { SimpleNode(A){ A }}}}}}}
	  // Decals:
      // List(NormalDeclaration(Declaration(special.name.for.query,Arrow(TSet(List(Const(String))),Const($Bottom_Type_Just_For_Resolution$)),
	  // Function(List(Const(String)),Const($Bottom_Type_Just_For_Resolution$)))))
	  // Params: 
	  // Map(Const(String) -> ContainerNode(Set(SimpleNode(List(NormalDeclaration(Declaration(some.package.A.m4,Arrow(TSet(List(Const(A))),Const(String)),
	  // Method(Const(A),List(),Const(String))))),Const(String),Map(Const(A) -> ContainerNode(Set(SimpleNode(List(NormalDeclaration(
	  // Declaration(some.package.A,Const(A),Const(A)))),Const(A),Map()))))))))
	  //************************************
	  
	  //************************************
	  // Scala types
	  //************************************
	  // class A { ... }
	  val objectA = Const("A")		
	  // def m4(): String	  
	  val m4 = Method(objectA, List(), typeString)
	  // query: String → ⊥
	  val queryType = Function(typeString, typeBottom)
	  
	  // NOTE InSynth query type: Arrow(TSet(List(Const(String))),Const($Bottom_Type_Just_For_Resolution$))
	  
	  //************************************
	  // Declarations
	  //************************************
	  
	  val objectADeclaration =  Declaration(
	      "some.package.A", // full name
	      transform(objectA), // inSynth type
	      objectA // scala type
	    )
	    
	  // needs a constructor
//	  objectADeclaration.setIsApply(true)
	  
	  val m4Declaration =  Declaration(
	      "some.package.A.m4", // full name
	      transform(m4), // inSynth type
	      m4 // scala type
	    )		
	  m4Declaration.setIsMethod(true)
	  m4Declaration.setHasParentheses(true)
	  
	  // special query declaration
	  val queryDeclaration = Declaration(
	      "special.name.for.query",
	      transform(queryType),
	      queryType
	    )	  
	  
	  //************************************
	  // InSynth proof trees
	  //************************************
	  
	  // XXX Unit→String is not the same as ()→String
	  // goal:String, type:A→String
	  // expression: m4(this):String

	  //decls: List[Declaration], tpe: Type, params: Map[Type, ContainerNode]
	  
	      
	  val getStringNode = new SimpleNode(
	    List[cDeclaration](NormalDeclaration(m4Declaration)),
	    InSynthConst("String"), 
	    ImmutableMap(
          // I will get object of class A from
          transform(objectA) ->
	  	  new ContainerNode(
	  		  ImmutableSet(
	  		      new SimpleNode(
  		    		  List[cDeclaration](NormalDeclaration(objectADeclaration)), 
  		    		  transform(objectA),
  		    		  ImmutableMap() // this is the end, no further nodes
  		          )
  		      )
	        )
	      )
	    )
	  
      // goal:Bottom, type:String→⊥
      // expression: query(m4(this, Unit)):⊥
	  val query = 
	    new SimpleNode(
	  	  List[cDeclaration](NormalDeclaration(queryDeclaration)),
	  	  BottomType, 
	  	  ImmutableMap( // for each parameter type - how can we resolve it
	  	      InSynthConst("String") ->
	  	      new ContainerNode(
	  	          ImmutableSet(getStringNode)
	            )
	        ) 
	    )
	    
	  query
	}




	
}