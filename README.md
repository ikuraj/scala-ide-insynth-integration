Scala IDE InSynth Integration
=============================

Central repository for managing the implementation of InSynth, its Scala IDE integration and documentation.  
Main source code of the InSynth plugin is in the [ch.epfl.insynth.build](https://github.com/kaptoxic/scala-ide-insynth-integration/tree/master/ch.epfl.insynth.build) subdirectory.
For more info about the plugin, visit the [wiki page](https://github.com/kaptoxic/scala-ide-insynth-integration/wiki).

The following gives a summary of subdirectories:

* InSynth_CompilerPlugin  
Scala compiler plugin for analyzing statistics of various Scala source code and deriving weight for using in InSynth synthesis.
* InSynth_HoFSearchPlugin  
Scala compiler plugin for searching for application of high-order functions (useful for demonstration of InSynth correctness).
* SAV_Project_Report  
Project report on the InSynth code generation phase, as a final course report in Sofrware Analysis and Verification, at EPFL.
* __ch.epfl.insynth.build__  
InSynth feature maven build.
* ch.epfl.insynth.reconstruction  
Temporary repository containing the code generation module of InSynth (deprecated and merged into the InSynth maven build).
