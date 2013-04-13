
import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import com.typesafe.plugin._
import play.api.Play.current
import db.DBFake

class EmailSpec extends Specification with DBFake {
  "EmailServer" should {
    "send email" in running(fake) { //new WithApplication {
      val email = use[MailerPlugin].email
      email.addFrom("andrekuhnen@gmail.com");
      email.addRecipient("kuhnen@list.com.br");
      email.setSubject("SUBJECT GOES HERE");
      email.send("HELLO THERE");
      "Test" must contain("est")
    }
  }
}