package ch.epfl.insynth.reconstruction

import java.util.regex.Pattern
import java.{ util => ju, lang => jl }

import insynth.structures._

import ch.epfl.insynth.reconstruction.codegen._
import ch.epfl.insynth.core.Activator
import ch.epfl.insynth.core.preferences.InSynthConstants
import ch.epfl.insynth.core.completion.InnerFinder

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.junit._
import org.junit.Assert._

@RunWith(value = classOf[Parameterized])
class ReconstructorTest(givenTree: SimpleNode, expectedListAll: List[(Object, Object)]) {
  
  @Test
  def test() {
    // get the list of expected snippets for variants of code styles
    val (cleanExpectedList, classicCodeExpectedList) = expectedListAll unzip
    
    // get the reconstruction result
    val cleanReconstructorOutput = Reconstructor(givenTree, new CleanCodeGenerator)   
    // use it to do the check
    checkResults(cleanReconstructorOutput map { _.getSnippet }, cleanExpectedList)
    
    // get the reconstruction result
    val classicReconstructorOutput = Reconstructor(givenTree, new ClassicStyleCodeGenerator)    
    // use it to do the check
    checkResults(classicReconstructorOutput map { _.getSnippet }, classicCodeExpectedList)    
  }
    
    
  def checkResults(reconstructorOutput: List[String], expectedList: List[Object]) {
    
    assertEquals("Number of completions did not match. ", expectedList.size, reconstructorOutput.size)
    
    // for each expected string
    for (expected <- expectedList) {
      // assert true with an error message containing reconstructed snippets
      assertTrue(
        "Expected string (" + expected + ") could not be found, reconstructor output:\n"
      		+ ( reconstructorOutput mkString ( "\n" ) ), 
    		// go though all reconstruction outputs and try to match with expected string
        (false /: reconstructorOutput) {
          (result, output) => {
            if (!result)
              // match string snippet
	            expected match {
              	// in case of string
	              case `output` => true
	              // in case of string representing a regex object
	              case pattern: Pattern if pattern.matcher(output) matches => true
	              case _ => result
	            }
            else result
          }
        }
      )
    }
  }

}

object ReconstructorTest {
  
  val numberOfCombinations = 15
  val maximumTime = 500
  
  @BeforeClass
  def setup() {    
    // set appropriate preference values
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.OfferedSnippetsPropertyString, 50)        
		Activator.getDefault.getPreferenceStore.setValue(InSynthConstants.MaximumTimePropertyString, 1000)
		
		// wait for builder thread to finish (we want to start next tests without interfering)
		if (InnerFinder.predefBuildLoader.isAlive())
			InnerFinder.predefBuildLoader.join();
  }
  
	@Parameters
	def parameters: ju.Collection[Array[Object]] = {
	  import scala.collection.JavaConversions._
	  import TreeExample._
	  
	  // some convenience
	  case class PairStrings(inner: Object) {
	  	def **(arg: Object): (Object, Object) = (inner, arg)
	  }
	  implicit def pairStringsCast(s: Object) = PairStrings(s)
	  
	  case class PatternString(s: String) {
	  	def unary_! = Pattern.compile(s)
	  }
	  implicit def patternizeStringsCast(s: String) = PatternString(s)
	  
	  implicit def objectToPairCast(o: Object) = (o, o)
	  	  
	  // prefix string with ! to treat it as a regex pattern
	  // ** pairs two objects
	  // ~ makes pair of two identical objects
		val trees: List[Array[Object]] = List(
	    // parameter[0]
      Array( buildSimpleTree, List("A m4" ** "A.m4()") )
      ,
	    // parameter[1]      
      Array( buildComplexTree, List(
        ! "m1\\((\\S+) => m6, m4\\)" ** ! "m1\\((\\S+) => m6\\(\\), m4\\(\\)\\)" ,
        ! "m1\\((\\S+) => m2\\(\\1\\), m4\\)" ** ! "m1\\((\\S+) => m2\\(\\1\\), m4\\(\\)\\)" ,
        ! "m1\\((\\S+) => m3\\(m5\\(\\1\\)\\), m4\\)" ** ! "m1\\((\\S+) => m3\\(m5\\(\\1\\)\\), m4\\(\\)\\)")
      )
      ,
	    // parameter[2]
      // NOTE this one can pass without specifying types
      // e.g. val testVal: (Int=>Char, Int) => Char = (x, y) => x(y)   (OK)
      Array(buildTreeAbsApplication, List(
        ! "\\((\\S+), (\\S+)\\) => \\1\\(\\2\\)" ** ! "\\((\\S+), (\\S+)\\) => \\1\\(\\2\\)")
      )
      ,
	    // parameter[3]
      Array(buildTreeArrowType, 
        List[(Object, Object)](
          ! "\\((\\S+), (\\S+)\\) => A m3" ** ! "\\((\\S+), (\\S+)\\) => A\\.m3\\(\\)",
          ! "\\((\\S+), (\\S+)\\) => outside\\(\\1, \\1\\)",
          ! "\\((\\S+), (\\S+)\\) => outside\\(\\1, \\2\\)",
          ! "\\((\\S+), (\\S+)\\) => outside\\(\\2, \\1\\)",
          ! "\\((\\S+), (\\S+)\\) => outside\\(\\2, \\2\\)",
          ! "\\((\\S+), (\\S+)\\) => outside\\(intVal, intVal\\)",
          ! "\\((\\S+), (\\S+)\\) => outside\\(intVal, \\1\\)",
          ! "\\((\\S+), (\\S+)\\) => outside\\(\\1, intVal\\)",
          ! "\\((\\S+), (\\S+)\\) => outside\\(intVal, \\2\\)",
          ! "\\((\\S+), (\\S+)\\) => outside\\(\\2, intVal\\)",
          ! "\\((\\S+), (\\S+)\\) => A\\.m1\\(\\)\\(\\1, \\1\\)",
          ! "\\((\\S+), (\\S+)\\) => A\\.m1\\(\\)\\(\\1, \\2\\)",
          ! "\\((\\S+), (\\S+)\\) => A\\.m1\\(\\)\\(\\2, \\1\\)",
          ! "\\((\\S+), (\\S+)\\) => A\\.m1\\(\\)\\(\\2, \\2\\)",
          ! "\\((\\S+), (\\S+)\\) => A\\.m1\\(\\)\\(intVal, intVal\\)",
          ! "\\((\\S+), (\\S+)\\) => A\\.m1\\(\\)\\(intVal, \\1\\)",
          ! "\\((\\S+), (\\S+)\\) => A\\.m1\\(\\)\\(\\1, intVal\\)",
          ! "\\((\\S+), (\\S+)\\) => A\\.m1\\(\\)\\(intVal, \\2\\)",
          ! "\\((\\S+), (\\S+)\\) => A\\.m1\\(\\)\\(\\2, intVal\\)",
          ! "\\((\\S+), (\\S+)\\) => A\\.m2\\(intVal, intVal\\)",
          ! "\\((\\S+), (\\S+)\\) => A\\.m2\\(intVal, intVal\\)",
          ! "\\((\\S+), (\\S+)\\) => A\\.m2\\(intVal, intVal\\)",
          ! "\\((\\S+), (\\S+)\\) => A\\.m2\\(intVal, intVal\\)",
          ! "\\((\\S+), (\\S+)\\) => A\\.m2\\(intVal, intVal\\)",
          ! "\\((\\S+), (\\S+)\\) => A\\.m2\\(intVal, intVal\\)",
          ! "\\((\\S+), (\\S+)\\) => A\\.m2\\(intVal, intVal\\)",
          ! "\\((\\S+), (\\S+)\\) => A\\.m2\\(intVal, intVal\\)",
          ! "\\((\\S+), (\\S+)\\) => A\\.m2\\(intVal, intVal\\)"
        ))
        ,
	    // parameter[4]
      // NOTE only one solution is expected (recursion in proof tree is terminated right away)
      // f(intVal) returned rather than intVal ??
//      Array(buildTreeCycles, List[(Object, Object)](
//        //"intVal"
//        //,
//        "f(intVal)"
//      ))
//      ,
	    // parameter[5]
      Array(buildTreeOverlapParameterTypeWithReturnType, List[(Object, Object)](
        ! "\\((\\S+), (\\S+)\\) => A\\.m1\\(\\)\\(\\1, \\2\\)",
        ! "\\((\\S+), (\\S+)\\) => A\\.m1\\(\\)\\(\\2, \\1\\)",
        ! "\\((\\S+), (\\S+)\\) => A\\.m1\\(\\)\\(\\1, \\1\\)",
        ! "\\((\\S+), (\\S+)\\) => A\\.m1\\(\\)\\(\\2, \\2\\)",
        ! "\\((\\S+), (\\S+)\\) => A\\.m1\\(\\)\\(\\1, intVal\\)",
        ! "\\((\\S+), (\\S+)\\) => A\\.m1\\(\\)\\(\\2, intVal\\)",
        ! "\\((\\S+), (\\S+)\\) => A\\.m1\\(\\)\\(intVal, \\1\\)",
        ! "\\((\\S+), (\\S+)\\) => A\\.m1\\(\\)\\(intVal, \\2\\)",
        ! "\\((\\S+), (\\S+)\\) => A\\.m1\\(\\)\\(intVal, intVal\\)",
        ! "\\((\\S+), (\\S+)\\) => A\\.m2\\(\\1, \\2\\)",
        ! "\\((\\S+), (\\S+)\\) => A\\.m2\\(\\2, \\1\\)",
        ! "\\((\\S+), (\\S+)\\) => A\\.m2\\(\\1, \\1\\)",
        ! "\\((\\S+), (\\S+)\\) => A\\.m2\\(\\2, \\2\\)",
        ! "\\((\\S+), (\\S+)\\) => A\\.m2\\(\\1, intVal\\)",
        ! "\\((\\S+), (\\S+)\\) => A\\.m2\\(\\2, intVal\\)",
        ! "\\((\\S+), (\\S+)\\) => A\\.m2\\(intVal, \\1\\)",
        ! "\\((\\S+), (\\S+)\\) => A\\.m2\\(intVal, \\2\\)",
        ! "\\((\\S+), (\\S+)\\) => A\\.m2\\(intVal, intVal\\)",
        ! "\\((\\S+), (\\S+)\\) => outside\\(\\1, \\2\\)",
        ! "\\((\\S+), (\\S+)\\) => outside\\(\\2, \\1\\)",
        ! "\\((\\S+), (\\S+)\\) => outside\\(\\1, \\1\\)",
        ! "\\((\\S+), (\\S+)\\) => outside\\(\\2, \\2\\)",
        ! "\\((\\S+), (\\S+)\\) => outside\\(\\1, intVal\\)",
        ! "\\((\\S+), (\\S+)\\) => outside\\(\\2, intVal\\)",
        ! "\\((\\S+), (\\S+)\\) => outside\\(intVal, \\1\\)",
        ! "\\((\\S+), (\\S+)\\) => outside\\(intVal, \\2\\)",
        ! "\\((\\S+), (\\S+)\\) => outside\\(intVal, intVal\\)",
        ! "\\((\\S+), (\\S+)\\) => A m3" ** ! "\\((\\S+), (\\S+)\\) => A\\.m3\\(\\)"
      ))
      ,
	    // parameter[6]
      Array(buildTreeSKombinator, List[(Object, Object)](
    		! "(\\S+) => (\\S+) => (\\S+) => \\1\\(\\3\\)\\(\\2\\(\\3\\)\\)"
      ))
      ,
	    // parameter[7]
      Array(buildTreeWithCurryingFunctions, List[(Object, Object)](
    		"this.m(intVal)(intVal)"
      ))
      ,
	    // parameter[8]
      Array(buildTreeWithVariousFunctions, List[(Object, Object)](
        "new B(obj m, obj intVal)" ** "new B(obj.m(), obj.intVal)" 
      ))
      ,
	    // parameter[9]
      Array(buildTreeWithoutThis, List[(Object, Object)](
        "m1(this m2, this f1, f2)" ** "m1(this.m2(), this.f1, f2)"
      ))
      ,
	    // parameter[10]
			Array(buildTreeIdentityFunction, List[(Object, Object)](
		    ! "(\\S+) => \\1"
	    ))
			,
	    // parameter[11]
			Array(buildTreeWithConstructors, List[(Object, Object)](
		    "f(new A() intVal, new A)" ** "f(new A().intVal, new A())"
	    ))
			,
	    // parameter[12]
      Array(buildSameInSynthDifferentWeight, List[(Object, Object)](
        "f1(intVal)",
        "f2(intVal, intVal)"
      ))
    )
	  
	  trees
//	  (List(trees(7)))
//	  (trees.take(7))
	}

}