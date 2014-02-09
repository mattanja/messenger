package service

import models._
import scala.slick.driver.H2Driver.simple._

/**
 * Queries for users.
 */
trait UserQueries {
  def table = TableQuery[Users]

  /**
   *
   */
  protected lazy val byEmailQuery = for {
    email <- Parameters[String]
    o <- table if o.email === email
  } yield o

  /**
   *
   */
  protected lazy val byTypeaheadQuery = for {
    typeahead <- Parameters[String]
    user <- table if user.email like s"%$typeahead%"
  } yield user
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
  def findByEmail(email: String)(implicit session: Session): Option[User] = byEmailQuery(email).firstOption

  /**
   *
   */
  def findByTypeahead(typeahead: String)(implicit session: Session): Seq[User] = byTypeaheadQuery(typeahead).list

  /**
   *
   */
  def authenticate(email: String, password: String)(implicit session: Session): Option[User]
    = table.filter(_.email === email).filter(_.password === password).firstOption
}