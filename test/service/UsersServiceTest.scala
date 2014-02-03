package service

import org.specs2.mutable.Specification
import play.api.test.WithApplication

import models._
import scala.slick.driver.JdbcDriver.simple._

class UsersServiceTest extends Specification {

//  "Users Service" should {
//
//    "save and query users" in new WithApplication {
//      DB.withSession {
//        implicit session =>
//          object UserService extends UserService
//
//          val user = User(UserId(-1), "test@email.com", "Name", "pass")
//          val userId = UserService.table.insert(user)
//          val userOpt = UserService findById userId
//
//          userOpt.map(_.email) must beSome(user.email)
//          userOpt.map(_.name) must beSome(user.name)
//          userOpt.map(_.password) must beSome(user.password)
//          userOpt.flatMap(_.id) must not be_=== None
//      }
//    }
//    
//    "get an empty list" in new WithApplication {
//      DB.withSession {
//        implicit session: Session =>
//          object UserService extends UserService
//          
//          //UserService findAll count < 1 must be true
//          UserService.findAll()
//      }
//    }
//  }
}