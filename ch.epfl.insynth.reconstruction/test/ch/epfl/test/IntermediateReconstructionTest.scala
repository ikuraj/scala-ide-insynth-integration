package ch.epfl.test
import ch.epfl.insynth.reconstruction.IntermediateTransformer
import ch.epfl.insynth.reconstruction.trees.FormatableIntermediate
import ch.epfl.insynth.combinator.Combinator
import ch.epfl.insynth.env.Node
import ch.epfl.insynth.env.SimpleNode
import ch.epfl.insynth.env.FormatNode
import ch.epfl.insynth.combinator.FormatPrNode
import ch.epfl.insynth.reconstruction.Extractor

object IntermediateReconstructionTest {
  
  val numberOfCombinations = 15
  
  implicit def toFormatNode(sn: SimpleNode) = FormatNode(sn)
  implicit def toPrFormatNode(sn: ch.epfl.insynth.combinator.Node) = FormatPrNode(sn)

  def main(args: Array[String]): Unit = {
    val tests =      
      Array(
//        TreeExample.buildSimpleTree, TreeExample.buildComplexTree,
//        TreeExample.buildTreeAbsApplication, TreeExample.buildTreeArrowType,
//        /*TreeExample.buildTreeCycles, */TreeExample.buildTreeOverlapParameterTypeWithReturnType,
//        TreeExample.buildTreeSKombinator, TreeExample.buildTreeWithCurryingFunctions,
//        TreeExample.buildTreeWithVariousFunctions, TreeExample.buildTreeWithoutThis,
        TreeExample.buildTreeIdentityFunction
      )
    
    for (tree <- tests )
      parametrizedTreeTransform(tree) 
  }
  
  def parametrizedTreeTransform(node: SimpleNode) = {    
    node.println
    
    val prunedTree = Combinator(node, numberOfCombinations)
    println("pruned tree")    
    prunedTree.println
    
    val transformedTrees = IntermediateTransformer(prunedTree)
    println("after intermediate transform")
    
    println("simple tree transformed") 
	FormatableIntermediate(transformedTrees).println
	println(transformedTrees)
  }

}