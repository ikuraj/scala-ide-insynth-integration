InSynth plugin in Scala IDE
==============================

This project contains plugins for building **InSynth** plugin in `Scala IDE`_.

*This is a work in progress.* Please submit `tickets`_ if you encounter problems.

.. _Scala IDE: http://scala-ide.org
.. _tickets: https://github.com/kaptoxic/scala-ide-insynth-integration/issues?state=open

Building
--------

The build is based on Maven and Tycho. There are several profiles to account for the different version of our dependencies:

* Scala IDE (milestone or nightly)
* Scala (2.9 or 2.10)
* Eclipse (indigo) 

You should check the existing profiles directly in the project's POM. But let's have an example of how you can compose the different profiles.

Say you want to build InSynth for the Scala IDE nightly bundled with Scala 2.9, for Eclipse indigo. Here is the Maven command you should enter:

  ```mvn -P 2.9.x -P nightly-scala-ide-scala-2.9 -P indigo clean install```

What if instead you wanted to build InSynth against the latest available milestone, instead of against the nightly? That's easy as well:

  ```mvn -P 2.9.x -P dev-scala-ide-indigo-scala-2.9 -P indigo clean install```

InSynth user documentation
==========================

A short user documentation can be found at the project's `Wiki`_ pages.

.. _Wiki: https://github.com/kaptoxic/scala-ide-insynth-integration/wiki
