package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Mailinglist(
  email: String,
  members: List[String] = List.empty) {

  def add(member: User) {
    MailinglistMembership.create(email, member.email)
  }

  override def toString = "Maillist: " + email + " Members: " + members
}
/**
 * This uses ScalaAnorm, packaged with play! framework 2
 * https://github.com/playframework/Play20/wiki/ScalaAnorm
 */
object Mailinglist {

  val mailinglist = {
    // TODO! http://www.playframework.com/documentation/2.1.0/ScalaAnorm
    get[String]("mailinglist_member.mailinglist_email") ~
      get[String]("mailinglist_member.user_email") map {
        case mailinglist_email ~ user_email => (mailinglist_email, user_email)
      }

  }

  def simple = {
    get[String]("mailinglist.email")
  }

  def allOnlyLists(): List[String] = DB.withConnection {
    implicit c => SQL("SELECT email FROM mailinglist").as(simple *)
  }

  private def groupMembersWithLists(groupsAndMembers: List[(String, String)]) = {
    val groupedMembers = groupsAndMembers.groupBy(_._1) mapValues (_.map(_._2))
    groupedMembers.map { case (k, v) => Mailinglist(k, v) }
  }
  def all(): Iterable[Mailinglist] = DB.withConnection {
    implicit c =>
      groupMembersWithLists(
        SQL("Select * from mailinglist_member").as(mailinglist *))
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
        case List() => None
        case emailList::nill => Option(emailList)
      }
      // TODO: implement members list
    }
  }

}