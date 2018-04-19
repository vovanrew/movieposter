package dto

import play.api.libs.json.Json


case class JwtUser(name: String, role: String) {
  def isAdmin: Boolean = name.toLowerCase == "admin"
}

object JwtUser {

  implicit val userFormat = Json.format[JwtUser]

}