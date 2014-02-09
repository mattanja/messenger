package models

import play.api.db._
import play.api.Play.current
import play.api.Logger
import play.api.libs.json._

// DB
import scala.slick.driver.H2Driver.simple._

case class MailinglistId(value: Long) extends MappedTo[Long]

object MailinglistId {
  implicit val fmt = Json.format[MailinglistId]
}

case class Mailinglist(
  id: MailinglistId,
  email: String,
  name: String
)

object Mailinglist {
  implicit val fmt = Json.format[Mailinglist]
}

class Mailinglists(tag: Tag) extends Table[Mailinglist](tag, "Mailinglists") {
  def id = column[MailinglistId]("id", O.PrimaryKey)
  def email = column[String]("email", O.NotNull)
  def name = column[String]("name", O.NotNull)

  val createMailinglist = Mailinglist.apply _
  def * = (id, email, name) <> (createMailinglist.tupled, Mailinglist.unapply)

  def memberships = TableQuery[MailinglistMemberships].filter(_.mailinglistId === id)
  def users = TableQuery[MailinglistMemberships].filter(_.mailinglistId === id).flatMap(_.userFK)
}
