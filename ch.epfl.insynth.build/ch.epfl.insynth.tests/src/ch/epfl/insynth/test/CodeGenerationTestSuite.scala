package ch.epfl.insynth.test

import org.junit.runner.RunWith
import org.junit.runners.Suite

import ch.epfl.insynth.reconstruction._

@RunWith(classOf[Suite])
@Suite.SuiteClasses(
  Array(
    classOf[ReconstructorTest],
    classOf[FeaturesTest],
    classOf[CodeGenerationTests]
  )
)
class CodeGenerationTestSuite 