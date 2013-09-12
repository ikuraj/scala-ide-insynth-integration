package ch.epfl.insynth.reconstruction

import java.{ util => ju, lang => jl }
import scala.text.Document

import ch.epfl.insynth.reconstruction.codegen.{ CleanCodeGenerator, ClassicStyleCodeGenerator, ApplyTransfromer }
import ch.epfl.insynth.reconstruction.codegen.SimpleApplicationNamesTransfromer
import ch.epfl.insynth.reconstruction.codegen.NameTransformer

import insynth.{ structures => InSynth }
import insynth.reconstruction.stream._
import ch.epfl.insynth.{ scala => Scala }
import ch.epfl.insynth.scala.loader._

import org.junit.Test
import org.junit.Assert._
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

import insynth.util.format._

class CodeGenerationTests {
  
  import TreeExample._
  import Scala._
  import TypeTransformer.transform
  implicit def scalaToDomain(x: ScalaType) = DomainTypeTransformer.apply(x)
  
  @Test
  def testApplyTransformer() = {
  
    
    // declare object
	  val objectType = Const("ObjectName")		
		val objectDeclaration = new ScalaDeclaration(
	      "some.package.ObjectName", // full name
	      transform(objectType), // inSynth type
	      objectType // scala type
	    )

    // declare method
	  val methodType = Method(objectType, List(), typeString)
    val methodDeclaration = new ScalaDeclaration(
	      "apply", // full name
	      transform(methodType), methodType
      )
    // it is an apply method, in an object
    methodDeclaration.setIsApply(true)
    methodDeclaration.setBelongsToObject(true)
    methodDeclaration.setObjectName("ObjectName")
    
    // declare according function
	  val functionType = Function(List(), typeString)
    val functionDeclaration = new ScalaDeclaration(
	      "ObjectName", // full name
	      transform(methodType), functionType
      )
	  
    // declare nodes
    val methodIdentifier = Identifier(methodType, methodDeclaration)    
	  val methodApplicationNode = Application( methodType, List(methodIdentifier, NullLeaf) )
	  val methodRootNode = Application(Scala.Function(List(typeString), typeBottom), List(NullLeaf, methodApplicationNode) )	  
	  
	  val functionIdentifier = Identifier(functionType, functionDeclaration)	  
	  val functionApplicationNode = Application( functionType, List(functionIdentifier) )  
	  val functionRootNode = Application(Scala.Function(List(typeString), typeBottom), List(NullLeaf, functionApplicationNode) )
	  
	  // make appropriate code generator objects
	  val methodGenerator = new CleanCodeGenerator with ApplyTransfromer
	  val functionGenerator = new CleanCodeGenerator
	    
	  // compare generated outputs
	  assertEquals(methodGenerator.apply(methodRootNode).toString, functionGenerator.apply(functionRootNode).toString)
	  		
	  // make appropriate code generator objects
	  val methodClassicGenerator = new ClassicStyleCodeGenerator with ApplyTransfromer
	  
	  // compare generated outputs
	  assertEquals(methodClassicGenerator.apply(methodRootNode).toString, functionGenerator.apply(functionRootNode).toString)
  }
  
  @Test
  def testSSimpleApplicationNamesTransfromer() = {
    
    def testMethodCase(keyIntoTransformer: String, mappedName: String) = {
	    // declare object
		  val classType = Const("ClazzA")		
			val classDeclaration = new ScalaDeclaration(
		      "some.package.ClazzA", // full name
		      transform(classType), // inSynth type
		      classType // scala type
		    )
	
	    // declare method
		  val methodType = Method(classType, List(List(typeString)), typeString)
		  
	    val methodDeclaration = new ScalaDeclaration(
		      keyIntoTransformer, // full name
		      transform(methodType), methodType
	      )
	    // it is an apply method, in an object
	    methodDeclaration.setIsMethod(true)
	    val methodDeclarationTransformed = new ScalaDeclaration(
		      mappedName, // full name
		      transform(methodType), methodType
	      )
	    // it is an apply method, in an object
	    methodDeclarationTransformed.setIsMethod(true)
	    	    
	    val reveiverDeclaration = new ScalaDeclaration(
		      "receiver", // full name
		      transform(classType), classType
	      )
		  val parameterDeclaration = new ScalaDeclaration(
		      "parameter", // full name
		      transform(typeString), typeString
	      )
	    		  
	    // declare nodes
		  val receiverNode = Identifier(classType, (reveiverDeclaration))
		  val parameterNode = Identifier(typeString, (parameterDeclaration))
		  
	    val methodIdentifier = Identifier(methodType, (methodDeclaration))    
		  val methodApplicationNode = Application( methodType, List((methodIdentifier), (receiverNode), (parameterNode)) )
		  val methodRootNode = Application(Scala.Function(List(typeString), typeBottom), List(NullLeaf, (methodApplicationNode)) )
		  
	    val methodIdentifierTransformed = Identifier(methodType, (methodDeclarationTransformed))    
		  val methodApplicationNodeTransformed = Application( methodType, List((methodIdentifierTransformed), (receiverNode), (parameterNode)) )
		  val methodRootNodeTransformed = Application(Scala.Function(List(typeString), typeBottom), List((NullLeaf), (methodApplicationNodeTransformed)) )
		  
		  // make appropriate code generator objects
		  val cleanGenerator = new CleanCodeGenerator
		  val classicGenerator = new ClassicStyleCodeGenerator
		  val cleanGeneratorTransfromer = new CleanCodeGenerator with SimpleApplicationNamesTransfromer
		  val classicGeneratorTransfromer = new ClassicStyleCodeGenerator with SimpleApplicationNamesTransfromer
		    
		  assertEquals(cleanGenerator.apply(methodRootNodeTransformed).toString, cleanGeneratorTransfromer.apply(methodRootNode).toString)
		  assertEquals(classicGenerator.apply(methodRootNodeTransformed).toString, classicGeneratorTransfromer.apply(methodRootNode).toString)
    }
    
    
    def testFunctionCase(keyIntoTransformer: String, mappedName: String) = {	
	        
	    // declare according function
		  val functionType = Function(List(typeString), typeString)
	    val functionDeclaration = new ScalaDeclaration(
		      keyIntoTransformer, // full name
		      transform(functionType), functionType
	      )
	    val functionDeclarationTransformed = new ScalaDeclaration(
		      mappedName, // full name
		      transform(functionType), functionType
	      )
		  
		  val parameterDeclaration = new ScalaDeclaration(
		      "parameter", // full name
		      transform(typeString), typeString
	      )
	    		  
	    // declare nodes
		  val parameterNode = Identifier(typeString, (parameterDeclaration))
		  
		  val functionIdentifier = Identifier(functionType, (functionDeclaration))	  
		  val functionApplicationNode = Application( functionType, List((functionIdentifier), (parameterNode)) )  
		  val functionRootNode = Application(Scala.Function(List(typeString), typeBottom), List((NullLeaf), (functionApplicationNode)) )
		  
		  val functionIdentifierTransformed = Identifier(functionType, (functionDeclarationTransformed))	  
		  val functionApplicationNodeTransformed = Application( functionType, List((functionIdentifierTransformed), (parameterNode)) )  
		  val functionRootNodeTransformed = Application(Scala.Function(List(typeString), typeBottom), List((NullLeaf), (functionApplicationNodeTransformed)) )
		  
		  // make appropriate code generator objects
		  val cleanGenerator = new CleanCodeGenerator
		  val classicGenerator = new ClassicStyleCodeGenerator
		  val cleanGeneratorTransfromer = new CleanCodeGenerator with SimpleApplicationNamesTransfromer
		  val classicGeneratorTransfromer = new ClassicStyleCodeGenerator with SimpleApplicationNamesTransfromer
		    
		  assertEquals(cleanGenerator.apply(functionRootNodeTransformed).toString, cleanGeneratorTransfromer.apply(functionRootNodeTransformed).toString)
		  assertEquals(classicGenerator.apply(functionRootNodeTransformed).toString, classicGeneratorTransfromer.apply(functionRootNodeTransformed).toString)
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
	    foldDoc(List[Document](empty, "a", "b", empty, "c", empty, empty), ", ").toString
    )
    
  	assertEquals(
	    "a,b,c",
	    (foldDoc(List[Document](empty, "a", "b", empty, "c", empty, empty), ",")).toString
    )
    
  }
}