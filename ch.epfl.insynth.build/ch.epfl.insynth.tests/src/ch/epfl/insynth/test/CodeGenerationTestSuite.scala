package ch.epfl.insynth.test

import org.junit.runner.RunWith
import org.junit.runners.Suite

import ch.epfl.insynth.test.reconstructor.ReconstructorTest
import ch.epfl.insynth.test.reconstructor.FeaturesTest

@RunWith(classOf[Suite])
@Suite.SuiteClasses(
  Array(
    classOf[ReconstructorTest],
    classOf[FeaturesTest]
  )
)
class CodeGenerationTestSuite 