package controllers


import com.google.inject._
import dto.JwtUser
import pdi.jwt.JwtSession._
import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class SecuredController @Inject()(scc: SecuredControllerComponents) extends AbstractController(scc) {
  def AdminAction: AdminActionBuilder                 = scc.adminActionBuilder
  def AuthenticatedAction: AuthenticatedActionBuilder = scc.authenticatedActionBuilder
}


class AuthenticatedRequest[A](val user: JwtUser, request: Request[A]) extends WrappedRequest[A](request)


class AuthenticatedActionBuilder @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    request.jwtSession.getAs[JwtUser]("user") match {
      case Some(user) =>
        block(new AuthenticatedRequest[A](user, request)).map(_.refreshJwtSession(request))
      case _ =>
        Future(Unauthorized)
    }
  }
}


class AdminActionBuilder @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    request.jwtSession.getAs[JwtUser]("user") match {
      case Some(user) if user.isAdmin =>
        block(new AuthenticatedRequest(user, request)).map(_.refreshJwtSession(request))
      case Some(_) =>
        Future(Forbidden.refreshJwtSession(request))
      case _ =>
        Future(Unauthorized)
    }
  }
}


case class SecuredControllerComponents @Inject()( adminActionBuilder: AdminActionBuilder,
                                                  authenticatedActionBuilder: AuthenticatedActionBuilder,
                                                  actionBuilder: DefaultActionBuilder,
                                                  parsers: PlayBodyParsers,
                                                  messagesApi: MessagesApi,
                                                  langs: Langs,
                                                  fileMimeTypes: FileMimeTypes,
                                                  executionContext: scala.concurrent.ExecutionContext
                                                ) extends ControllerComponents
