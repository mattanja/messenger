package test.database

import models.User
import play.api.test.FakeApplication
import play.api.test.Helpers.inMemoryDatabase
import play.api.test.Helpers.running
import org.specs2.mutable._
import models.Mailinglist
import models.MailinglistMembership

class MailinglistMembershipSpec extends Specification with DBFake {

//  "Mailinglist membership" should {
//    "create a new relation if both exists" in running(fake) {
//      val mailinglist = Mailinglist.findByEmailWithUsers("kuhnen@list.com.br").get
//      mailinglist.add("mattanja@terra.com.br") must beEqualTo(1)
//    }
//  }
//
//  "not create a new relation if maillist does not exist" in running(fake) {
//    MailinglistMembership.create("asd@asd", "asd@coma") must beEqualTo(0)
//  }
//
//  "not create a new relation if user does not exist" in running(fake) {
//    val mailinglist = Mailinglist.findByEmailWithUsers("kuhnen@list.com.br").get
//    mailinglist.add("notexist@exist.com") must beEqualTo(0)
//  }
//
//  "not create a new relation if relation already exist" in running(fake) {
//    val mailinglist = Mailinglist.findByEmailWithUsers("kuhnen@list.com.br").get
//    println("asdal" + mailinglist.members)
//    mailinglist.add("test@test.com.br") must beEqualTo(0)
//
//    mailinglist.add("tesat@test.com.br") must beEqualTo(0)
//  }
}