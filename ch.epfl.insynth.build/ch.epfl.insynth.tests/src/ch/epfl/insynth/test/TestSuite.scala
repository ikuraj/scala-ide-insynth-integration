package ch.epfl.insynth.test

import org.junit.runner.RunWith
import org.junit.runners.Suite

import ch.epfl.insynth.test.completion.InSynthCompletionTests

@RunWith(classOf[Suite])
@Suite.SuiteClasses(
  Array(
    classOf[InSynthLibraryTestSuite],
    classOf[CodeGenerationTestSuite],
    classOf[InSynthCompletionTests],
    classOf[BenchmarkTestSuite]
  )
)
class TestSuite 