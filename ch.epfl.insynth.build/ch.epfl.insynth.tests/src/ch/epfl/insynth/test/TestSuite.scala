package ch.epfl.insynth.test

import org.junit.runner.RunWith
import org.junit.runners.Suite

import ch.epfl.insynth.trees.tests.TreesTest
import ch.epfl.insynth.test.completion.InSynthCompletionTests
//import ch.epfl.insynth.test.completion.InSynthBenchmarkCompletionTests
//import ch.epfl.insynth.test.completion.InSynthBenchmarkCompletionParametrizedTests

@RunWith(classOf[Suite])
@Suite.SuiteClasses(
  Array(
    classOf[TreesTest],
    classOf[InSynthCompletionTests]
//    classOf[InSynthBenchmarkCompletionTests],
//    classOf[InSynthBenchmarkCompletionParametrizedTests]
  )
)
class TestSuite 