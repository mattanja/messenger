package models


import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

/**
 * TODO: implement relation between user and mailinglist
 */
case class MailinglistMembership(
    memberemail: String,
    mailinglistemail: String,
    isApproved: Boolean,
    isActive: Boolean
)


object MailinglistMembership {
  
  def create(mailinglist_email: String, user_email: String) {
    DB.withConnection { implicit c =>
      SQL("insert into mailinglist_member values ({mailinglist_email}, {user_email})").
      on('mailinglist_email -> mailinglist_email,
          'user_email -> user_email).executeUpdate()
    }
  }
  
}