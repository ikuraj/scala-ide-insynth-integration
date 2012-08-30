package ch.epfl.insynth.test

import org.junit.runner.RunWith
import org.junit.runners.Suite

import ch.epfl.insynth.test.completion.InSynthPreferencesTests

@RunWith(classOf[Suite])
@Suite.SuiteClasses(
  Array(
    classOf[InSynthLibraryTestSuite],
    classOf[CodeGenerationTestSuite],
    classOf[CompletionTestSuite],
    classOf[BenchmarkTestSuite],
    classOf[InSynthPreferencesTests]
  )
)
class TestSuite 