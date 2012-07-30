InSynth plugin in Scala IDE
==============================

This project contains plugins for building *InSynth* plugin in `Scala IDE`_.

*This is a work in progress. Please submit `issues`_ if you encounter problems.*

`tickets`_

.. _Scala IDE: http://scala-ide.org
.. _issues: https://github.com/kaptoxic/scala-ide-insynth-integration/issues?state=open
.. _tickets: http://scala-ide.org/docs/user/community.html

Building
--------

Maven is used to manage the build process.  The project can be built for Scala IDE 2.0.2 (stable) and master (nightly/2.1.0).

*To build for Scala IDE 2.0.2 (stable), use

  $ mvn clean install -P scala-ide-2.0.2-scala-2.9

*To build for Scala IDE master (nightly/2.1.0), use

  $ mvn clean install -P scala-ide-master-scala-2.9 

InSynth user documentation
==========================

A short user documentation can be found at the project's `Wiki`_ pages.

.. _Wiki: https://github.com/kaptoxic/scala-ide-insynth-integration/wiki
