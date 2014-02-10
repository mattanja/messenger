package models

import play.api.Play.current
import play.api.libs.json._
import play.api.Logger
import play.api.libs.json._
import play.api.db.DB

// DB
import scala.slick.driver.H2Driver.simple._

case class MailinglistMembership(
  mailinglistId: MailinglistId,
  userId: UserId,
  isApproved: Boolean,
  isActive: Boolean
)

object MailinglistMembership {
  implicit val fmt = Json.format[MailinglistMembership]
}

class MailinglistMemberships(tag: Tag) extends Table[MailinglistMembership](tag, "MailinglistMembers") {
  def mailinglistId = column[MailinglistId]("MailinglistId", O.PrimaryKey)
  def userId = column[UserId]("UserId", O.PrimaryKey)
  def isApproved = column[Boolean]("IsApproved")
  def isActive = column[Boolean]("IsActive")
  def mailinglistFK = foreignKey("fk_MailinglistMembership_MailinglistId", mailinglistId, Models.mailinglists)(_.id)
  def userFK = foreignKey("fk_MailinglistMembership_UserId", userId, Models.users)(_.id)

  val createMailinglistMembership = MailinglistMembership.apply _
  def * = (mailinglistId, userId, isApproved, isActive) <> (createMailinglistMembership.tupled, MailinglistMembership.unapply)
}
