package models

import anorm._
import play.api.db._
import play.api.Play.current
import anorm.SqlParser._
import play.api.Logger
import play.api.libs.json._

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
  implicit val fmt = Json.format[MailinglistMembership]
  
  def create(mailinglist_email: String, user_email: String) = 
    DB.withConnection { implicit c =>
      try {
      SQL("insert into mailinglist_member values ({mailinglist_email}, {user_email})").
      on('mailinglist_email -> mailinglist_email,
          'user_email -> user_email).executeUpdate()
    } catch {
      case e: Exception => Logger.error("Could not create relationship: " + e); 0
    }
  }
  
  
  def findAll = {
    val all = SQL("Select * from mailinglist_member")
    DB.withConnection { implicit c =>
    val wholeTable = all().map(row => 
  row[String]("mailinglist_email") -> row[String]("user_email")
).toList
	val groupedMembers = wholeTable.groupBy(_._1) mapValues(_.map (_._2))
	groupedMembers.map {case (k,v) => Mailinglist(k,v)}
  }
  }
   //ONLY FOR TEST!!!!!!!!
  def deleteAll = 
    DB.withConnection{ implicit connection =>
      SQL("DELETE FROM mailinglist_member").execute
    }
  
}