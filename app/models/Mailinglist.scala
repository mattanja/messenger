package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.Logger

case class Mailinglist(
  email: String,
  members: List[String] = List.empty) {

  def add(member: User) = 
    add(member.email)
  }

  def add(member: String) = {
    MailinglistMembership.create(email, member)
  }
  override def toString = "Maillist: " + email + " Members: " + members
}
/**
 * This uses ScalaAnorm, packaged with play! framework 2
 * https://github.com/playframework/Play20/wiki/ScalaAnorm
 */
object Mailinglist {

  def findAllWithUsers = DB.withConnection {
    implicit c =>
      groupMembersWithLists(
        SQL("Select * from mailinglist_member").as(mailinglist *)).toList
  }

  def findAll = DB.withConnection {
    implicit c => SQL("SELECT email FROM mailinglist").as(simple *)
  }

  def create(email: String) = {
    DB.withConnection { implicit c =>
      try {
        SQL("insert into mailinglist (email) values ({email})").on('email -> email).executeUpdate
      } catch {
        case e: Exception => Logger.error("Mailinglist error" + e); 0
      }
    }
  }

  def delete(email: String) = {
    DB.withConnection { implicit c =>
      SQL("delete from mailinglist where email = {email}").on('email -> email).executeUpdate()
    }
  }

 
  def findByEmail(email: String): Option[String] = {
    DB.withConnection { implicit connection =>
      SQL("select * from mailinglist where email = {email}").on(
        'email -> email).as(Mailinglist.simple.singleOpt)
    }
  }

  def findByEmailWithUsers(email: String): Option[Mailinglist] = {
    DB.withConnection { implicit connection =>
      groupMembersWithLists(SQL("SELECT * FROM mailinglist_member WHERE mailinglist_email = {email}").on(
        'email -> email).as(mailinglist *)) match {
        case List() => if (findByEmail(email).isDefined) Option(Mailinglist(email)) else None
        case emailList :: nill => Option(emailList)
      }
    }
  }

 
 private def groupMembersWithLists(groupsAndMembers: List[(String, String)]) = {
    println(groupsAndMembers + "ASDLIjasd")
    val groupedMembers = groupsAndMembers.groupBy(_._1) mapValues (_.map(_._2))
    groupedMembers.map { case (k, v) => Mailinglist(k, v) }
  }

  val mailinglist =
    get[String]("mailinglist_member.mailinglist_email") ~
      get[String]("mailinglist_member.user_email") map {
        case mailinglist_email ~ user_email => (mailinglist_email, user_email)
      }

  def simple = get[String]("mailinglist.email")

}