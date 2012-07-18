package ch.epfl.insynth.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ch.epfl.insynth.test.completion.BlaTests;
import ch.epfl.insynth.test.completion.CompletionTests;
//import ch.epfl.insynth.test.completion.InSynthCompletionTests;
import ch.epfl.insynth.trees.tests.TreesTest;



/**
 * To run this class DO NOT FORGET to set the config.ini in the  "configuration" tab.
 * @author ratiu
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	CompletionTests.class,
	BlaTests.class
//	TreesTest.class
})
class TestsSuite { }
