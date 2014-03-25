package service

import models._
import scala.slick.driver.H2Driver.simple._
import play.api._

/**
 * Queries for users.
 */
trait UserQueries {
  //def table = TableQuery[Users]


}

/**
 * Service for users.
 * It's a trait, so you can use your favourite DI method to instantiate/mix it to your application.
 */
trait UserService extends UserQueries {

  /**
   * Finds one element by email.
   *
   * @param email email of user
   * @param session implicit session
   * @return Option(User)
   */
  def findByEmail(email: String)(implicit session: Session): Option[User]
    = Models.users.filter(_.email === email).firstOption

  /**
   *
   */
  protected lazy val byEmailQuery = for {
    email <- Parameters[String]
    o <- Models.users if o.email === email
  } yield o

  /**
   *
   */
  def findByTypeahead(typeahead: String)(implicit session: Session): Seq[User] =
  {
  	for {
  		//typeahead <- Parameters[String]
  		user <- Models.users if user.email like s"%$typeahead%" //s"%$typeahead%"
  	} yield user
  }.list

  /**
   *
   */
  def authenticate(email: String, password: String)(implicit session: Session): Option[User]
    = Models.users.filter(_.email === email).filter(_.password === password).firstOption
}