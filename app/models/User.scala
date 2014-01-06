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
case class UserId(id: Long) extends AnyVal with BaseId

object UserId extends IdCompanion[UserId] {
  implicit val fmt = Json.format[UserId]
  //implicit val userIdType = MappedTypeMapper.base[UserId, Long](_.id, new UserId(_))
}

case class User(
  id: Option[UserId],
  email: String,
  name: String,
  password: String
)
extends WithId[UserId] {
  if (email.length < 1) {
    throw new Exception("email must be set")
  }
}

object User {
  implicit val fmt = Json.format[User]
}

/**
 * Updated using slick
 * (based on example from https://github.com/VirtusLab/activator-play-advanced-slick)
 */
object Users extends IdTable[UserId, User]("Users") {
  def email = column[String]("email", O.NotNull)
  def name = column[String]("name", O.NotNull)
  def password = column[String]("password", O.NotNull)
  def base = email ~ name ~ password

  override def * = id.? ~: base <> (User.apply _, User.unapply _)

  override def insertOne(elem: User)(implicit session: Session): UserId =
    saveBase(base, User.unapply _)(elem)

  def memberships = MailinglistMemberships.filter(_.user === id)
  def mailinglists = memberships.flatMap(_.mailinglistFK)
}
