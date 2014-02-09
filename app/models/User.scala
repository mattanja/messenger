package models

import play.api.db._
import play.api.Play.current
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{ QueryStringBindable, PathBindable }

// DB
//import scala.slick.lifted.{MappedTypeMapper,BaseTypeMapper,TypeMapperDelegate}
import scala.slick.driver.H2Driver.simple._

/** Id class for type-safe joins and queries. */
case class UserId(value: Long) extends MappedTo[Long]

object UserId {
  implicit val fmt = Json.format[UserId]
}

case class User(
  id: UserId,
  email: String,
  name: String,
  password: String
)
{
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
class Users(tag: Tag) extends Table[User](tag, "Users") {
  def id = column[UserId]("id", O.PrimaryKey, O.AutoInc)
  def email = column[String]("email", O.NotNull)
  def name = column[String]("name", O.NotNull)
  def password = column[String]("password", O.NotNull)

  val createUser = User.apply _
  def * = (id, email, name, password) <> (createUser.tupled, User.unapply)

  //def memberships = MailinglistMemberships.filter(_.user === id)
  //def mailinglists = memberships.flatMap(_.mailinglistFK)
}
