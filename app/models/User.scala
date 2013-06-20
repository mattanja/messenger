package models

import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import anorm._
import anorm.SqlParser._
import play.api.Logger
import play.api.libs.json._

case class User(
  email: String,
  name: String,
  password: String
) {
  if (email.length < 1) {
    throw new Exception("email must be set")
  }
}

/**
 * This uses ScalaAnorm, packaged with play! framework 2
 * https://github.com/playframework/Play20/wiki/ScalaAnorm
 */
object User {

  implicit val fmt = Json.format[User]

  // -- Parsers

  /**
   * Parse a User from a ResultSet
   */
  val simple = {
    get[String]("user.email") ~
    get[String]("user.name") map {
      case email ~ name => User(email, name, "")
    }
  }

  // -- Queries

  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where email = {email}").on(
        'email -> email).as(User.simple.singleOpt)
    }
  }

  /**
   * Retrieve all users.
   */
  def findAll: Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user").as(User.simple *)
    }
  }

  def authenticate(email: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where	email = {email} and password = {password}").on(
        'email -> email,
        'password -> password).as(User.simple.singleOpt)
    }
  }

  def create(user: User): Int = {
    DB.withConnection { implicit connection =>
      try {
        SQL("INSERT INTO user values ({email}, {name}, {password})").on(
          'email -> user.email,
          'name -> user.name,
          'password -> user.password).executeUpdate()
      } catch {
        case e: Exception => Logger.error("Could not create new user\n" + e); 0
      }
    }
  }

  def insert(user: User): Int = {
    DB.withConnection { implicit connection =>
      SQL("INSERT INTO user values ({email}, {name}, {password})").on(
        'email -> user.email,
        'name -> user.name,
        'password -> user.password).executeUpdate()
    }
  }
}
