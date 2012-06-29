package insynth.standalone

import insynth.InSynthPlugin
import scala.tools.nsc.{Global, Settings, SubComponent}
import scala.tools.nsc.reporters.{ConsoleReporter, Reporter}

/** This class is a compiler that will be used for running
 *  the plugin in standalone mode.
 */
class PluginRunner(settings: Settings, reporter: Reporter)
extends Global(settings, reporter) {
  def this(settings: Settings) = this(settings, new ConsoleReporter(settings))

  /** The phases to be run.
   *
   *  @todo: Adapt to specific plugin implementation
   */
  override protected def computeInternalPhases() {
    phasesSet += syntaxAnalyzer
    phasesSet += analyzer.namerFactory
    phasesSet += analyzer.typerFactory
    phasesSet += superAccessors			       // add super accessors
    phasesSet += pickler			       // serialize symbol tables
    phasesSet += refchecks			       // perform reference and override checking, translate nested objects

    for (phase <- InSynthPlugin.components(this)) {
      phasesSet += phase
    }
  }

}
