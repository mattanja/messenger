package models

import anorm._
import play.api.db._
import play.api.Play.current
import anorm.SqlParser._

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
  
  /* val simple = {
    get[String]("mailinglist_member.mailinglist_email") ~
    get[String]("mailinglist_member.user_email") 
     map {
      case mailinglist_email~user_email => ""
    }
   }
  */
  def create(mailinglist_email: String, user_email: String) {
    DB.withConnection { implicit c =>
      SQL("insert into mailinglist_member values ({mailinglist_email}, {user_email})").
      on('mailinglist_email -> mailinglist_email,
          'user_email -> user_email).executeUpdate()
    }
  }
  
  def findAll: List[(String, String)] = {
    val all = SQL("Select * from mailinglist_member")
    DB.withConnection { implicit c =>
    val wholeTable = all().map(row => 
  row[String]("mailinglist_email") -> row[String]("user_email")
).toList
	wholeTable 
  }
  }
}