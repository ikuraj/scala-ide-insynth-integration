package ch.epfl.test
import ch.epfl.lambda.LambdaTransformer

object LambdaReconstructionTest {

  def main(args: Array[String]): Unit = {
    println("simpleTreeTransform")
    simpleTreeTransform
    println("complexTreeTransform")
    complexTreeTransform
    println("arrowTreeTransform")
    arrowTreeTransform
  }
  
  def simpleTreeTransform() = {
    val simpleTree = TreeExample.buildSimpleTree
    
    val terms = LambdaTransformer(simpleTree)
    
    for (term <- terms)
	  println(term)
  }
  
  def complexTreeTransform() = {
    val complexTree = TreeExample.buildComplexTree
    
    val terms = LambdaTransformer(complexTree)
    
    for (term <- terms)
      println(term)
  }
  
  def arrowTreeTransform() = {
    val arrowTree = TreeExample.buildTreeArrowType
    
    val terms = LambdaTransformer(arrowTree)
    
    for (term <- terms)
      println(term)
  }

}