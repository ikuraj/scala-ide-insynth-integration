package ch.epfl.insynth.test

import org.junit.runner.RunWith
import org.junit.runners.Suite

import ch.epfl.insynth.test.completion.InSynthPreferencesTests

@RunWith(classOf[Suite])
@Suite.SuiteClasses(
  Array(
    classOf[InSynthLibraryTestSuite],
    classOf[CodeGenerationTestSuite],
    classOf[InSynthCompletionTestSuite],
    classOf[BenchmarkTestSuite],
    classOf[InSynthPreferencesTests]
  )
)
class TestSuite 