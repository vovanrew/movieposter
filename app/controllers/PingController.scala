package controllers

import play.api.mvc._
import com.google.inject._

@Singleton
class PingController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index = Action {
    Ok(views.html.index("pong"))
  }

}
