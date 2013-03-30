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
  
   
  def create(mailinglist_email: String, user_email: String) {
    DB.withConnection { implicit c =>
      SQL("insert into mailinglist_member values ({mailinglist_email}, {user_email})").
      on('mailinglist_email -> mailinglist_email,
          'user_email -> user_email).executeUpdate()
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
}