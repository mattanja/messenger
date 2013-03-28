package email
import org.apache.james.mime4j.message.MessageImpl
import org.specs2.mutable.Specification
import java.util.Date
import org.apache.james.mime4j.field.address.AddressBuilder
import org.apache.james.mime4j.storage.StorageBodyFactory
import org.apache.james.mime4j.message.DefaultMessageWriter
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.io.InputStream
import java.io.ByteArrayInputStream
import play.api.test.Helpers._
import play.api.test.FakeApplication
import models.EmailContents

object EmailParseSpec extends Specification {


    "Email received" should { 
      "be parsed" in running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
    
    val writer = new DefaultMessageWriter()
    println(Message.message.toString)
    val out = new ByteArrayOutputStream()
    writer.writeMessage(Message.message, out)
    val inputStream = new ByteArrayInputStream(out.toString.getBytes)
    val Expected = ("This is a message just to "
                + "say hello.\r\nSo, \"Hello\".")
                
    Expected must equalTo(EmailContents(inputStream).txtBody)
      }
  }
  
object Message {
  // 1) start with an empty message

  val message = new MessageImpl();

     // 2) set header fields

        // Date and From are required fields
        message.setDate(new Date());
        message.setFrom(AddressBuilder.DEFAULT.parseMailbox("John Doe <jdoe@machine.example>"));

        // Message-ID should be present
        message.createMessageId("machine.example");

        // set some optional fields
        message.setTo(AddressBuilder.DEFAULT.parseMailbox("Mary Smith <mary@example.net>"));
        message.setSubject("Saying Hello");

        // 3) set a text body

       val bodyFactory = new StorageBodyFactory();
       val body = bodyFactory.textBody("This is a message just to "
                + "say hello.\r\nSo, \"Hello\".");

        // note that setText also sets the Content-Type header field
        message.setText(body);

        // 4) print message to standard output

     //   val writer = new DefaultMessageWriter();
      //  writer.writeMessage(message, System.out);

        // 5) message is no longer needed and should be disposed of

      //  message.dispose();
    }
} 
