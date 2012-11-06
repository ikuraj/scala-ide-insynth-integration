package github3

/**
 * @see <a href="https://github.com/kaptoxic/scala-ide-insynth-integration/issues/3">Issue #3</a>
 * there is shown among others: new C(0) but not C apply 0 or C(0)
 */

case class C(i: Int)

object X {
  val c: C =  /*!*/
}

// use case added
class Main {
  def c: C =  /*!*/ 
}