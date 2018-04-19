package services

import com.google.inject._
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import repository.{Movie, MovieRepository}

import scala.concurrent.Future

@Singleton
class MovieService @Inject() (movieRepository: MovieRepository) {

  def all: Future[Seq[Movie]] = movieRepository.findAll()

  def getMovie(title: String): Future[Movie] = movieRepository.findByTitle(title)

  def updateDescription(title: String, description: String): Future[UpdateWriteResult] =
    movieRepository.updateDescriptionByTitle(title, description)

  def deleteMovieWithTitle(title: String): Future[WriteResult] = movieRepository.deleteMovieByTitle(title)

}
