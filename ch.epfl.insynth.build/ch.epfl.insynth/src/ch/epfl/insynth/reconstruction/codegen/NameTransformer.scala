package ch.epfl.insynth.reconstruction.codegen

object NameTransformer extends (String => String) {
  
  def apply(name: String): String = mapper(name)
    
  val mapper = Map[String, String](
    "$hash$hash" -> "##"
  ) withDefault identity
  
}