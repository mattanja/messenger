package models

import play.api.Play.current
import play.api.libs.json._
import play.api.Logger
import play.api.libs.json._
import play.api.db.DB

// DB
import scala.slick.driver.H2Driver.simple._
import scala.slick.session.Session
import org.virtuslab.unicorn.ids._

/** Id class for type-safe joins and queries. */
case class MailinglistMembershipId(id: Long) extends AnyVal with BaseId

object MailinglistMembershipId extends IdCompanion[MailinglistMembershipId] {
  implicit val fmt = Json.format[MailinglistMembershipId]
  implicit val mailinglistMembershipIdType = MappedTypeMapper.base[MailinglistMembershipId, Long](_.id, new MailinglistMembershipId(_))
}

case class MailinglistMembership(
  id: Option[MailinglistMembershipId],
  mailinglist: MailinglistId,
  user: UserId,
  isApproved: Boolean,
  isActive: Boolean
) extends WithId[MailinglistMembershipId]

object MailinglistMembership {
  implicit val fmt = Json.format[MailinglistMembership]
}

object MailinglistMemberships extends IdTable[MailinglistMembershipId, MailinglistMembership]("MailinglistMembers") {
  def mailinglist = column[MailinglistId]("MailinglistId")
  def user = column[UserId]("UserId")
  def isApproved = column[Boolean]("IsApproved")
  def isActive = column[Boolean]("IsActive")
  def mailinglistFK = foreignKey("fk_mailinglist", mailinglist, Mailinglists)(_.id)
  def userFK = foreignKey("fk_user", user, Users)(_.id)

  def base = mailinglist ~ user ~ isApproved ~ isActive

  override def * = id.? ~: base <> (MailinglistMembership.apply _, MailinglistMembership.unapply _)

  override def insertOne(elem: MailinglistMembership)(implicit session: Session): MailinglistMembershipId =
    saveBase(base, MailinglistMembership.unapply _)(elem)
}

/*
  def create(mailinglist_email: String, user_email: String) = DB.withConnection { implicit c =>
    try {
      SQL("insert into mailinglist_member values ({mailinglist_email}, {user_email})").
        on('mailinglist_email -> mailinglist_email,
          'user_email -> user_email).executeUpdate()
    } catch {
      case e: Exception => Logger.error("Could not create relationship: " + e); 0
    }
  }

  def create(mailinglist_email: String, user: User) = DB.withConnection { implicit c =>
    try {
      SQL("insert into mailinglist_member values ({mailinglist_email}, {user_email})").
        on('mailinglist_email -> mailinglist_email,
          'user_email -> user.email).executeUpdate()
    } catch {
      case e: Exception => Logger.error("Could not create relationship: " + e); 0
    }
  }

  def delete(mailinglist_email: String, user_email: String) = DB.withConnection { implicit c =>
    try {
      SQL("delete from mailinglist_member where mailinglist_email = {mailinglist_email} and user_email = {user_email}").
        on('mailinglist_email -> mailinglist_email,
          'user_email -> user_email).executeUpdate()
    } catch {
      case e: Exception => Logger.error("Could not delete relation: " + e); 0
    }
  }

  def findAll = {
    val all = SQL("Select * from mailinglist_member")
    DB.withConnection { implicit c =>
      val wholeTable = all().map(row =>
        row[String]("mailinglist_email") -> row[String]("user_email")).toList
      val groupedMembers = wholeTable.groupBy(_._1) mapValues (_.map(_._2))
      groupedMembers.map { case (k, v) => Mailinglist(k, v) }
    }
  }

  //ONLY FOR TEST!!!!!!!!
  def deleteAll =
    DB.withConnection { implicit connection =>
      SQL("DELETE FROM mailinglist_member").execute
    }
}
*/