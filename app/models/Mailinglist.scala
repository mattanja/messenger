package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Mailinglist(email: String)

/**
 * This uses ScalaAnorm, packaged with play! framework 2
 * https://github.com/playframework/Play20/wiki/ScalaAnorm
 */
object Mailinglist {

  val mailinglist = {
    // TODO! http://www.playframework.com/documentation/2.1.0/ScalaAnorm
    //get[String]("name") ~
    get[String]("email") map {
      case email => Mailinglist(email)
    }
  }

  def all(): List[Mailinglist] = DB.withConnection {
    implicit c => SQL("select email from mailinglist").as(mailinglist *)
  }
  
  def create(email: String) {
    DB.withConnection { implicit c =>
      SQL("insert into mailinglist (email) values ({email})").on('email -> email).executeUpdate()
    }
  }
  
  def delete(email: String) {
    DB.withConnection { implicit c =>
      SQL("delete from mailinglist where email = {email}").on('email -> email).executeUpdate()
    }
  }
  
  /**
   * Retrieve a Mailinglist from email.
   */
  def findByEmail(email: String): Option[Mailinglist] = {
    DB.withConnection { implicit connection =>
      SQL("select * from mailinglist where email = {email}").on(
        'email -> email
      ).as(Mailinglist.mailinglist.singleOpt)
    }
  }
  
}