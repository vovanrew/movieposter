package dto

import play.api.libs.json.{Json, OFormat}
import repository.Movie

case class Poster(movies: Seq[Movie])

object Poster {

  import Movie._

  implicit val posterJsonFormat: OFormat[Poster] = Json.format[Poster]

}