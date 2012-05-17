package ch.epfl.test

import ch.epfl.insynth.env._
import ch.epfl.scala.trees._
import ch.epfl.insynth.trees.{ Const=>InSynthConst, Type=>InSynthType, _ }

object TreeExample {
	val fullNameClassA = "some.package.A"

	val typeInt = Const("Int")
	val typeLong = Const("Long")
	val typeString = Const("String")
	val typeBoolean = Const("Boolean")
	val typeChar = Const("Char")
	val typeUnit = Const("Unit")
	
	val typeBottom = Const("$Bottom_Type_Just_For_Resolution$")
	
	private implicit def parameterToList(t: ScalaType): List[ScalaType] = List(t)
	private implicit def parameterToList(t: List[ScalaType]): List[List[ScalaType]] = List(t)
	private implicit def declarationToList(t: Declaration): List[Declaration] = List(t)
	
	import ch.epfl.insynth.trees.TypeTransformer.transform
	
	private implicit def scalaTypeToInSynthType(t: ScalaType): InSynthType = transform(t)
	
	// this **** can't work....
	type NodeMap = Map[InSynthType, ContainerNode]

	def main(args: Array[String]): Unit = {
	  simpleTree
	}
  
	/**
	 * Constructs a simple tree (only one trivial method application).
	 * Based on the example we had when discussing.
	 */
	private def simpleTree = {
	  //************************************
	  // Goals
	  //	find expression of type: String
	  //	expression: query(m4(this))
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
	  val objectADeclaration = new Declaration(
	      "some.package.A", // full name
	      transform(objectA), // inSynth type
	      objectA // scala type
	    )
	  // needs a constructor
	  objectADeclaration.setIsApply(true)
	  
	  val m4Declaration = new Declaration(
	      "some.package.A.m4", // full name
	      transform(m4), // inSynth type
	      m4 // scala type
	    )		
	  
	  // special query declaration
	  val queryDeclaration = new Declaration(
	      "special.name.for.query",
	      transform(queryType),
	      queryType
	    )	  
	  
	  //************************************
	  // InSynth proof trees
	  //************************************
	  
	  // goal:String, type:Unit→String
	  // expression: m4(this, Unit):String
	  val getStringNode = SimpleNode(
	    m4Declaration,
	    InSynthConst("String"),
	    Map(
		  // I will get Unit from nothing
	  	  transform(typeUnit) ->
	  	  ContainerNode(
	  		  transform(typeUnit),
	  		  Set()
	  		  // can be an empty map actually?
	        ),
          // I will get object of class A from
          transform(objectA) ->
	  	  ContainerNode(
	  		  transform(typeUnit),
	  		  Set(
	  		      SimpleNode(
  		    		  objectADeclaration,
  		    		  InSynthConst("A"),
  		    		  Map() // this is the end, no further nodes
  		          )
  		      )
	        )
	      )
	    )
	  
      // goal:Bottom, type:String→⊥
      // expression: query(m4(this, Unit)):⊥
	  val query = 
	    SimpleNode(
	  	  queryDeclaration,
	  	  transform(typeBottom),
	  	  Map( // for each parameter type - how can we resolve it
	  	      InSynthConst("String") ->
	  	      ContainerNode(
	  	          InSynthConst("String"),
	  	          Set(getStringNode)
	            )
	        ) 
	    )
	}

	/**
	 * Constructs a complex tree.
	 * Based on the example we had when discussing.
	 */
	private def complexTree = {
	//***************************************************
	// Goals
	//	find expression of type: Boolean
	//	expression: query(m1(this, m2(this)), m4(this))
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
	      List( List ( Function(typeInt, typeString), typeString ), typeChar ), // parameters
	      typeBoolean // return type
		)	
	  // def m2(a: Int): String 
	  val m2 = Method(objectA, List(typeInt), typeString)
	  // def m3(a:Long): String
	  val m3 = Method(objectA, List(), typeInt)
	  // def m4(): Char
	  val m4 = Method(objectA, List(), typeChar)
	  // def m5(a: Int): Long
	  val m5 = Method(objectA, List(typeInt), typeLong)
	  // def m6(): String
	  val m6 = Method(objectA, List(), typeString)
	  // query: String → ⊥
	  val queryType = Function(typeString, typeBottom)
	  
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
	  
	  // special query declaration
	  val queryDeclaration = new Declaration(
	      "special.name.for.query",
	      queryType, queryType
	    )	  
	  
	  //************************************
	  // InSynth proof trees
	  //************************************
	  
	  // XXX found out that there is a non-needed redundancy, ContainerNode type
	  // is actually not needed?
	  
	  // goal:ClassA object, type:ClassA
	  // expression: this	  
	  val thisNode = SimpleNode(
	      objectADeclaration, objectA, Map()
      )
	    
	  // goal:Char, type:Unit→Char
	  // expression: m4(this)	  
	  val m4Node = SimpleNode(
	      m4Declaration,
	      typeChar,
	      Map(
	          transform(objectA) -> ContainerNode(transform(objectA), Set(thisNode))
          )
      )
      
      // goal:(Int→String), type:(Int→String)
	  // expression: m2(this)
	  val m2Node = SimpleNode(
	      m2Declaration, m2,
	      Map(
	          transform(objectA) -> ContainerNode(transform(objectA), Set(thisNode))
          )
      )
      
      // goal:(Int→String), type:(Unit→String)
	  // expression: (Int=>m6(this))
	  val m6Node = SimpleNode(
	      m6Declaration, m6,
	      Map(
	          transform(objectA) -> ContainerNode(transform(objectA), Set(thisNode))
          )
      )
            
      // goal: Long, type:(Int→Long)
	  // expression: m5(this, _)
	  val m5Node = SimpleNode(
	      m5Declaration,
	      transform(typeLong),
	      Map(
	          transform(objectA) -> ContainerNode(transform(objectA), Set(thisNode)),
	          transform(typeInt) -> ContainerNode(transform(typeInt), 
	          	Set( Leaf(transform(typeInt)) )
	          )
          )
      )
      
      // goal:(Int→String), type:(Long→String)
	  // expression: Int => m3(this, m5(this, _))
	  val composeNode = SimpleNode(
	      m3Declaration,
	      transform(Function(typeInt, typeString)),
	      Map(
	          transform(objectA) -> ContainerNode(transform(objectA), Set(thisNode)),
	          transform(typeLong) -> ContainerNode(transform(typeLong), Set(m5Node))
          )
      )
	    
	  // goal:Boolean, type:List((Int→String),Char)→Boolean
	  // expression: m1(this, 
      //				m2(this) |  m3(this) ∘ m5(this) | Int→m6(this), 
	  //				m4(this))	  
	  val m1Node = SimpleNode(
	      m1Declaration,
	      transform(typeBoolean),
	      Map(
	          transform(typeChar) -> ContainerNode(InSynthConst("Char"), Set(m4Node)),
	          transform(Function(typeInt, typeString)) ->
	          	ContainerNode(transform(Function(typeInt, typeString)), Set(composeNode, m2Node, m6Node)),
	          transform(objectA) -> ContainerNode(transform(objectA), Set(thisNode))
          )
      )
	  
      // goal:⊥, type:Boolean→⊥	    
      // expression: query(		m1(this,
	  //			m2(this) |  m3(this) ∘ m5(this) | Int→m6(this), 
	  //			m4(this)	)):⊥
	  val queryNode = 
	    SimpleNode(
	  	  queryDeclaration,
	  	  transform(typeBottom),
	  	  Map( // for each parameter type - how can we resolve it
	  	      transform(typeBoolean) ->
	  	      ContainerNode(
	  	          InSynthConst("Boolean"),
	  	          Set(m1Node)
	            )
	        ) 
	    )
	}
	
}