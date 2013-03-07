
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Properties
import org.subethamail.smtp.helper.SimpleMessageListener
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import play.api.Logger
import play.api.Play
import org.apache.commons.mail.SimpleEmail
import org.apache.commons.mail.DefaultAuthenticator

// For a more advanced message handling, check https://code.google.com/p/subetha/source/browse/trunk/src/org/subethamail/core/smtp/SMTPHandler.java

/**
 *
 */
class MessengerMessageHandlerFactory extends SimpleMessageListenerAdapter(new MessengerSimpleMessageListener) {
}

/**
 * 
 * Implements the interface org.subethamail.smtp.helper.SimpleMessageListener
 */
class MessengerSimpleMessageListener extends SimpleMessageListener {
  
  /**
   * Implementation of org.subethamail.smtp.helper.SimpleMessageListener.accept
   */
  def accept(from: String, recipient: String): Boolean = {
    Logger.info("Receiving message from: " + from + " for recipient: " + recipient)
    true
  }

  /**
   * Implementation of org.subethamail.smtp.helper.SimpleMessageListener.deliver
   */
  def deliver(from: String, recipient: String, data: InputStream): Unit = {
    val mailData = readInputStream(data)
    Logger.info("Delivering message from: " + from + " for recipient: " + recipient + " with data:" + mailData)

    // TODO: Send mail to members of mailinglist with the name of "recipient"
    // val members = MailinglistMembers.findByMailinglist(recipient)
    // foreach member send mail

    // Just for testing: Return message to sender
    sendMail("messenger@localhost", from, data)
  }

  def sendMail(from: String, to: String, data: InputStream): Unit = {
    Logger.info("sendMail")
    
    // TODO: Replace this whole mail handling using one of these:
    // * http://code.google.com/p/simple-java-mail/wiki/Manual
    // * http://commons.apache.org/proper/commons-email/
    // * http://stackoverflow.com/questions/1574116/best-mail-library-for-java-web-applications
    
    val  email = new SimpleEmail();
    val (host, auth, port) = getProperties
    email.setHostName(host);
    email.setSmtpPort(port);
   
    if (auth)
      email.setAuthenticator(new DefaultAuthenticator("username", "password"));
    
    
   //email.setSSLOnConnect(true);
    email.setFrom("user@gmail.com");
    email.setSubject("TestMail");
    email.setMsg("This is a test mail ... :-)");
    email.addTo("foo@bar.com");
    email.send();
    }

  def getMessage(from: String, to: String, data: InputStream, session: Session): Message = {
    Logger.info("getMessage")
    val msg = new MimeMessage(session)
    msg.setFrom(new InternetAddress(from))
    msg.setRecipients(Message.RecipientType.TO, to)
    msg.setText(readInputStream(data))
    msg
  }

  /**
   * Get Transport with a new session and configuration from app config.
   */
 
  
  private def getProperties = {
    val props = new Properties();
    val host = Play.current.configuration.getString("mail.host").getOrElse("localhost")
    val auth = Play.current.configuration.getBoolean("mail.smtp.auth").getOrElse(false)
    val port = Play.current.configuration.getInt("mail.smtp.port").getOrElse(25).toString
    Logger.logger.debug("Send email properties: HOST: {}", host)
    Logger.logger.debug(" PORT: {}  AUTH: {} ", port,auth)
    (host, auth, port.toInt)
  }
  
  private def createMailAuthenticator(props: Properties) =
    
     if (props.getProperty("mail.smtp.auth").toBoolean)
      javax.mail.Session.getInstance(props)
    else 
       javax.mail.Session.getInstance(props, new javax.mail.Authenticator() {
       override def getPasswordAuthentication(): PasswordAuthentication = {
        new PasswordAuthentication(
          Play.current.configuration.getString("mail.smtp.username").get,
          Play.current.configuration.getString("mail.smtp.password").get)
      }
    }) 
    
    
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
