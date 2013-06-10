package database

import org.specs2.mutable

import models.User
import play.api.test.FakeApplication
import play.api.test.Helpers.inMemoryDatabase
import play.api.test.Helpers.running


//NOt good idea email as unique KEY

trait DBFake {
   val memoryDB = inMemoryDatabase("test")
  protected def fake = FakeApplication(additionalConfiguration =
    Map("db.default.url" -> memoryDB.get("db.test.url").get) ++ memoryDB)

 
}

class UserSpec extends mutable.Specification  with DBFake{ //with BeforeExample  {

 
  "User" should {
    "be retrieved by id(email)" in 
      running(fake) {
        val Some(user) = User.findByEmail("kuhnen@terra.com.br")
        user.name must equalTo("kuhnen")
        user.email must equalTo("kuhnen@terra.com.br")
        user.password must equalTo("secret")
    }

    "not be retrieved if does not exit" in running(fake) {
      User.findByEmail("not@there.here") must beNone
      
     }
    
    "should retrieve all users" in running(fake) {
      val expected = Set("kuhnen", "andre", "matt", "test")
      User.findAll.map(_.name).toSet must beEqualTo(expected)
    }
    
    "should authenticate if exists" in running(fake) {
      User.authenticate("test@test.com.br", "secret") must beSome
    }
    
    "should not be authenticate if does not exists" in running(fake) {
      User.authenticate("testa@test.com.br", "secret") must beNone
    }
    
    "should not be authenticate if does password is wrong" in running(fake) {
      User.authenticate("test@test.com.br", "secreta") must beNone
    }
    
    "should create user if does not exist" in running(fake) {
     val user = User("new@new.com.br", "new", "secret")
     User.create(user) must beEqualTo(1)
    }
    
    "should not create if email exist " in running(fake) {
      val user = User("andre@terra.com.br", "asdandre", "secret")
      User.create(user) must beEqualTo(0)
    }
  }

}

