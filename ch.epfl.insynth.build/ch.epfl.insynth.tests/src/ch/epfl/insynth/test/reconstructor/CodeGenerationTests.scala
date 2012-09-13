package ch.epfl.insynth.test.reconstructor

import java.{ util => ju, lang => jl }
import ch.epfl.insynth.reconstruction.intermediate.IntermediateTransformer
import ch.epfl.insynth.reconstruction.codegen.{ CleanCodeGenerator, ClassicStyleCodeGenerator, ApplyTransfromer }
import ch.epfl.insynth.reconstruction.combinator.Combinator
import ch.epfl.insynth.reconstruction.combinator.DeclarationTransformer
import ch.epfl.insynth.trees.{ Type }
import ch.epfl.insynth.{ env => InSynth }
import ch.epfl.scala.{ trees => Scala }
import ch.epfl.insynth.reconstruction.intermediate._
import org.junit.Test
import org.junit.Assert._
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import ch.epfl.insynth.trees.BottomType
import ch.epfl.insynth.reconstruction.codegen.SimpleApplicationNamesTransfromer
import ch.epfl.insynth.reconstruction.codegen.NameTransformer
import ch.epfl.insynth.print.Formatable
import ch.epfl.insynth.print.FormatHelpers
import scala.text.Document

class CodeGenerationTests {
  
  import TreeExample._
  
  @Test
  def testApplyTransformer() = {
  
    import Scala._
    import ch.epfl.insynth.trees.TypeTransformer.transform
    
    // declare object
	  val objectType = Const("ObjectName")		
		val objectDeclaration = new InSynth.Declaration(
	      "some.package.ObjectName", // full name
	      transform(objectType), // inSynth type
	      objectType // scala type
	    )

    // declare method
	  val methodType = Method(objectType, List(), typeString)
    val methodDeclaration = new InSynth.Declaration(
	      "apply", // full name
	      transform(methodType), methodType
      )
    // it is an apply method, in an object
    methodDeclaration.setIsApply(true)
    methodDeclaration.setBelongsToObject(true)
    methodDeclaration.setObjectName("ObjectName")
    
    // declare according function
	  val functionType = Function(List(), typeString)
    val functionDeclaration = new InSynth.Declaration(
	      "ObjectName", // full name
	      transform(methodType), functionType
      )
	  
    // declare nodes
    val methodIdentifier = Identifier(methodType, DeclarationTransformer.fromInSynthDeclaration(methodDeclaration))    
	  val methodApplicationNode = Application( methodType, List(Set(methodIdentifier), Set(NullLeaf)) )
	  val methodRootNode = Application(Scala.Function(List(typeString), typeBottom), List(Set(NullLeaf), Set(methodApplicationNode)) )	  
	  
	  val functionIdentifier = Identifier(functionType, DeclarationTransformer.fromInSynthDeclaration(functionDeclaration))	  
	  val functionApplicationNode = Application( functionType, List(Set(functionIdentifier)) )  
	  val functionRootNode = Application(Scala.Function(List(typeString), typeBottom), List(Set(NullLeaf), Set(functionApplicationNode)) )
	  
	  // make appropriate code generator objects
	  val methodGenerator = new CleanCodeGenerator with ApplyTransfromer
	  val functionGenerator = new CleanCodeGenerator
	    
	  // compare all generated outputs
	  for ((output1, output2) <- 
      methodGenerator.apply(methodRootNode) zip functionGenerator.apply(functionRootNode))
	  		assertEquals(output1.toString, output2.toString)
	  		
	  // make appropriate code generator objects
	  val methodClassicGenerator = new ClassicStyleCodeGenerator with ApplyTransfromer
	  
	  // compare all generated outputs
	  for ((output1, output2) <- 
      methodClassicGenerator.apply(methodRootNode) zip functionGenerator.apply(functionRootNode))
	  		assertEquals(output1.toString, output2.toString)
  }
  
  @Test
  def testSSimpleApplicationNamesTransfromer() = {
  
    import Scala._
    import ch.epfl.insynth.trees.TypeTransformer.transform
    
    def testMethodCase(keyIntoTransformer: String, mappedName: String) = {
	    // declare object
		  val classType = Const("ClazzA")		
			val classDeclaration = new InSynth.Declaration(
		      "some.package.ClazzA", // full name
		      transform(classType), // inSynth type
		      classType // scala type
		    )
	
	    // declare method
		  val methodType = Method(classType, List(List(typeString)), typeString)
		  
	    val methodDeclaration = new InSynth.Declaration(
		      keyIntoTransformer, // full name
		      transform(methodType), methodType
	      )
	    // it is an apply method, in an object
	    methodDeclaration.setIsMethod(true)
	    val methodDeclarationTransformed = new InSynth.Declaration(
		      mappedName, // full name
		      transform(methodType), methodType
	      )
	    // it is an apply method, in an object
	    methodDeclarationTransformed.setIsMethod(true)
	    	    
	    val reveiverDeclaration = new InSynth.Declaration(
		      "receiver", // full name
		      transform(classType), classType
	      )
		  val parameterDeclaration = new InSynth.Declaration(
		      "parameter", // full name
		      transform(typeString), typeString
	      )
	    		  
	    // declare nodes
		  val receiverNode = Identifier(classType, DeclarationTransformer.fromInSynthDeclaration(reveiverDeclaration))
		  val parameterNode = Identifier(typeString, DeclarationTransformer.fromInSynthDeclaration(parameterDeclaration))
		  
	    val methodIdentifier = Identifier(methodType, DeclarationTransformer.fromInSynthDeclaration(methodDeclaration))    
		  val methodApplicationNode = Application( methodType, List(Set(methodIdentifier), Set(receiverNode), Set(parameterNode)) )
		  val methodRootNode = Application(Scala.Function(List(typeString), typeBottom), List(Set(NullLeaf), Set(methodApplicationNode)) )
		  
	    val methodIdentifierTransformed = Identifier(methodType, DeclarationTransformer.fromInSynthDeclaration(methodDeclarationTransformed))    
		  val methodApplicationNodeTransformed = Application( methodType, List(Set(methodIdentifierTransformed), Set(receiverNode), Set(parameterNode)) )
		  val methodRootNodeTransformed = Application(Scala.Function(List(typeString), typeBottom), List(Set(NullLeaf), Set(methodApplicationNodeTransformed)) )
		  
		  // make appropriate code generator objects
		  val cleanGenerator = new CleanCodeGenerator
		  val classicGenerator = new ClassicStyleCodeGenerator
		  val cleanGeneratorTransfromer = new CleanCodeGenerator with SimpleApplicationNamesTransfromer
		  val classicGeneratorTransfromer = new ClassicStyleCodeGenerator with SimpleApplicationNamesTransfromer
		    
		  // compare all generated outputs
		  for ((output1, output2) <- 
	      cleanGenerator.apply(methodRootNodeTransformed) zip cleanGeneratorTransfromer.apply(methodRootNode))
		  		assertEquals(output1.toString, output2.toString)
		  			  
		  // compare all generated outputs
		  for ((output1, output2) <- 
	      classicGenerator.apply(methodRootNodeTransformed) zip classicGeneratorTransfromer.apply(methodRootNode))
		  		assertEquals(output1.toString, output2.toString)
    }
    
    
    def testFunctionCase(keyIntoTransformer: String, mappedName: String) = {	
	        
	    // declare according function
		  val functionType = Function(List(typeString), typeString)
	    val functionDeclaration = new InSynth.Declaration(
		      keyIntoTransformer, // full name
		      transform(functionType), functionType
	      )
	    val functionDeclarationTransformed = new InSynth.Declaration(
		      mappedName, // full name
		      transform(functionType), functionType
	      )
		  
		  val parameterDeclaration = new InSynth.Declaration(
		      "parameter", // full name
		      transform(typeString), typeString
	      )
	    		  
	    // declare nodes
		  val parameterNode = Identifier(typeString, DeclarationTransformer.fromInSynthDeclaration(parameterDeclaration))
		  
		  val functionIdentifier = Identifier(functionType, DeclarationTransformer.fromInSynthDeclaration(functionDeclaration))	  
		  val functionApplicationNode = Application( functionType, List(Set(functionIdentifier), Set(parameterNode)) )  
		  val functionRootNode = Application(Scala.Function(List(typeString), typeBottom), List(Set(NullLeaf), Set(functionApplicationNode)) )
		  
		  val functionIdentifierTransformed = Identifier(functionType, DeclarationTransformer.fromInSynthDeclaration(functionDeclarationTransformed))	  
		  val functionApplicationNodeTransformed = Application( functionType, List(Set(functionIdentifierTransformed), Set(parameterNode)) )  
		  val functionRootNodeTransformed = Application(Scala.Function(List(typeString), typeBottom), List(Set(NullLeaf), Set(functionApplicationNodeTransformed)) )
		  
		  // make appropriate code generator objects
		  val cleanGenerator = new CleanCodeGenerator
		  val classicGenerator = new ClassicStyleCodeGenerator
		  val cleanGeneratorTransfromer = new CleanCodeGenerator with SimpleApplicationNamesTransfromer
		  val classicGeneratorTransfromer = new ClassicStyleCodeGenerator with SimpleApplicationNamesTransfromer
		    
		  // compare all generated outputs
		  for ((output1, output2) <- 
	      cleanGenerator.apply(functionRootNodeTransformed) zip cleanGeneratorTransfromer.apply(functionRootNode))
		  		assertEquals(output1.toString, output2.toString)
		  			  
		  // compare all generated outputs
		  for ((output1, output2) <- 
	      classicGenerator.apply(functionRootNodeTransformed) zip classicGeneratorTransfromer.apply(functionRootNode))
		  		assertEquals(output1.toString, output2.toString)
    }
    
    for ( (key, mapped) <- NameTransformer.mapper ) {
      testMethodCase(key, mapped)
      testFunctionCase(key, mapped)
    }
    
  }
  
  @Test
  def testFormatable {
    
  	import FormatHelpers._
  	import Document._
    
    object Formatable1 extends Formatable {
      override def toDocument = empty :: "a" :: empty :: "a" :: empty
    }
  	
  	assertEquals("aa", Formatable1.toString)
  	
    object Formatable2 extends Formatable {
      override def toDocument = empty :: "a" :/: empty :/: "a" :: empty
    }
  	
  	// number of spaces depends on the number of :/: not empty
  	assertEquals("a  a", Formatable2.toString)
  	
  	assertEquals(
	    "a, b, c",
	    Formatable(foldDoc(List[Document](empty, "a", "b", empty, "c", empty, empty), ", ")).toString
    )
    
  	assertEquals(
	    "a,b,c",
	    Formatable(foldDoc(List[Document](empty, "a", "b", empty, "c", empty, empty), ",")).toString
    )
    
  }
}