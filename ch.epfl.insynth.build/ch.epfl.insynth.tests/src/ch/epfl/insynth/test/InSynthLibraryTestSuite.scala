package ch.epfl.insynth.test

import org.junit.runner.RunWith
import org.junit.runners.Suite

import ch.epfl.insynth.trees.tests._

@RunWith(classOf[Suite])
@Suite.SuiteClasses(
  Array(
    classOf[TreesTest],
    classOf[TypeTransformerTest]
  )
)
class InSynthLibraryTestSuite 