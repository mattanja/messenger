package database
import org.specs2.mutable._
import play.api.test.Helpers._
import models.Mailinglist
import models.MailinglistMembership

class MailingListSpec extends Specification with DBFake{
  
  "Mailinglist " should {
    "retrieve all mailing lists as lists of string " in running(fake) {
      val expected = Set("kuhnen@list.com.br", "mat@list.com.br")
      Mailinglist.findAll.toSet must beEqualTo(expected)
      
    }
    "retrieve a empty list if there is no Mainlinglist on data base" in running(fake) {
      Mailinglist.delete("kuhnen@list.com.br")
      Mailinglist.delete("mat@list.com.br")
      Mailinglist.findAll must beEmpty
    }
    
    "retrieve all Lists and Users as a List of MailingList" in running(fake) {
      val expected =  Set(Mailinglist("kuhnen@list.com.br", List("test@test.com.br", "kuhnen@terra.com.br")))
      Mailinglist.findAllWithUsers.toSet must beEqualTo(expected)
    }
    
    "retrieve empty List if there is no user in any list" in running(fake) {
      MailinglistMembership.deleteAll
      Mailinglist.findAllWithUsers must beEmpty
    }
    
    "create an new mailinglist if it does not exist" in running(fake) {
      Mailinglist.create("newlist@list.com") must beEqualTo(1)
    }
    
    "not create an mailing if it does exist" in running(fake) {
      Mailinglist.create("kuhnen@list.com.br") must beEqualTo(0)
    }
    
    "return the Some(email) if mailinglist exist" in running(fake) {
      Mailinglist.findByEmail("kuhnen@list.com.br") must beSome 
    }
    
    "return None  if mailinglist does not exist" in running(fake) {
      Mailinglist.findByEmail("asd@asd") must beNone
    }
    
    "return Mailinglist with users" in running(fake) {
      val some = Mailinglist.findByEmailWithUsers("kuhnen@list.com.br")
      some must beSome
      val Some(mailinglist) = some
      mailinglist.members.size must beEqualTo(2)
    }
    "return Mailinglist with empty members" in running(fake) {
      val some = Mailinglist.findByEmailWithUsers("mat@list.com.br")
      some must beSome
      val Some(mailinglist) = some
      mailinglist.members must beEmpty
    }
  }

}