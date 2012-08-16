package ch.epfl.insynth.test

import org.junit.runner.RunWith
import org.junit.runners.Suite

import ch.epfl.insynth.test.completion.InSynthCompletionTests
import ch.epfl.insynth.test.completion.InSynthPreferencesTests

@RunWith(classOf[Suite])
@Suite.SuiteClasses(
  Array(
//    classOf[InSynthLibraryTestSuite],
    classOf[CodeGenerationTestSuite]//,
//    classOf[InSynthCompletionTests],
//    classOf[BenchmarkTestSuite],
//    classOf[InSynthPreferencesTests]
  )
)
class TestSuite 