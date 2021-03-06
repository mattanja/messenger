package test.email

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import com.typesafe.plugin._
import play.api.Play.current

import test.database.DBFake

class EmailSpec extends PlaySpecification with DBFake {
  "EmailServer" should {
    "send email" in running(fake) {
      val email = use[MailerPlugin].email
      email.setFrom("test@test.com");
      email.setRecipient("info@kernetics.de");
      email.setSubject("SUBJECT GOES HERE");
      email.send("HELLO THERE");
      "Test" must contain("est")
    }
  }
}
