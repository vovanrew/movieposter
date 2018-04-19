package dto

import play.api.libs.json.Json

case class RespError(reason: String)

object RespError {

  implicit def respErrorFormat = Json.format[RespError]

}
