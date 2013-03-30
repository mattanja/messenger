package models

import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import anorm._
import anorm.SqlParser._
import play.api.Logger

case class User(
  email: String,
  name: String,
  password: String)

/**
 * This uses ScalaAnorm, packaged with play! framework 2
 * https://github.com/playframework/Play20/wiki/ScalaAnorm
 */
object User {

  /**
   * JsonFormat
   */
  implicit object UserJsonFormat extends Format[User] {
    def reads(json: JsValue) = JsSuccess(User(
      (json \ "email").as[String],
      (json \ "name").as[String],
      (json \ "password").as[String]))

    def writes(u: User): JsValue = JsObject(List(
      "email" -> JsString(u.email),
      "name" -> JsString(u.name),
      "password" -> JsString(u.password)))
  }

  // -- Parsers

  /**
   * Parse a User from a ResultSet
   */
  val simple = {
    get[String]("user.email") ~
      get[String]("user.name") ~
      get[String]("user.password") map {
        case email ~ name ~ password => User(email, name, password)
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

   def create(user: User): User = {
    DB.withConnection { implicit connection =>
      try {
        SQL("INSERT INTO user values ({email}, {name}, {password})").on(
          'email -> user.email,
          'name -> user.name,
          'password -> user.password).executeUpdate()
          user
      } catch {
        case e: Exception => Logger.error("Could not create new user\n" + e); user
      }
   }
  }

}
