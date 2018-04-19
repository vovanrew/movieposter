package services

import com.google.inject._
import repository.{Account, AccountRepository}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AuthService @Inject() (accountRepository: AccountRepository) {

  def authenticate(email: String, password: String): Future[Account] = {
    accountRepository.findByEmail(email).map { account =>
      if (account.password == password) account
      else throw new NoSuchElementException
    }
  }
}
