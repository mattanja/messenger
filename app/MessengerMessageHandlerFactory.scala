
import org.subethamail.smtp._
import scala.collection.immutable.Nil
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter
import org.subethamail.smtp.helper.SimpleMessageListener
import java.io.InputStream
import play.api.Logger
import play.api.Play
import java.io.InputStreamReader
import java.io.BufferedReader
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Transport
import javax.mail.Message
import javax.mail.internet.MimeMessage
import javax.mail.Session
import javax.mail.internet.InternetAddress
import com.sun.mail.smtp.SMTPMessage
import javax.mail.Message.RecipientType

// For a more advanced message handling, check https://code.google.com/p/subetha/source/browse/trunk/src/org/subethamail/core/smtp/SMTPHandler.java

/**
 *
 */
class MessengerMessageHandlerFactory extends SimpleMessageListenerAdapter(new MessengerSimpleMessageListener) {
}

class MessengerSimpleMessageListener extends SimpleMessageListener {
  def accept(from: String, recipient: String): Boolean = {
    Logger.info("Receiving message from: " + from + " for recipient: " + recipient)
    true
  }

  def deliver(from: String, recipient: String, data: InputStream): Unit = {
    val mailData = readInputStream(data)
    Logger.info("Receiving message from: " + from + " for recipient: " + recipient + " with data:" + mailData)

    sendMail("messenger@localhost", "mattanja.kern@gmail.com", data)
  }

  def sendMail(from: String, to: String, data: InputStream): Unit = {
    Logger.info("sendMail")
    val session = getSession
    val transport = session.getTransport()

    try {
      transport.connect()

      val msg = getMessage(from, to, data, session)
      Logger.info("msg is prepared")

      msg.saveChanges()

      transport.sendMessage(msg, msg.getAllRecipients())
      transport.close()
    } catch {
      case ex: MessagingException => throw new RuntimeException(ex)
    } finally {
      transport.close()
    }
  }

  def getMessage(from: String, to: String, data: InputStream, session: Session): Message = {
    Logger.info("getMessage")
    val msg = new SMTPMessage(session, data)
    msg.setFrom(new InternetAddress(from))
    msg.setRecipients(Message.RecipientType.TO, to)
    msg
  }

  /**
   * Get Transport with a new session and configuration from app config.
   */
  def getSession(): Session = {
    Logger.info("getSession")
    val props = new java.util.Properties();
    props.setProperty("mail.host", Play.current.configuration.getString("mail.host").get);
    props.setProperty("mail.smtp.auth", Play.current.configuration.getBoolean("mail.smtp.auth").get.toString);
    props.setProperty("mail.smtp.port", Play.current.configuration.getInt("mail.smtp.port").get.toString);
    props.setProperty("mail.smtp.starttls.enable", Play.current.configuration.getBoolean("mail.smtp.starttls.enable").get.toString);
    props.setProperty("mail.transport.protocol", Play.current.configuration.getString("mail.transport.protocol").get);
    props.setProperty("mail.debug", Play.current.configuration.getBoolean("mail.debug").get.toString);
    javax.mail.Session.getInstance(props, new javax.mail.Authenticator() {
      override def getPasswordAuthentication(): PasswordAuthentication = {
        new PasswordAuthentication(
          Play.current.configuration.getString("mail.smtp.username").get,
          Play.current.configuration.getString("mail.smtp.password").get)
      }
    })
  }

  def readInputStream(stream: InputStream): String = {
    val is = new InputStreamReader(stream);
    val sb = new StringBuilder();
    val br = new BufferedReader(is);
    var read = br.readLine();

    while (read != null) {
      //System.out.println(read);
      sb.append(read);
      read = br.readLine();
    }

    sb.toString();
  }
}
