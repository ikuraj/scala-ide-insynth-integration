package ch.epfl.test

import ch.epfl.insynth.env._
import ch.epfl.scala.trees._
import ch.epfl.insynth.trees.{ Const=>InSynthConst, _ }

object TreeExample {

	val typeInt = Const("Int")
	val typeString = Const("String")
	val typeBoolean = Const("Boolean")
	val typeChar = Const("Char")
	val typeUnit = Const("Unit")
	
	val typeBottom = Const("$Bottom_Type_Just_For_Resolution$")
	
	private implicit def parameterToList(t: ScalaType): List[ScalaType] = List(t)
	private implicit def parameterToList(t: List[ScalaType]): List[List[ScalaType]] = List(t)
	private implicit def declarationToList(t: Declaration): List[Declaration] = List(t)
	
	import ch.epfl.insynth.trees.TypeTransformer.transform

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
	  //	expression: query(this, m4())
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
	    
	  // XXX not sure how the tree hierarchy ends?!
	}

}

// not needed atm
/*


	  val m1 = Method(
	      objectA, // receiver
	      List( List ( Function(typeInt, typeString), typeString ), typeChar ), // parameters
	      typeBoolean // return type
		)
		
		
	  val m2 = Method(objectA, List(typeInt), typeString)		
	  val m3 = Method(objectA, List(), typeInt)
	  
	  	
	  val m2Declaration = new Declaration(
	      "some.package.A.m2", // full name
	      transform(m2), // inSynth type
	      m2 // scala type
	    )	

*/