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

    
	
	def buildCombinedComplexTree = {
	//***************************************************
	// Goals
	//	find expression of type: Boolean
	//	expression: query(m1(this, m2(this), m4(this)))
	//	code:
	// 	class A {
	//  	def m1(f: Int=>String, c:Char): Boolean
	//  	def m2(a: Int): String
	//  	def m3(a: Long): String
	//  	def m4(): Char
	//  	def m5(a: Int): Long
	//  	def m6(): String
	//  	def test() {
	//    		val b:Bool = ?synthesize?
	//  	}
	//	}
	//***************************************************
	  
	  //************************************
	  // Scala types
	  //************************************
	  // class A { ... }
	  val objectA = Const("A")	
	  // def m1(f: Int=>String, c:Char): Boolean
	  val m1 = Method(
	      objectA, // receiver
	      List( List ( Function(typeInt, typeString), typeChar ) ), // parameters
	      typeBoolean // return type
		)	
	  // def m2(a: Int): String 
	  val m2 = Method(objectA, List(typeInt), typeString)
	  // def m3(a:Long): String
	  val m3 = Method(objectA, List(typeLong), typeString)
	  // def m4(): Char
	  val m4 = Method(objectA, List(), typeChar)
	  // def m5(a: Int): Long
	  val m5 = Method(objectA, List(typeInt), typeLong)
	  // def m6(): String
	  val m6 = Method(objectA, List(), typeString)
	  // query: typeBoolean → ⊥
	  val queryType = Function(typeBoolean, typeBottom)
	  
	  // NOTE InSynth query type: Arrow(TSet(List(Const(String))),Const($Bottom_Type_Just_For_Resolution$))
	  
	  //************************************
	  // Declarations
	  //************************************
	  val objectADeclaration = new Declaration(
	      fullNameClassA, // full name
	      transform(objectA), // inSynth type
	      objectA // scala type
	  )
	  // needs a constructor
	  objectADeclaration.setIsApply(true)	  
	  
	  val m1Declaration	= new Declaration(
	      fullNameClassA + ".m1",
	      transform(m1),
	      m1
	  )
	  val m2Declaration = new Declaration(
	      fullNameClassA + ".m2", // full name
	      m2, // inSynth type (implicit conversion)
	      m2 // scala type
	  )
	  val m3Declaration = new Declaration(
	      fullNameClassA + ".m3", // full name
	      m3, m3
      )
	  val m4Declaration = new Declaration(
	      fullNameClassA + ".m4", // full name
	      m4, m4
      )
	  val m5Declaration = new Declaration(
	      fullNameClassA + ".m5", // full name
	      m5, m5
      )
	  val m6Declaration = new Declaration(
	      fullNameClassA + ".m6", // full name
	      m6, m6
      )		
	  m1Declaration.setIsMethod(true)
	  m2Declaration.setIsMethod(true)
	  m3Declaration.setIsMethod(true)
	  m4Declaration.setIsMethod(true)
	  m5Declaration.setIsMethod(true)
	  m6Declaration.setIsMethod(true)
	  	  
	  m1Declaration.setHasParentheses(true)
	  m2Declaration.setHasParentheses(true)
	  m3Declaration.setHasParentheses(true)
	  m4Declaration.setHasParentheses(true)
	  m5Declaration.setHasParentheses(true)
	  m6Declaration.setHasParentheses(true)
	  
	  m1Declaration.setHasThis(false)
	  m2Declaration.setHasThis(false)
	  m3Declaration.setHasThis(false)
	  m4Declaration.setHasThis(false)
	  m5Declaration.setHasThis(false)
	  m6Declaration.setHasThis(false)
	  objectADeclaration.setIsThis(true)
	  
	  // special query declaration
	  val queryDeclaration = new Declaration(
	      "special.name.for.query",
	      queryType, queryType
	    )	  
	  
	  //************************************
	  // InSynth proof trees
	  //************************************
	  
	  // XXX found out that there is a non-needed redundancy, new ContainerNode type
	  // is actually not needed?
	  
	  // goal:ClassA object, type:ClassA
	  // expression: this	  
	  val thisNode = new SimpleNode(
	      List[cDeclaration](NormalDeclaration(objectADeclaration)), null, ImmutableMap()
      )
	    
	  // goal:Char, type:Unit→Char
	  // expression: m4(this)	  
	  val m4Node = new SimpleNode(
	      List[cDeclaration](NormalDeclaration(m4Declaration)),
	      transform(objectA), 
	      ImmutableMap(
	          transform(objectA) -> new ContainerNode(ImmutableSet(thisNode))
          )
      )
      
      // goal:(Int→String), type:(Int→String)
	  // expression: m2(this)
	  val m2Node = new SimpleNode(
	      List[cDeclaration](NormalDeclaration(m2Declaration)),
	      transform(objectA), 
	      ImmutableMap(
	          transform(objectA) -> new ContainerNode(ImmutableSet(thisNode)),
	          transform(typeInt) ->
	          	new ContainerNode(ImmutableSet(new SimpleNode(
	          	    { 
	          	      val dec = new Declaration(typeInt); dec.setIsApply(true);	List[cDeclaration](NormalDeclaration(dec))	
	          	    }, null, ImmutableMap.empty
          	    )))
          )
      )      
      
      // goal:String, type:(A→String)
	  // expression: m6(this)
	  val m6Node = new SimpleNode(
	     List[cDeclaration](NormalDeclaration(m6Declaration)),
	     transform(objectA), 
	      ImmutableMap(
	          transform(objectA) -> new ContainerNode(ImmutableSet(thisNode))
          )
      )
            
      // goal: Long, type:(Int→Long)
	  // expression: m5(this, _)
	  val m5Node = new SimpleNode(
	      List[cDeclaration](NormalDeclaration(m5Declaration)),
	      transform(objectA), 
	      ImmutableMap(
	          transform(objectA) -> new ContainerNode(ImmutableSet(thisNode)),
	          transform(typeInt) -> new ContainerNode( 
	          	ImmutableSet( new SimpleNode(
	          	    { 
	          	      val dec = new Declaration(typeInt); dec.setIsApply(true);  List[cDeclaration](NormalDeclaration(dec))	          	    	
	          	    }, null, ImmutableMap.empty
          	    ) )
	          )
          )
      )
      
      // goal:(Int→String), type:(Long→String)
	  // expression: Int => m3(this, m5(this, _))
	  val composeNode = new SimpleNode(
	      List[cDeclaration](NormalDeclaration(m3Declaration)),
	      transform(objectA),
	      ImmutableMap(
	          transform(objectA) -> new ContainerNode(ImmutableSet(thisNode)),
	          transform(typeLong) -> new ContainerNode(ImmutableSet(m5Node))
          )
      )
	    
	  // goal:Boolean, type:List((Int→String),Char)→Boolean
	  // expression: m1(this, 
      //				m2(this) |  m3(this) ∘ m5(this) | Int→m6(this), 
	  //				m4(this))	  
	  val m1Node = new SimpleNode(
	      List[cDeclaration](NormalDeclaration(m1Declaration)),
	      transform(typeChar), 
	      ImmutableMap(
	          transform(typeChar) -> new ContainerNode(ImmutableSet(m4Node)),
	          transform(Function(typeInt, typeString)) ->
	          	new ContainerNode( 
	          	    ImmutableSet(composeNode, m2Node, m6Node)
          	    ),
	          transform(objectA) -> new ContainerNode(ImmutableSet(thisNode))
          )
      )
	  
      // goal:⊥, type:Boolean→⊥	    
      // expression: query(		m1(this,
	  //			m2(this) |  m3(this) ∘ m5(this) | Int→m6(this), 
	  //			m4(this)	)):⊥
	  val queryNode = 
	    new SimpleNode(
	  	   List[cDeclaration](NormalDeclaration(queryDeclaration)),
	  	   BottomType, 
	  	   ImmutableMap( // for each parameter type - how can we resolve it
	  	      transform(typeBoolean) ->
	  	      new ContainerNode(
	  	          ImmutableSet(m1Node)
	            )
	        ) 
	    )
	    
	  queryNode
	}
	
	def buildCombinedTreeArrowType = {
	//***************************************************
	// Goals
	//	find expression of type: (Int, Int)→Char
	//	expression:
	//	code:
	//  def outside(a: Int, b:Int): Char
	// 	class A {
	//		val intVal: Int  
	//  	def m1(): ((Int, Int)=>Char)
	//  	def m2(a: Int, b:Int): Char
	//  	def m3(): Char
	//  	def test() {
	//    		val b:(Int, Int)=>Char = ?synthesize?
	//  	}
	//	}
	//***************************************************
	  
	  //************************************
	  // Scala types
	  //************************************
	  // class A { ... }
	  val objectA = Const("A")	
	  // def m1(): ((Int, Int)=>Char)
	  val m1 = Method(
	      objectA, // receiver
	      List(), // parameters
	      Function(List(typeInt, typeInt), typeChar) // return type
		)	
	  // def m2(a: Int, b:Int): Char
	  val m2 = Method(objectA, List(List(typeInt, typeInt)), typeChar)
	  // def m3(): Char
	  val m3 = Method(objectA, List(), typeChar)
	  // query: String → ⊥
	  val queryType = Function(
	    Function(List(typeInt, typeInt), typeChar),
	    typeBottom
	  )
	  // def outside(a: Int, b:Int): Char
	  val outside = Function(List(typeInt, typeInt), typeChar)
	  // val intVal: Int
	  val intVal = typeInt
	  	  	  
	  //************************************
	  // Declarations
	  //************************************
	  val objectADeclaration = Declaration(
	      fullNameClassA, // full name
	      transform(objectA), // inSynth type
	      objectA // scala type
	    )
	  // needs a constructor
	  objectADeclaration.setIsApply(true)	  
	  
	  val m1Declaration	= Declaration(
	      fullNameClassA + ".m1",
	      // XXX
	      m1,
	      //Arrow(TMutableSet(objectA), Function(List(typeInt, typeInt), typeChar)),
	      m1
	  )
	  val m2Declaration = Declaration(
	      fullNameClassA + ".m2", // full name
	      m2, // inSynth type (implicit conversion)
	      m2 // scala type
	  )
	  val m3Declaration = Declaration(
	      fullNameClassA + ".m3", // full name
	      m3, m3
      )	  
            
	  m1Declaration.setIsMethod(true)
	  m2Declaration.setIsMethod(true)
	  m3Declaration.setIsMethod(true)
	  	  
	  m1Declaration.setHasParentheses(true)
	  m2Declaration.setHasParentheses(true)
	  m3Declaration.setHasParentheses(true)
	  
	  // special query declaration
	  val queryDeclaration = Declaration(
	      "special.name.for.query",
	      queryType, queryType
	    )	  	
	  
	  val outsideDeclaration = Declaration(
	      "outside",
	      outside, outside
      )	 
	  val intValDeclaration = Declaration(
	      "A.intVal",
	      intVal, intVal
      )	 
            
	  val leafIntDeclaration = new Declaration(typeInt)
	  
	  //************************************
	  // InSynth proof trees
	  //************************************	  
	  
	  // goal:A, type: A
	  // expression: d.fullname
	  val thisNode = new SimpleNode(
	      List[cDeclaration](NormalDeclaration(objectADeclaration)),
	      transform(objectA),
	      ImmutableMap()
      )
      
      // goal:Int, type:Int
	  // expression: A.intVal	  
	  val intValNode = new SimpleNode(
	      List[cDeclaration](NormalDeclaration(intValDeclaration)),
	      typeInt,
	      ImmutableMap()
      )
	  
	  // goal:(Int→Char), type:A→Int→Char
	  // expression: (Int,Int) → m1(this)(_, _)	  
	  val m1Node = new SimpleNode(
	      List[cDeclaration](NormalDeclaration(m1Declaration)), 
	      transform(objectA), 
	      ImmutableMap(
	          transform(objectA) ->new ContainerNode(ImmutableSet(thisNode)),
	          transform(typeInt) -> new ContainerNode(
	              ImmutableSet(new SimpleNode(List[cDeclaration](NormalDeclaration(leafIntDeclaration)), transform(objectA), ImmutableMap.empty), intValNode))
	              
          )
      )
      
      // goal:(Int→Char), type:(Int→Char)
	  // expression: d.fullName ("outside")	  
	  val outsideNode = new SimpleNode(
	      List[cDeclaration](NormalDeclaration(outsideDeclaration)),
	      transform(typeInt), 
	      ImmutableMap(
	          transform(typeInt) -> new ContainerNode(
	              ImmutableSet(new SimpleNode(List[cDeclaration](NormalDeclaration(leafIntDeclaration)), transform(typeInt), ImmutableMap.empty), intValNode))
          )
      )
      
      // goal:(Char), type:(A→Char)
	  // expression: (Int,Int)→m3(A)	  
	  val m3Node = new SimpleNode(
	      List[cDeclaration](NormalDeclaration(m3Declaration)),
	      transform(objectA), 
	      ImmutableMap(
	        transform(objectA) -> 
	          new ContainerNode(ImmutableSet(thisNode))
          )
      )
      
      // goal:(Int→Char), type:((Int,A)→Char)
	  // expression: (Int, Int) → m2(this, _, _)	  
	  val m2Node = new SimpleNode(
	      List[cDeclaration](NormalDeclaration(m2Declaration)),
	      transform(typeInt),  
	      ImmutableMap(
	        transform(typeInt) -> 
        	  new ContainerNode(
	              ImmutableSet(new SimpleNode(List[cDeclaration](NormalDeclaration(leafIntDeclaration)), transform(typeInt), ImmutableMap.empty), intValNode)),
	        transform(objectA) ->
        	  new ContainerNode(ImmutableSet(thisNode))
          )
      )     
	  
      // goal:⊥, type:(Int→Char)→⊥	    
      // expression: query	(		
      //	(Int,Int) -> m1(this)(_,_) | (Int,Int) -> m1(this)(intVal, intVal)
	  //	(Int,Int) -> m2(this,_,_) | m2(this, intVal, intVal)
      //	(Int,Int) -> m3(this) | outside
      //					):⊥
	  val queryNode = 
	    new SimpleNode(
	  	  List[cDeclaration](NormalDeclaration(queryDeclaration)),
	  	  BottomType, //TODO
	  	  ImmutableMap( // for each parameter type - how can we resolve it
	  	      transform(Function(List(typeInt, typeInt), typeChar)) ->
	  	      new ContainerNode(
	  	          ImmutableSet(m1Node, outsideNode, m2Node, m3Node)
	            )
	        ) 
	    )
      queryNode
	}
	



	
}