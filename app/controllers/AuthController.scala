package controllers

import javax.inject._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import pdi.jwt.JwtSession._
import dto.{JwtUser, RespError}
import services.AuthService
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class AuthController @Inject() (scc: SecuredControllerComponents, authService: AuthService)
                               (implicit ec: ExecutionContext) extends SecuredController(scc) {

  private val loginForm: Reads[(String, String)] =
    ((JsPath \ "email").read[String] and (JsPath \ "password").read[String]).tupled

  def login: Action[JsValue] =  Action.async (parse.json) { implicit request: Request[JsValue] =>
    val result: Future[Result] = request.body
      .validate(loginForm)
      .fold(
        errors => {
          Future(BadRequest(JsError.toJson(errors)))
        }, {
          case (email, password) =>
            authService.authenticate(email, password).transform {
              case Success(account) =>
                Try(Ok.addingToJwtSession("user", JwtUser(account.name, account.role)))

              case Failure(e) =>
                Try(BadRequest(Json.toJson(RespError("Invalid auth data"))))
            }
        }
      )

    result
  }

}
