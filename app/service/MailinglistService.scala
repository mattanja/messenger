package service

import models._

import scala.slick.driver.JdbcDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

/**
 * Queries for Mailinglists.
 */
trait MailinglistQueries {
  def table = TableQuery[Mailinglists]

  protected lazy val byIdQuery = for {
    id <- Parameters[MailinglistId]
    o <- table if o.id === id
  } yield o

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
    o <- table if (o.email like s"%$typeahead%")
  } yield o
}

/**
 * Service for Mailinglists.
 *
 * It brings all base service methods with it from [[org.virtuslab.unicorn.ids.services.BaseIdService]], but you can add yours as well.
 *
 * It's a trait, so you can use your favourite DI method to instantiate/mix it to your application.
 */
trait MailinglistService extends MailinglistQueries {

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
      m <- table if m.email === email
      u <- m.users
    } yield Tuple2(m, u))
  
  /**
   * Get all recipients email addresses for the given list.
   */
  def getRecipientsForList(email: String)(implicit session: Session) =
    (for {
      m <- table if m.email === email
      u <- m.users
    } yield u.email)
    
  /**
   *
   */
  def findByTypeahead(typeahead: String)(implicit session: Session): Seq[Mailinglist] = byTypeaheadQuery(typeahead).list
}
