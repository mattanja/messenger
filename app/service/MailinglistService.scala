package service

import models._
import play.api.db.slick.Config.driver.simple._
import org.virtuslab.unicorn.ids.services._

/**
 * Queries for Mailinglists.
 * It brings all base queries with it from [[org.virtuslab.unicorn.ids.services.BaseIdQueries]], but you can add yours as well.
 */
trait MailinglistQueries extends BaseIdQueries[MailinglistId, Mailinglist] {
  override def table = Mailinglists

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
    Mailinglist <- Mailinglists if Mailinglist.email like s"%$typeahead%"
  } yield Mailinglist
}

/**
 * Service for Mailinglists.
 *
 * It brings all base service methods with it from [[org.virtuslab.unicorn.ids.services.BaseIdService]], but you can add yours as well.
 *
 * It's a trait, so you can use your favourite DI method to instantiate/mix it to your application.
 */
trait MailinglistService extends BaseIdService[MailinglistId, Mailinglist] with MailinglistQueries {

  /**
   * Finds one element by email.
   *
   * @param email email of Mailinglist
   * @param session implicit session
   * @return Option(Mailinglist)
   */
  def findByEmail(email: String)(implicit session: Session): Option[Mailinglist] = byEmailQuery(email).firstOption

  /**
   * Finds the mailinglist with the given email and returns a list of tuples
   * with the mailinglist and the users of the list.
   */
  def findByEmailWithUsers(email: String)(implicit session: Session) =
    (for {
      m <- Mailinglists if m.email === email
      u <- m.users
    } yield Tuple2(m, u))
  
  /**
   * Get all recipients email addresses for the given list.
   */
  def getRecipientsForList(email: String)(implicit session: Session) =
    (for {
      m <- Mailinglists if m.email === email
      u <- m.users
    } yield u.email)
    
  /**
   *
   */
  def findByTypeahead(typeahead: String)(implicit session: Session): Seq[Mailinglist] = byTypeaheadQuery(typeahead).list
}
