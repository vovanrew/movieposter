package controllers

import com.google.inject._
import dto.{Poster, RespError}
import play.api.mvc.{Action, Request, Result}
import services.MovieService
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}


class MovieController @Inject() (scc: SecuredControllerComponents, movieService: MovieService)
                                (implicit ec: ExecutionContext) extends SecuredController(scc) {


  import repository.Movie._
  import dto.Poster._


  private val movieForm: Reads[(String, String)] =
    ((JsPath \ "title").read[String] and (JsPath \ "description").read[String]).tupled

  def updateMovieDescription: Action[JsValue] = AdminAction.async (parse.json) { implicit request: Request[JsValue] =>
    val result: Future[Result] = request.body
      .validate(movieForm)
      .fold(
        errors => {
          Future(BadRequest(JsError.toJson(errors)))
        }, {
          case (title, description) =>
            movieService.updateDescription(title, description).transform {
              case Success(result) =>
                Try(Ok)

              case Failure(e) =>
                Try(NotFound(Json.toJson(RespError("Movie could not be found"))))
            }
        }
      )

    result
  }

  def deleteMovie: Action[JsValue] = AdminAction.async (parse.json) { implicit request: Request[JsValue] =>
    val result: Future[Result] = request.body
      .validate(movieForm)
      .fold(
        errors => {
          Future(BadRequest(JsError.toJson(errors)))
        }, {
          case (title, _) =>
            movieService.deleteMovieWithTitle(title).transform {
              case Success(result) =>
                Try(Ok)

              case Failure(e) =>
                Try(NotFound(Json.toJson(RespError("Movie could not be found"))))
            }
        }
      )

    result
  }

  def getMovie: Action[JsValue] =  Action.async (parse.json) { implicit request: Request[JsValue] =>
    val result: Future[Result] = request.body
      .validate(movieForm)
      .fold(
        errors => {
          Future(BadRequest(JsError.toJson(errors)))
        }, {
          case (title, _) =>
            movieService.getMovie(title).transform {
              case Success(movie) =>
                Try(Ok(Json.toJson(movie)))

              case Failure(e) =>
                Try(NotFound(Json.toJson(RespError("Movie could not be found"))))
            }
        }
      )

    result
  }

  def getMovies = Action.async {
    movieService.all.transform {
      case Success(movies) =>
        val poster = Poster(movies)
        Try(Ok(Json.toJson(poster)))

      case Failure(e) =>
        Try(NotFound(Json.toJson(RespError(e.toString/*"Movies could not be found"*/))))
    }
  }

}
