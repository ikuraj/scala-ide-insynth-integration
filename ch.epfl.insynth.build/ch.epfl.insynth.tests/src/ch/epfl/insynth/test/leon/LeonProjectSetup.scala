package ch.epfl.insynth.test.leon

import scala.tools.eclipse.testsetup.TestProjectSetup
import ch.epfl.insynth.test.completion.CompletionUtility

import org.junit.Assert._
import org.junit.Test
import org.junit.BeforeClass
import org.junit.Ignore

object LeonProjectSetup extends TestProjectSetup("leon", bundleName = "ch.epfl.insynth.tests")

class LeonProjectSetup {
	val testProjectSetup = new CompletionUtility(LeonProjectSetup)
	
	import testProjectSetup._

	@Test
	def run() {
	  //withCompletions("list/List.scala")(List("sizeTail(tail, 1)"), 0)("ListGenerated_%d.scala")
	  withCompletions("RedBlackTree.scala")(List("Node(Red(),Node(Black(),a,xV,b),yV,Node(Black(),c,zV,d))"), 0)("RedBlackTreeGenerated_%d.scala")
	}

}