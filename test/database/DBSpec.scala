package test.database

import org.specs2.mutable._
import models.User
import play.api.test._
import play.api.test.Helpers._
import service.UserService
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

trait DBFake {
  val memoryDB = inMemoryDatabase("test")
  var fake = {
    new FakeApplication(additionalConfiguration =
      Map("db.default.url" -> memoryDB.get("db.test.url").get)
        ++ memoryDB
        ++ Map("mailserver.port" -> "9025"))
  }
}

class UserSpec extends Specification with DBFake {

  object UserService extends UserService

  "User" should {
    "be retrieved by id(email)" in running(fake) {
      val Some(user) = UserService.findByEmail("kuhnen@terra.com.br")
      user.name must equalTo("kuhnen")
      user.email must equalTo("kuhnen@terra.com.br")
      user.password must equalTo("secret")
    }

    "not be retrieved if does not exit" in running(fake) {
      UserService.findByEmail("not@there.here") must beNone
    }

    "should retrieve all users" in running(fake) {
      val expected = Set("kuhnen", "andre", "matt", "test")
      UserService.table.map(_.name) must beEqualTo(expected)
    }

    "should authenticate if exists" in running(fake) {
      UserService.authenticate("test@test.com.br", "secret") must beSome
    }

    "should not be authenticate if does not exists" in running(fake) {
      UserService.authenticate("testa@test.com.br", "secret") must beNone
    }

    "should not be authenticate if does password is wrong" in running(fake) {
      UserService.authenticate("test@test.com.br", "secreta") must beNone
    }

    "should create user if does not exist" in running(fake) {
      val user = User(UserId(-1), "new@new.com.br", "new", "secret")
      UserService.save(user).id must beGreaterThan(0L)
    }

    "should not create if email exist " in running(fake) {
      val user = User(UserId(-1), "andre@terra.com.br", "asdandre", "secret")
      UserService.save(user).id must beGreaterThan(0L)
    }
  }
}

