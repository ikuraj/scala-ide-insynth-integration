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
	  val getStringNode = SimpleNode(
	    m4Declaration,
	    Map(
          // I will get object of class A from
          transform(objectA) ->
	  	  ContainerNode(
	  		  Set(
	  		      SimpleNode(
  		    		  objectADeclaration,
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
	  	  Map( // for each parameter type - how can we resolve it
	  	      InSynthConst("String") ->
	  	      ContainerNode(
	  	          Set(getStringNode)
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
	  
	  // XXX found out that there is a non-needed redundancy, ContainerNode type
	  // is actually not needed?
	  
	  // goal:ClassA object, type:ClassA
	  // expression: this	  
	  val thisNode = SimpleNode(
	      objectADeclaration, Map()
      )
	    
	  // goal:Char, type:Unit→Char
	  // expression: m4(this)	  
	  val m4Node = SimpleNode(
	      m4Declaration,
	      Map(
	          transform(objectA) -> ContainerNode(Set(thisNode))
          )
      )
      
      // goal:(Int→String), type:(Int→String)
	  // expression: m2(this)
	  val m2Node = SimpleNode(
	      m2Declaration,
	      Map(
	          transform(objectA) -> ContainerNode(Set(thisNode)),
	          transform(typeInt) ->
	          	ContainerNode(Set(SimpleNode(
	          	    { 
	          	      val dec = new Declaration(typeInt); dec.setIsApply(true); dec	          	    	
	          	    }, Map.empty
          	    )))
          )
      )      
      
      // goal:String, type:(A→String)
	  // expression: m6(this)
	  val m6Node = SimpleNode(
	      m6Declaration,
	      Map(
	          transform(objectA) -> ContainerNode(Set(thisNode))
          )
      )
            
      // goal: Long, type:(Int→Long)
	  // expression: m5(this, _)
	  val m5Node = SimpleNode(
	      m5Declaration,
	      Map(
	          transform(objectA) -> ContainerNode(Set(thisNode)),
	          transform(typeInt) -> ContainerNode( 
	          	Set( SimpleNode(
	          	    { 
	          	      val dec = new Declaration(typeInt); dec.setIsApply(true); dec	          	    	
	          	    }, Map.empty
          	    ) )
	          )
          )
      )
      
      // goal:(Int→String), type:(Long→String)
	  // expression: Int => m3(this, m5(this, _))
	  val composeNode = SimpleNode(
	      m3Declaration,
	      Map(
	          transform(objectA) -> ContainerNode(Set(thisNode)),
	          transform(typeLong) -> ContainerNode(Set(m5Node))
          )
      )
	    
	  // goal:Boolean, type:List((Int→String),Char)→Boolean
	  // expression: m1(this, 
      //				m2(this) |  m3(this) ∘ m5(this) | Int→m6(this), 
	  //				m4(this))	  
	  val m1Node = SimpleNode(
	      m1Declaration,
	      Map(
	          transform(typeChar) -> ContainerNode(Set(m4Node)),
	          transform(Function(typeInt, typeString)) ->
	          	ContainerNode( 
	          	    Set(composeNode, m2Node, m6Node)
          	    ),
	          transform(objectA) -> ContainerNode(Set(thisNode))
          )
      )
	  
      // goal:⊥, type:Boolean→⊥	    
      // expression: query(		m1(this,
	  //			m2(this) |  m3(this) ∘ m5(this) | Int→m6(this), 
	  //			m4(this)	)):⊥
	  val queryNode = 
	    SimpleNode(
	  	  queryDeclaration,
	  	  Map( // for each parameter type - how can we resolve it
	  	      transform(typeBoolean) ->
	  	      ContainerNode(
	  	          Set(m1Node)
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
	  val thisNode = SimpleNode(
	      objectADeclaration,
	      Map()
      )
      
      // goal:Int, type:Int
	  // expression: A.intVal	  
	  val intValNode = SimpleNode(
	      intValDeclaration,
	      Map()
      )
	  
	  // goal:(Int→Char), type:A→Int→Char
	  // expression: (Int,Int) → m1(this)(_, _)	  
	  val m1Node = SimpleNode(
	      m1Declaration,
	      Map(
	          transform(objectA) -> ContainerNode(Set(thisNode)),
	          transform(typeInt) -> ContainerNode(
	              Set(SimpleNode(leafIntDeclaration, Map.empty), intValNode))
          )
      )
      
      // goal:(Int→Char), type:(Int→Char)
	  // expression: d.fullName ("outside")	  
	  val outsideNode = SimpleNode(
	      outsideDeclaration,
	      Map(
	          transform(typeInt) -> ContainerNode(
	              Set(SimpleNode(leafIntDeclaration, Map.empty), intValNode))
          )
      )
      
      // goal:(Char), type:(A→Char)
	  // expression: (Int,Int)→m3(A)	  
	  val m3Node = SimpleNode(
	      m3Declaration,
	      Map(
	        transform(objectA) -> 
	          ContainerNode(Set(thisNode))
          )
      )
      
      // goal:(Int→Char), type:((Int,A)→Char)
	  // expression: (Int, Int) → m2(this, _, _)	  
	  val m2Node = SimpleNode(
	      m2Declaration,
	      Map(
	        transform(typeInt) -> 
        	  ContainerNode(Set(SimpleNode(leafIntDeclaration, Map.empty), intValNode)),
	        transform(objectA) ->
        	  ContainerNode(Set(thisNode))
          )
      )     
	  
      // goal:⊥, type:(Int→Char)→⊥	    
      // expression: query	(		
      //	(Int,Int) -> m1(this)(_,_) | (Int,Int) -> m1(this)(intVal, intVal)
	  //	(Int,Int) -> m2(this,_,_) | m2(this, intVal, intVal)
      //	(Int,Int) -> m3(this) | outside
      //					):⊥
	  val queryNode = 
	    SimpleNode(
	  	  queryDeclaration,
	  	  Map( // for each parameter type - how can we resolve it
	  	      transform(Function(List(typeInt, typeInt), typeChar)) ->
	  	      ContainerNode(
	  	          Set(m1Node, outsideNode, m2Node, m3Node)
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
	  val thisNode = SimpleNode(
	      objectADeclaration,
	      Map()
      )
      
      // goal:Int, type:Int
	  // expression: A.intVal	  
	  val intValNode = SimpleNode(
	      intValDeclaration,
	      Map()
      )
	  
	  // goal:(Int→Char), type:A→Int→Char
	  // expression: (Int,Int) → m1(this)(_, _)	  
	  val m1Node = SimpleNode(
	      m1Declaration,
	      Map(
	          transform(objectA) -> ContainerNode(Set(thisNode)),
	          transform(typeInt) -> ContainerNode(
	              Set(SimpleNode(leafIntDeclaration, Map.empty), intValNode))
          )
      )
      
      // goal:(Int→Char), type:(Int→Char)
	  // expression: d.fullName ("outside")	  
	  val outsideNode = SimpleNode(
	      outsideDeclaration,
	      Map(
	          transform(typeInt) -> ContainerNode(
	              Set(SimpleNode(leafIntDeclaration, Map.empty), intValNode))
          )
      )
      
      // goal:(Char), type:(A→Char)
	  // expression: (Int,Int)→m3(A)	  
	  val m3Node = SimpleNode(
	      m3Declaration,
	      Map(
	        transform(objectA) -> 
	          ContainerNode(Set(thisNode))
          )
      )
      
      // goal:(Int→Char), type:((Int,A)→Char)
	  // expression: (Int, Int) → m2(this, _, _)	  
	  val m2Node = SimpleNode(
	      m2Declaration,
	      Map(
	        transform(typeInt) -> 
        	  ContainerNode(Set(SimpleNode(leafIntDeclaration, Map.empty), intValNode)),
	        transform(objectA) ->
        	  ContainerNode(Set(thisNode))
          )
      )     
	  
      // goal:⊥, type:(Int→Char)→⊥	    
      // expression: query	(		
      //	(Int,Int) -> m1(this)(_,_) | (Int,Int) -> m1(this)(intVal, intVal)
	  //	(Int,Int) -> m2(this,_,_) | m2(this, intVal, intVal)
      //	(Int,Int) -> m3(this) | outside
      //					):⊥
	  val queryNode = 
	    SimpleNode(
	  	  queryDeclaration,
	  	  Map( // for each parameter type - how can we resolve it
	  	      transform(Function(List(typeInt, typeInt), typeChar)) ->
	  	      ContainerNode(
	  	          Set(m1Node, outsideNode, m2Node, m3Node)
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
	  
	  val intLeafNode = SimpleNode(new Declaration(typeInt), Map.empty)
	  	  	  
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
	  val absNode = SimpleNode(
	      new Declaration(Function(typeInt, typeChar)),
	      Map(
	        transform(typeInt) -> 
        	  ContainerNode(Set(intLeafNode))
          )
      )     
	  
      // goal:⊥, type:(Int→Char, Int)→Char→⊥	    
      // expression: query	(		
      //	(Int→Char, Int) -> _(_)
      //					):⊥
	  val queryNode = 
	    SimpleNode(
	  	  queryDeclaration,
	  	  Map( // for each parameter type - how can we resolve it
	  	      transform(Function(List(Function(typeInt, typeChar), typeInt), typeChar)) ->
	  	      ContainerNode(
	  	          Set(absNode)
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
      
	  val intLeafNode = SimpleNode(new Declaration(typeInt), Map.empty)
	  
      // TODO
      // goal:(Int→Char, Int)→Char, type:Char
	  // expression: (Int, Int) → m2(this, _, _)	????  
	  val absNode2 = SimpleNode(
	      new Declaration(Function(List(typeInt), typeChar)),
	      Map(
	        transform(typeInt) -> 
        	  ContainerNode(Set(intLeafNode))     	  
          )
      )     
	    
      // TODO
      // goal:(Int→Char, Int)→Char, type:Char
	  // expression: (Int, Int) → m2(this, _, _)	????  
	  val absNode1 = SimpleNode(
	      new Declaration(Function(typeInt, Function(typeChar, typeString))),
	      Map(
	        transform(typeInt) -> 
        	  ContainerNode(Set(intLeafNode)),
        	transform(typeChar) ->
        	  ContainerNode(Set(absNode2))        	  
          )
      )     

      // TODO      
      // goal:⊥, type:(Int→Char, Int)→Char→⊥	    
      // expression: query	(		
      //	(Int→Char, Int) -> _(_)
      //					):⊥????
	  val queryNode = 
	    SimpleNode(
	  	  queryDeclaration,
	  	  Map( // for each parameter type - how can we resolve it
	  	      transform(sKombType) ->
	  	      ContainerNode(
	  	          Set(absNode1)
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
	  val constructB = Function(List(typeString, typeInt), objectB)
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
	  	  
	  val thisNode = SimpleNode(
	      objectADeclaration,
	      Map()
      )
	  
	  // goal:String, type:String, →B
	  // expression: new (this.m(), this.bla)
	  val getBNode = SimpleNode(
	    constructBDeclaration,
	    Map(
          // I will get object of class A from
          transform(typeString) ->
	  	  ContainerNode(
	  		  Set(
	  		      SimpleNode(
  		    		  mDeclaration,
  		    		  Map( transform(objectA) -> ContainerNode(Set( thisNode )))
  		          )
  		      )
	        ),
          transform(typeInt) ->
	  	  ContainerNode(
	  		  Set(
	  		      SimpleNode(
  		    		  intValDeclaration,
  		    		  Map( transform(objectA) -> ContainerNode(Set( thisNode )))  		    		  
  		          )
  		      )
	        )
	      )
	    )
	  
      // goal:Bottom, type:B→⊥
      // expression: query(new (this.m(), this.bla)):⊥
	  val query = 
	    SimpleNode(
	  	  queryDeclaration,
	  	  Map( // for each parameter type - how can we resolve it
	  	      transform(objectB) ->
	  	      ContainerNode(
	  	          Set(getBNode)
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
	  	  
	  val thisNode = SimpleNode(
	      objectADeclaration,
	      Map()
      )
      
      val intNode = SimpleNode(
          intValDeclaration,
          Map()
      )
	  
	  val getStringNode = SimpleNode(
	    mDeclaration,
	    Map(
          // I will get object of class A from
          transform(typeInt) ->
	  	  ContainerNode(
	  		  Set(intNode)
	        ),
          transform(objectA) ->
	  	  ContainerNode(
	  		  Set(thisNode)
	        )
	      )
	    )
	  
	  val query = 
	    SimpleNode(
	  	  queryDeclaration,
	  	  Map( // for each parameter type - how can we resolve it
	  	      transform(typeString) ->
	  	      ContainerNode(
	  	          Set(getStringNode)
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
	  // Arrow(TSet(List(typeInt)),Const($Bottom_Type_Just_For_Resolution$))
	  
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
	  	        
      val intNode = SimpleNode(
          intValDeclaration,
          Map()
      )
	  
	  lazy val getIntNode:SimpleNode = SimpleNode(
	    fDeclaration,
	    Map(
          transform(typeInt) ->
	  	  ContainerNode(
	  		  Set(intNode, getIntNodeRec)
	        )
	    )
	  )
	  
	  lazy val getIntNodeRec = SimpleNode(
	    fDeclaration,
	    Map(
          transform(typeInt) ->
	  	  ContainerNode(
	  		  Set(getIntNode)
	        )
	    )
	  )
	  
	  
	  val query = 
	    SimpleNode(
	  	  queryDeclaration,
	  	  Map( // for each parameter type - how can we resolve it
	  	      transform(typeInt) ->
	  	      ContainerNode(
	  	          Set(getIntNode)
	            )
	        ) 
	    )
	    
	  query
	}
	
}