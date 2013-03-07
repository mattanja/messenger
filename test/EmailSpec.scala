
import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import com.typesafe.plugin._
import play.api.Play.current

class EmailSpec extends Specification {
  "EmailServer" should {
   	"send email" in {running(FakeApplication()) {
	   	   val email = use[MailerPlugin].email
	   	   email.addFrom("andrekuhnen@gmail.com");
	   	   email.addRecipient("andrekuhnen@gmail.com");
	   	   email.setSubject("subject");
	   	   email.send("HELLO THERE");
	}   
  }
 }
}