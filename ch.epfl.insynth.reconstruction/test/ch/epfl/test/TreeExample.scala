package ch.epfl.test

import ch.epfl.insynth.env._
import ch.epfl.scala.trees._
import ch.epfl.insynth.trees.{ Const=>InSynthConst, Type=>InSynthType, _ }
import scala.collection.mutable.{ Map => MutableMap }
import scala.collection.mutable.{ Set => MutableSet }

object TreeExample {
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
	  buildSimpleTree
	}
  
	/**
	 * Constructs a simple tree (only one trivial method application).
	 * Based on the example we had when discussing.
	 */
	def buildSimpleTree = {
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
	  m4Declaration.setIsMethod(true)
	  
	  // special query declaration
	  val queryDeclaration = new Declaration(
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
	  val getStringNode = new SimpleNode(
	    m4Declaration,
	    MutableMap(
          // I will get object of class A from
          transform(objectA) ->
	  	  new ContainerNode(
	  		  MutableSet(
	  		      new SimpleNode(
  		    		  objectADeclaration,
  		    		  MutableMap() // this is the end, no further nodes
  		          )
  		      )
	        )
	      )
	    )
	  
      // goal:Bottom, type:String→⊥
      // expression: query(m4(this, Unit)):⊥
	  val query = 
	    new SimpleNode(
	  	  queryDeclaration,
	  	  MutableMap( // for each parameter type - how can we resolve it
	  	      InSynthConst("String") ->
	  	      new ContainerNode(
	  	          MutableSet(getStringNode)
	            )
	        ) 
	    )
	    
	  query
	}

	/**
	 * Constructs a complex tree.
	 * Based on the example we had when discussing.
	 */
	def buildComplexTree = {
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
	      objectADeclaration, MutableMap()
      )
	    
	  // goal:Char, type:Unit→Char
	  // expression: m4(this)	  
	  val m4Node = new SimpleNode(
	      m4Declaration,
	      MutableMap(
	          transform(objectA) -> new ContainerNode(MutableSet(thisNode))
          )
      )
      
      // goal:(Int→String), type:(Int→String)
	  // expression: m2(this)
	  val m2Node = new SimpleNode(
	      m2Declaration,
	      MutableMap(
	          transform(objectA) -> new ContainerNode(MutableSet(thisNode)),
	          transform(typeInt) ->
	          	new ContainerNode(MutableSet(new SimpleNode(
	          	    { 
	          	      val dec = new Declaration(typeInt); dec.setIsApply(true); dec	          	    	
	          	    }, MutableMap.empty
          	    )))
          )
      )      
      
      // goal:String, type:(A→String)
	  // expression: m6(this)
	  val m6Node = new SimpleNode(
	      m6Declaration,
	      MutableMap(
	          transform(objectA) -> new ContainerNode(MutableSet(thisNode))
          )
      )
            
      // goal: Long, type:(Int→Long)
	  // expression: m5(this, _)
	  val m5Node = new SimpleNode(
	      m5Declaration,
	      MutableMap(
	          transform(objectA) -> new ContainerNode(MutableSet(thisNode)),
	          transform(typeInt) -> new ContainerNode( 
	          	MutableSet( new SimpleNode(
	          	    { 
	          	      val dec = new Declaration(typeInt); dec.setIsApply(true); dec	          	    	
	          	    }, MutableMap.empty
          	    ) )
	          )
          )
      )
      
      // goal:(Int→String), type:(Long→String)
	  // expression: Int => m3(this, m5(this, _))
	  val composeNode = new SimpleNode(
	      m3Declaration,
	      MutableMap(
	          transform(objectA) -> new ContainerNode(MutableSet(thisNode)),
	          transform(typeLong) -> new ContainerNode(MutableSet(m5Node))
          )
      )
	    
	  // goal:Boolean, type:List((Int→String),Char)→Boolean
	  // expression: m1(this, 
      //				m2(this) |  m3(this) ∘ m5(this) | Int→m6(this), 
	  //				m4(this))	  
	  val m1Node = new SimpleNode(
	      m1Declaration,
	      MutableMap(
	          transform(typeChar) -> new ContainerNode(MutableSet(m4Node)),
	          transform(Function(typeInt, typeString)) ->
	          	new ContainerNode( 
	          	    MutableSet(composeNode, m2Node, m6Node)
          	    ),
	          transform(objectA) -> new ContainerNode(MutableSet(thisNode))
          )
      )
	  
      // goal:⊥, type:Boolean→⊥	    
      // expression: query(		m1(this,
	  //			m2(this) |  m3(this) ∘ m5(this) | Int→m6(this), 
	  //			m4(this)	)):⊥
	  val queryNode = 
	    new SimpleNode(
	  	  queryDeclaration,
	  	  MutableMap( // for each parameter type - how can we resolve it
	  	      transform(typeBoolean) ->
	  	      new ContainerNode(
	  	          MutableSet(m1Node)
	            )
	        ) 
	    )
	    
	  queryNode
	}
	
	/**
	 * Constructs a tree with an arrow goal type.
	 */
	def buildTreeArrowType = {
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
	  val m2 = Method(objectA, List(typeInt, typeInt), typeChar)
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
	      objectADeclaration,
	      MutableMap()
      )
      
      // goal:Int, type:Int
	  // expression: A.intVal	  
	  val intValNode = new SimpleNode(
	      intValDeclaration,
	      MutableMap()
      )
	  
	  // goal:(Int→Char), type:A→Int→Char
	  // expression: (Int,Int) → m1(this)(_, _)	  
	  val m1Node = new SimpleNode(
	      m1Declaration,
	      MutableMap(
	          transform(objectA) -> new ContainerNode(MutableSet(thisNode)),
	          transform(typeInt) -> new ContainerNode(
	              MutableSet(new SimpleNode(leafIntDeclaration, MutableMap.empty), intValNode))
          )
      )
      
      // goal:(Int→Char), type:(Int→Char)
	  // expression: d.fullName ("outside")	  
	  val outsideNode = new SimpleNode(
	      outsideDeclaration,
	      MutableMap(
	          transform(typeInt) -> new ContainerNode(
	              MutableSet(new SimpleNode(leafIntDeclaration, MutableMap.empty), intValNode))
          )
      )
      
      // goal:(Char), type:(A→Char)
	  // expression: (Int,Int)→m3(A)	  
	  val m3Node = new SimpleNode(
	      m3Declaration,
	      MutableMap(
	        transform(objectA) -> 
	          new ContainerNode(MutableSet(thisNode))
          )
      )
      
      // goal:(Int→Char), type:((Int,A)→Char)
	  // expression: (Int, Int) → m2(this, _, _)	  
	  val m2Node = new SimpleNode(
	      m2Declaration,
	      MutableMap(
	        transform(typeInt) -> 
        	  new ContainerNode(MutableSet(new SimpleNode(leafIntDeclaration, MutableMap.empty), intValNode)),
	        transform(objectA) ->
        	  new ContainerNode(MutableSet(thisNode))
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
	  	  queryDeclaration,
	  	  MutableMap( // for each parameter type - how can we resolve it
	  	      transform(Function(List(typeInt, typeInt), typeChar)) ->
	  	      new ContainerNode(
	  	          MutableSet(m1Node, outsideNode, m2Node, m3Node)
	            )
	        ) 
	    )
      queryNode
	}
	
	/**
	 * Constructs a tree in which expression can be synthesized as a return one
	 * but also as a parameter to one of the methods
	 */
	def buildTreeOverlapParameterTypeWithReturnType = {
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
	  val m2 = Method(objectA, List(typeInt, typeInt), typeChar)
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
	      //Arrow(TSet(objectA), Function(List(typeInt, typeInt), typeChar)),
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
	      objectADeclaration,
	      MutableMap()
      )
      
      // goal:Int, type:Int
	  // expression: A.intVal	  
	  val intValNode = new SimpleNode(
	      intValDeclaration,
	      MutableMap()
      )
	  
	  // goal:(Int→Char), type:A→Int→Char
	  // expression: (Int,Int) → m1(this)(_, _)	  
	  val m1Node = new SimpleNode(
	      m1Declaration,
	      MutableMap(
	          transform(objectA) -> new ContainerNode(MutableSet(thisNode)),
	          transform(typeInt) -> new ContainerNode(
	              MutableSet(new SimpleNode(leafIntDeclaration, MutableMap.empty), intValNode))
          )
      )
      
      // goal:(Int→Char), type:(Int→Char)
	  // expression: d.fullName ("outside")	  
	  val outsideNode = new SimpleNode(
	      outsideDeclaration,
	      MutableMap(
	          transform(typeInt) -> new ContainerNode(
	              MutableSet(new SimpleNode(leafIntDeclaration, MutableMap.empty), intValNode))
          )
      )
      
      // goal:(Char), type:(A→Char)
	  // expression: (Int,Int)→m3(A)	  
	  val m3Node = new SimpleNode(
	      m3Declaration,
	      MutableMap(
	        transform(objectA) -> 
	          new ContainerNode(MutableSet(thisNode))
          )
      )
      
      // goal:(Int→Char), type:((Int,A)→Char)
	  // expression: (Int, Int) → m2(this, _, _)	  
	  val m2Node = new SimpleNode(
	      m2Declaration,
	      MutableMap(
	        transform(typeInt) -> 
        	  new ContainerNode(MutableSet(new SimpleNode(leafIntDeclaration, MutableMap.empty), intValNode)),
	        transform(objectA) ->
        	  new ContainerNode(MutableSet(thisNode))
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
	  	  queryDeclaration,
	  	  MutableMap( // for each parameter type - how can we resolve it
	  	      transform(Function(List(typeInt, typeInt), typeChar)) ->
	  	      new ContainerNode(
	  	          MutableSet(m1Node, outsideNode, m2Node, m3Node)
    		  )
	        ) 
	    )
      queryNode
	}	
	
	/**
	 * Small example that uses function parameter application
	 */
	def buildTreeAbsApplication = {
	//***************************************************
	// Goals
	//	find expression of type: (Int→Char, Int)→Char
	//	expression:
	//	code:
	//  	def test() {
	//    		val b:(Int→Char)→Char = ?synthesize?
	//  	}
	//***************************************************
	  
	  //************************************
	  // Scala types
	  //************************************
	  
	  // query: (Int→Char)→Char → ⊥
	  val queryType = Function(
	    Function(List(Function(typeInt, typeChar), typeInt), typeChar),
	    typeBottom
	  )
	  
	  val intLeafNode = new SimpleNode(new Declaration(typeInt), MutableMap.empty)
	  	  	  
	  //************************************
	  // Declarations
	  //************************************
	 	  
	  // special query declaration
	  val queryDeclaration = Declaration(
	      "special.name.for.query",
	      queryType, queryType
	    )	  
	  
	  //************************************
	  // InSynth proof trees
	  //************************************	
      
      // goal:(Int→Char, Int)→Char, type:Char
	  // expression: (Int, Int) → m2(this, _, _)	  
	  val absNode = new SimpleNode(
	      new Declaration(Function(typeInt, typeChar)),
	      MutableMap(
	        transform(typeInt) -> 
        	  new ContainerNode(MutableSet(intLeafNode))
          )
      )     
	  
      // goal:⊥, type:(Int→Char, Int)→Char→⊥	    
      // expression: query	(		
      //	(Int→Char, Int) -> _(_)
      //					):⊥
	  val queryNode = 
	    new SimpleNode(
	  	  queryDeclaration,
	  	  MutableMap( // for each parameter type - how can we resolve it
	  	      transform(Function(List(Function(typeInt, typeChar), typeInt), typeChar)) ->
	  	      new ContainerNode(
	  	          MutableSet(absNode)
	            )
	        ) 
	    )
      queryNode
	}	
	
	/**
	 * Small example that uses function parameter application
	 */
	def buildTreeSKombinator = {
	//***************************************************
	// Goals
	//	find expression of type: (Int→(Char→String)) → (Int→Char) → Int→String
	//	expression:
	//	code:
	//  	def test() {
	//    		val b:(Int=>(Char=>String))=>(Int=>Char)=>Int=>String = ?synthesize?
	//  	}
	//***************************************************
	  
	  //************************************
	  // Scala types
	  //************************************
	  
	  val sKombType = 
	    Function(
          List(Function(typeInt, Function(typeChar, typeString))),
          Function(
            Function(typeInt, typeChar),
            Function(typeInt, typeString)
          )
		)
	  
	  // query: (Int→(Char→String)) → (Int→Char) → Int→String → ⊥
	  val queryType = Function(
	    sKombType,
	    typeBottom
	  )
	  	  	  
	  //************************************
	  // Declarations
	  //************************************
	 	  
	  // special query declaration
	  val queryDeclaration = Declaration(
	      "special.name.for.query",
	      queryType, queryType
	    )	  
	  
	  //************************************
	  // InSynth proof trees
	  //************************************	
      
	  val intLeafNode = new SimpleNode(new Declaration(typeInt), MutableMap.empty)
	  
      // TODO
      // goal:(Int→Char, Int)→Char, type:Char
	  // expression: (Int, Int) → m2(this, _, _)	????  
	  val absNode2 = new SimpleNode(
	      new Declaration(Function(List(typeInt), typeChar)),
	      MutableMap(
	        transform(typeInt) -> 
        	  new ContainerNode(MutableSet(intLeafNode))     	  
          )
      )     
	    
      // TODO
      // goal:(Int→Char, Int)→Char, type:Char
	  // expression: (Int, Int) → m2(this, _, _)	????  
	  val absNode1 = new SimpleNode(
	      new Declaration(Function(typeInt, Function(typeChar, typeString))),
	      MutableMap(
	        transform(typeInt) -> 
        	  new ContainerNode(MutableSet(intLeafNode)),
        	transform(typeChar) ->
        	  new ContainerNode(MutableSet(absNode2))        	  
          )
      )     

      // TODO      
      // goal:⊥, type:(Int→Char, Int)→Char→⊥	    
      // expression: query	(		
      //	(Int→Char, Int) -> _(_)
      //					):⊥????
	  val queryNode = 
	    new SimpleNode(
	  	  queryDeclaration,
	  	  MutableMap( // for each parameter type - how can we resolve it
	  	      transform(sKombType) ->
	  	      new ContainerNode(
	  	          MutableSet(absNode1)
	            )
	        ) 
	    )
      queryNode
	}	
	
	
	/**
	 * Constructs a simple tree which has calls to various variants of application
	 * (method, function, constructor)
	 */
	def buildTreeWithVariousFunctions = {
	  //************************************
	  // Goals
	  //	find expression of type: String
	  //	expression: query(new (this.m(), this.bla))
	  //************************************
	  
	  //************************************
	  // Scala types
	  //************************************
	  // class A { ... }
	  val objectA = Const("A")		
	  // class B { ... }
	  val objectB = Const("B")	
	  // constructor B(String, Int)
	  val constructB = Method(null, List(typeString, typeInt), objectB)
	  // def m(): String	  
	  val m = Method(objectA, List(), typeString)
	  // int field
	  val intField = Method(objectA, List(), typeInt)
	  // query: String → ⊥
	  val queryType = Function(objectB, typeBottom)
	  
	  // NOTE InSynth query type:
	  // Arrow(TSet(List(Const(B))),Const($Bottom_Type_Just_For_Resolution$))
	  
	  //************************************
	  // Declarations
	  //************************************
	  val objectADeclaration = new Declaration(
	      "this", // full name
	      transform(objectA), // inSynth type
	      objectA // scala type
	    )
	  // needs a constructor
	  
	  val mDeclaration = new Declaration(
	      "some.package.A.m", // full name
	      transform(m), // inSynth type
	      m // scala type
	    )		
	  mDeclaration.setIsMethod(true)
	  mDeclaration.setIsThis(false)
	  
	  val constructBDeclaration = new Declaration(
	      "some.package.B", constructB, constructB
      )
	  constructBDeclaration.setIsConstructor(true)
	  	  
	  val intValDeclaration = Declaration(
	      "A.intVal",
	      intField, intField
      )	 
      intValDeclaration.setIsField(true)
	  mDeclaration.setIsThis(true)
	  
	  // special query declaration
	  val queryDeclaration = new Declaration(
	      "special.name.for.query",
	      transform(queryType),
	      queryType
	    )	  
	  
	  //************************************
	  // InSynth proof trees
	  //************************************
	  	  
	  val thisNode = new SimpleNode(
	      objectADeclaration,
	      MutableMap()
      )
	  
	  // goal:String, type:String, →B
	  // expression: new (this.m(), this.bla)
	  val getBNode = new SimpleNode(
	    constructBDeclaration,
	    MutableMap(
          // I will get object of class A from
          transform(typeString) ->
	  	  new ContainerNode(
	  		  MutableSet(
	  		      new SimpleNode(
  		    		  mDeclaration,
  		    		  MutableMap( transform(objectA) -> new ContainerNode(MutableSet( thisNode )))
  		          )
  		      )
	        ),
          transform(typeInt) ->
	  	  new ContainerNode(
	  		  MutableSet(
	  		      new SimpleNode(
  		    		  intValDeclaration,
  		    		  MutableMap( transform(objectA) -> new ContainerNode(MutableSet( thisNode )))  		    		  
  		          )
  		      )
	        )
	      )
	    )
	  
      // goal:Bottom, type:B→⊥
      // expression: query(new (this.m(), this.bla)):⊥
	  val query = 
	    new SimpleNode(
	  	  queryDeclaration,
	  	  MutableMap( // for each parameter type - how can we resolve it
	  	      transform(objectB) ->
	  	      new ContainerNode(
	  	          MutableSet(getBNode)
	            )
	        ) 
	    )
	    
	  query
	}
		
	/**
	 * Constructs a simple tree which has calls to curried methods
	 */
	def buildTreeWithCurryingFunctions = {
	  //************************************
	  // Goals
	  //	find expression of type: String
	  //	expression: query(this.m(intVal)(intVal))
	  //************************************
	  
	  // NOTE what about curried local functions?
	  def curriedFun(i: Int)(c: Char):Unit = {}
	  
	  //************************************
	  // Scala types
	  //************************************
	  // class A { ... }
	  val objectA = Const("A")
	  // def m(): String	  
	  val m = Method(objectA, List(List(typeInt), List(typeInt)), typeString)
	  // query: String → ⊥
	  val queryType = Function(typeString, typeBottom)
	  
	  // NOTE InSynth query type:
	  // Arrow(TSet(List(typeString)),Const($Bottom_Type_Just_For_Resolution$))
	  
	  //************************************
	  // Declarations
	  //************************************
	  val objectADeclaration = new Declaration(
	      "this", // full name
	      transform(objectA), // inSynth type
	      objectA // scala type
	    )
	  
	  val mDeclaration = new Declaration(
	      "some.package.A.m", // full name
	      transform(m), // inSynth type
	      m // scala type
	    )		
	  mDeclaration.setIsMethod(true)
	  	  	  
	  val intValDeclaration = Declaration(
	      "intVal",
	      typeInt, typeInt
      )	 
      intValDeclaration.setIsLocal(true)
	  
	  // special query declaration
	  val queryDeclaration = new Declaration(
	      "special.name.for.query",
	      transform(queryType),
	      queryType
	    )	  
	  
	  //************************************
	  // InSynth proof trees
	  //************************************
	  	  
	  val thisNode = new SimpleNode(
	      objectADeclaration,
	      MutableMap()
      )
      
      val intNode = new SimpleNode(
          intValDeclaration,
          MutableMap()
      )
	  
	  val getStringNode = new SimpleNode(
	    mDeclaration,
	    MutableMap(
          // I will get object of class A from
          transform(typeInt) ->
	  	  new ContainerNode(
	  		  MutableSet(intNode)
	        ),
          transform(objectA) ->
	  	  new ContainerNode(
	  		  MutableSet(thisNode)
	        )
	      )
	    )
	  
	  val query = 
	    new SimpleNode(
	  	  queryDeclaration,
	  	  MutableMap( // for each parameter type - how can we resolve it
	  	      transform(typeString) ->
	  	      new ContainerNode(
	  	          MutableSet(getStringNode)
	            )
	        ) 
	    )
	    
	  query
	}
	
	/**
	 * Constructs a simple tree which has cycles
	 */
	def buildTreeCycles = {
	  //************************************
	  // Goals
	  //	find expression of type: String
	  //	expression: query(intVal | f(intVal) | f(f(intVal)) | ... )
	  //************************************
	  	  
	  //************************************
	  // Scala types
	  //************************************
	  // def f(): Int=>Int	  
	  val f = Function(List(typeInt), typeInt)
	  // query: Int → ⊥
	  val queryType = Function(typeInt, typeBottom)
	  
	  // NOTE InSynth query type:
	  // Arrow(TMutableSet(List(typeInt)),Const($Bottom_Type_Just_For_Resolution$))
	  
	  //************************************
	  // Declarations
	  //************************************	  
	  val fDeclaration = new Declaration(
	      "some.package.f", // full name
	      transform(f), // inSynth type
	      f // scala type
	    )		
	  fDeclaration.setIsMethod(false)
	  fDeclaration.setIsLocal(true)
	  	  	  
	  val intValDeclaration = Declaration(
	      "intVal",
	      typeInt, typeInt
      )	 
      intValDeclaration.setIsLocal(true)
	  
	  // special query declaration
	  val queryDeclaration = new Declaration(
	      "special.name.for.query",
	      transform(queryType),
	      queryType
	    )	  
	  
	  //************************************
	  // InSynth proof trees
	  //************************************
	  	        
      val intNode = new SimpleNode(
          intValDeclaration,
          MutableMap()
      )
	  
	  val getIntNode:SimpleNode = new SimpleNode(
	    fDeclaration,
	    MutableMap()
	  )
	  
	  getIntNode.getParams +=
	    (
          transform(typeInt) ->
	  	  new ContainerNode(
	  		  MutableSet(intNode, getIntNode)
	        )
	    )
	  
//	  lazy val getIntNodeRec = new SimpleNode(
//	    fDeclaration,
//	    MutableMap(
//          transform(typeInt) ->
//	  	  new ContainerNode(
//	  		  MutableSet(getIntNode)
//	        )
//	    )
//	  )
	  	  
	  val query = 
	    new SimpleNode(
	  	  queryDeclaration,
	  	  MutableMap( // for each parameter type - how can we resolve it
	  	      transform(typeInt) ->
	  	      new ContainerNode(
	  	          MutableSet(getIntNode)
	            )
	        ) 
	    )
	    
	  query
	}
	
	/**
	 * Constructs a simple tree which has prints without "this" keyword
	 */
	def buildTreeWithoutThis = {
	  //************************************
	  // Goals
	  //	find expression of type: String
	  //	expression: query([this].m1(this.m2, this.f1, [this].f2))
	  //************************************
	  
	  //************************************
	  // Scala types
	  //************************************
	  // class A { ... }
	  val objectA = Const("A")		
	  // def m1(Char, Int, Float): String	  
	  val m1 = Method(objectA, List(List(typeChar, typeInt, typeFloat)), typeString)
	  // int field
	  val intField = Method(objectA, List(), typeInt)
	  // float field
	  val floatField = Method(objectA, List(), typeFloat)
	  // def m2(): Char
	  val m2 = Method(objectA, List(), typeChar)
	  // query: String → ⊥
	  val queryType = Function(typeString, typeBottom)
	  
	  //************************************
	  // Declarations
	  //************************************
	  val objectADeclaration = new Declaration(
	      "this", // full name
	      transform(objectA), // inSynth type
	      objectA // scala type
	    )
	  objectADeclaration.setIsThis(true)
	  
	  val m1Declaration = new Declaration(
	      "some.package.A.m1", // full name
	      transform(m1), // inSynth type
	      m1 // scala type
	    )		
	  m1Declaration.setIsMethod(true)
	  m1Declaration.setIsThis(false)
	  m1Declaration.setHasThis(false)
	  
	  val m2Declaration = new Declaration(
	      "some.package.A.m2", // full name
	      transform(m2), // inSynth type
	      m2 // scala type
	    )		
	  m2Declaration.setIsMethod(true)
	  m2Declaration.setHasThis(false)
	  	  	  
	  val intFieldDeclaration = Declaration(
	      "A.f1",
	      intField, intField
      )	 
      intFieldDeclaration.setIsField(true)
      intFieldDeclaration.setHasThis(true)
      
	  val floatFieldDeclaration = Declaration(
	      "A.f2",
	      floatField, floatField
      )	 
      floatFieldDeclaration.setIsField(true)
      floatFieldDeclaration.setHasThis(false)
	  
	  // special query declaration
	  val queryDeclaration = new Declaration(
	      "special.name.for.query",
	      transform(queryType),
	      queryType
	    )	  
	  queryDeclaration.setIsQuery(true)
	  
	  //************************************
	  // InSynth proof trees
	  //************************************
	  	  
	  val thisNode = new SimpleNode(
	      objectADeclaration,
	      MutableMap()
      )
	  
	  val m1Node = new SimpleNode(
	    m1Declaration,
	    MutableMap(
          transform(objectA) ->
	  	  new ContainerNode(
	  		  MutableSet( thisNode )
	        ),
          transform(typeInt) ->
	  	  new ContainerNode(
	  		  MutableSet(
	  		      new SimpleNode(
  		    		  intFieldDeclaration,
  		    		  MutableMap( transform(objectA) -> new ContainerNode(MutableSet( thisNode )))  		    		  
  		          )
  		      )
	        ),
          transform(typeFloat) ->
	  	  new ContainerNode(
	  		  MutableSet(
	  		      new SimpleNode(
  		    		  floatFieldDeclaration,
  		    		  MutableMap( transform(objectA) -> new ContainerNode(MutableSet( thisNode )))  		    		  
  		          )
  		      )
	        ),
          transform(typeChar) ->
	  	  new ContainerNode(
	  		  MutableSet(
	  		      new SimpleNode(
  		    		  m2Declaration,
  		    		  MutableMap( transform(objectA) -> new ContainerNode(MutableSet( thisNode )))  		    		  
  		          )
  		      )
	        )
	      )
	    )
	  
      // goal:Bottom, type:B→⊥
      // expression: query(new (this.m(), this.bla)):⊥
	  val query = 
	    new SimpleNode(
	  	  queryDeclaration,
	  	  MutableMap( // for each parameter type - how can we resolve it
	  	      transform(typeString) ->
	  	      new ContainerNode(
	  	          MutableSet(m1Node)
	            )
	        ) 
	    )
	    
	  query
	}
	
	
	/**
	 * Constructs a simple tree which has prints without "this" keyword
	 */
	def buildTreeIdentityFunction = {
	  //************************************
	  // Goals
	  //	find expression of type: Int=>Int
	  //	expression: query(x:Int => Int)
	  //************************************
	  
	  //************************************
	  // Scala types
	  //************************************
	  
	  val neededType = Function(typeInt, typeInt)
		
	  val queryType = Function(neededType, typeBottom)
	  
	  
	  //************************************
	  // Declarations
	  //************************************
	  	  
	  // special query declaration
	  val queryDeclaration = new Declaration(
	      "special.name.for.query",
	      transform(queryType),
	      queryType
	    )	  
	  queryDeclaration.setIsQuery(true)
	  
	  //************************************
	  // InSynth proof trees
	  //************************************
	  	  	  
	  val intLeafNode = new SimpleNode(new Declaration(typeInt), MutableMap.empty)

	  val absNode = new SimpleNode(
	      new Declaration(Function(typeInt, typeInt)),
	      MutableMap(
	        transform(typeInt) -> 
        	  new ContainerNode(MutableSet(intLeafNode))      	  
          )
      )     
	  
	  val query = 
	    new SimpleNode(
	  	  queryDeclaration,
	  	  MutableMap( // for each parameter type - how can we resolve it
	  	      transform(Function(typeInt, typeInt)) ->
	  	      new ContainerNode(
	  	          MutableSet(intLeafNode)
	            )
	        ) 
	    )
	    
	  query
	}

	/**
	 * Constructs a simple tree which has constructor with no parameters in two contexts
	 * should output parentheses only in one case
	 */
	def buildTreeWithConstructors = {
	  //************************************
	  // Goals
	  //	find expression of type: String
	  //	expression: query(f(new A().f, new A))
	  //************************************
	  
	  //************************************
	  // Scala types
	  //************************************
	  // class A { ... }
	  val objectA = Const("A")		
	  // constructor A()
	  val constructA = Method(null, List(), objectA)
	  // def f(Int, A): String	  
	  val f = Function(List(typeInt, objectA), typeString)
	  // int field
	  val intField = Method(objectA, List(), typeInt)
	  // query: String → ⊥
	  val queryType = Function(typeString, typeBottom)
	  
	  
	  //************************************
	  // Declarations
	  //************************************
	  	  
	  val fDeclaration = new Declaration(
	      "some.package.A.f", // full name
	      transform(f), // inSynth type
	      f // scala type
	    )		
	  fDeclaration.setIsMethod(false)
	  fDeclaration.setIsThis(false)
	  
	  val constructADeclaration = new Declaration(
	      "some.package.A", constructA, constructA
      )
	  constructADeclaration.setIsConstructor(true)
	  	  
	  val intValDeclaration = Declaration(
	      "A.intVal",
	      intField, intField
      )	 
      intValDeclaration.setIsField(true)
	  intValDeclaration.setIsThis(false)
	  
	  // special query declaration
	  val queryDeclaration = new Declaration(
	      "special.name.for.query",
	      transform(queryType),
	      queryType
	    )	  
	  
	  //************************************
	  // InSynth proof trees
	  //************************************
	  	              
	  val constructANode = new SimpleNode( 
	      constructADeclaration,
	      MutableMap()
      )
      
	  val getStringNode = new SimpleNode(
	    fDeclaration,
	    MutableMap(
          transform(objectA) ->
	  	  new ContainerNode(
	  		  MutableSet(
	  		      constructANode
  		      )
	        ),
          transform(typeInt) ->
	  	  new ContainerNode(
	  		  MutableSet(
	  		      new SimpleNode(
  		    		  intValDeclaration,
  		    		  MutableMap( transform(objectA) -> new ContainerNode(MutableSet( constructANode )))  		    		  
  		          )
  		      )
	        )
	      )
	    )
	  
	  val query = 
	    new SimpleNode(
	  	  queryDeclaration,
	  	  MutableMap(
	  	      transform(typeString) ->
	  	      new ContainerNode(
	  	          MutableSet(getStringNode)
	            )
	        ) 
	    )
	    
	  query
	}
	
	/**
	 * Constructs a simple tree which has two methods with same InSynth type but
	 * different number of parameters (and weights sum)
	 */
	def buildSameInSynthDifferentWeight = {
	  //************************************
	  // Goals
	  //	find expression of type: String
	  //	expression: query(f1(i), f2(i, i))
	  //************************************
	  
	  //************************************
	  // Scala types
	  //************************************
	  // def f1(Int): String	  
	  val f1 = Function(List(typeInt), typeString)
	  // def f2(Int, Int): String	  
	  val f2 = Function(List(typeInt, typeInt), typeString)
	  // query: String → ⊥
	  val queryType = Function(typeString, typeBottom)
	  
	  
	  //************************************
	  // Declarations
	  //************************************
	  	  
	  val f1Declaration = new Declaration(
	      "f1", // full name
	      transform(f1), // inSynth type
	      f1 // scala type
	    )		
	  f1Declaration.setIsMethod(false)
	  f1Declaration.setIsThis(false)
	  
	  val f2Declaration = new Declaration(
	      "f2", // full name
	      transform(f2), // inSynth type
	      f2 // scala type
	    )		
	  f2Declaration.setIsMethod(false)
	  f2Declaration.setIsThis(false)
	  
	  val intValDeclaration = Declaration(
	      "intVal",
	      typeInt, typeInt
      )	 
      intValDeclaration.setIsLocal(true)	  	  
	  
	  // special query declaration
	  val queryDeclaration = new Declaration(
	      "special.name.for.query",
	      transform(queryType),
	      queryType
	    )	  
	  
	  //************************************
	  // InSynth proof trees
	  //************************************
	  	        
	  val f1Node = new SimpleNode(
	    f1Declaration,
	    MutableMap(
          transform(typeInt) ->
	  	  new ContainerNode(
	  		  MutableSet(
	  		      new SimpleNode(
  		    		  intValDeclaration,
  		    		  MutableMap()  		    		  
  		          )
  		      )
	        )
	      )
	    )
	  
	  
	  val f2Node = new SimpleNode(
	    f2Declaration,
	    MutableMap(
          transform(typeInt) ->
	  	  new ContainerNode(
	  		  MutableSet(
	  		      new SimpleNode(
  		    		  intValDeclaration,
  		    		  MutableMap()  		    		  
  		          )
  		      )
	        )
	      )
	    )
	  
	  val query = 
	    new SimpleNode(
	  	  queryDeclaration,
	  	  MutableMap(
	  	      transform(typeString) ->
	  	      new ContainerNode(
	  	          MutableSet(f1Node, f2Node)
	            )
	        ) 
	    )
	    
	  query
	}
	
}