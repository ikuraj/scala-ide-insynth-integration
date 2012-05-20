package ch.epfl.test
import ch.epfl.lambda.LambdaTransformer

object LambdaReconstructionTest {

  def main(args: Array[String]): Unit = {
    complexTreeTransform
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

}