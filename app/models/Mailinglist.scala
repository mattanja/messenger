package models

import play.api.db._
import play.api.Play.current
import play.api.Logger
import play.api.libs.json._

// DB
import scala.slick.driver.H2Driver.simple._
import scala.slick.session.Session
import org.virtuslab.unicorn.ids._

/** Id class for type-safe joins and queries. */
case class MailinglistId(id: Long) extends AnyVal with BaseId

object MailinglistId extends IdCompanion[MailinglistId] {
  implicit val fmt = Json.format[MailinglistId]
  //implicit val mailinglistIdType = MappedTypeMapper.base[MailinglistId, Long](_.id, new MailinglistId(_))
}

trait Mailing {
  //val members: List[String]
}

case class Mailinglist(
  id: Option[MailinglistId],
  email: String,
  name: String
)
extends WithId[MailinglistId]
with Mailing

/**
 * This uses ScalaAnorm, packaged with play! framework 2
 * https://github.com/playframework/Play20/wiki/ScalaAnorm
 */
object Mailinglist {
  implicit val fmt = Json.format[Mailinglist]
}

object Mailinglists extends IdTable[MailinglistId, Mailinglist]("Mailinglists") {
  def email = column[String]("email", O.NotNull)
  def name = column[String]("name", O.NotNull)
  def base = email ~ name

  override def * = id.? ~: base <> (Mailinglist.apply _, Mailinglist.unapply _)

  override def insertOne(elem: Mailinglist)(implicit session: Session): MailinglistId =
    saveBase(base, Mailinglist.unapply _)(elem)
    
  def memberships = MailinglistMemberships.filter(_.mailinglist === id)
  def users = MailinglistMemberships.filter(_.mailinglist === id).flatMap(_.userFK)
}

/*
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
    val groupedMembers = groupsAndMembers.groupBy(_._1) mapValues (_.map(_._2))
    groupedMembers.map { case (k, v) => Mailinglist(k, v) }
  }

  val mailinglist =
    get[String]("mailinglist_member.mailinglist_email") ~
      get[String]("mailinglist_member.user_email") map {
        case mailinglist_email ~ user_email => (mailinglist_email, user_email)
      }

  def simple = get[String]("mailinglist.email")

  object EmptyMailinglist extends Mailing {
    val members = List.empty
  }

}
*/