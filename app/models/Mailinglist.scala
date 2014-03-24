package models

import play.api.db._
import play.api.Play.current
import play.api.Logger
import play.api.libs.json._
import play.api.libs.functional.syntax._

// DB
import scala.slick.driver.H2Driver.simple._

case class MailinglistId(value: Long) extends MappedTo[Long]

object MailinglistId {
  implicit val fmt = Json.format[MailinglistId]
}

abstract class MailinglistBase {
  def id: MailinglistId
  def email: String
  def name: String
}

case class Mailinglist(id: MailinglistId, email: String, name: String) extends MailinglistBase
object Mailinglist {
  implicit val fmt = Json.format[Mailinglist]
//  implicit val fmt: Format[Mailinglist] = (
//      (__ \ 'id).format[MailinglistId] and
//      (__ \ 'email).format[String] and
//      (__ \ 'name).format[String]
//  )(Mailinglist.apply, unlift(Mailinglist.unapply))
}

case class MailinglistExtended(
    id: MailinglistId,
    email: String,
    name: String,
    members: List[String] = List.empty) extends MailinglistBase
{
  def memberships = Models.mailinglistMemberships.filter(_.mailinglistId === id)
  def users = Models.mailinglistMemberships.filter(_.mailinglistId === id).flatMap(_.userFK)

  def memberEmails = Models.mailinglistMemberships.filter(_.mailinglistId === id).flatMap(_.user).map(_.email)
}
object MailinglistExtended { implicit val fmt = Json.format[MailinglistExtended] }

class Mailinglists(tag: Tag) extends Table[Mailinglist](tag, "Mailinglists") {
  def id = column[MailinglistId]("id", O.PrimaryKey, O.AutoInc)
  def email = column[String]("email", O.NotNull)
  def name = column[String]("name", O.NotNull)

  val createMailinglist = Mailinglist.apply _
  def * = (id, email, name) <> (createMailinglist.tupled, Mailinglist.unapply)

  def memberships = Models.mailinglistMemberships.filter(_.mailinglistId === id)
  def users = Models.mailinglistMemberships.filter(_.mailinglistId === id).flatMap(_.userFK)
}
