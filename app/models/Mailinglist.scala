package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Mailinglist(name: String)

object Mailinglist {

  val mailinglist = {
    // TODO! http://www.playframework.com/documentation/2.1.0/ScalaAnorm
    //get[String]("name") ~
    get[String]("name") map {
      case name => Mailinglist(name)
    }
  }

  def all(): List[Mailinglist] = DB.withConnection {
    implicit c => SQL("select * from mailinglist").as(mailinglist *)
  }
  
  def create(name: String) {
    DB.withConnection { implicit c =>
      SQL("insert into mailinglist (name) values ({name})").on('name -> name).executeUpdate()
    }
  }
  
  def delete(name: String) {
    DB.withConnection { implicit c =>
      SQL("delete from mailinglist where name = {name}").on('name -> name).executeUpdate()
    }
  }
}