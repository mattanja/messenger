package service

import models._
import play.api.db.slick.Config.driver.simple._
import org.virtuslab.unicorn.ids.services._

/**
 * Queries for users.
 * It brings all base queries with it from [[org.virtuslab.unicorn.ids.services.BaseIdQueries]], but you can add yours as well.
 */
trait UserQueries extends BaseIdQueries[UserId, User] {
  override def table = Users

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
    user <- Users if user.email like s"%$typeahead%"
  } yield user
}

/**
 * Service for users.
 *
 * It brings all base service methods with it from [[org.virtuslab.unicorn.ids.services.BaseIdService]], but you can add yours as well.
 *
 * It's a trait, so you can use your favourite DI method to instantiate/mix it to your application.
 */
trait UserService extends BaseIdService[UserId, User] with UserQueries {

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
    = Query(Users).filter(_.email === email).filter(_.password === password).firstOption
}