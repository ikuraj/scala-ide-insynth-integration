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

class CodeGenerationTests {
  
  import TreeExample._
  
  @Test
  def test1() = {
  
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
}