package ch.epfl.insynth.reconstruction.codegen

import insynth.reconstruction.stream._

import ch.epfl.insynth.scala.loader._

object ScalaExtractors {

  object Application {
    def unapply(app: Application) = {
      app.getParams match {
        case Identifier(_, decl: ScalaDeclaration) :: _ => Some((decl.scalaType, app.getParams))
        case Variable(_, _) :: _ => throw new UnsupportedOperationException
        case _ => None
      }
    }
  }

}