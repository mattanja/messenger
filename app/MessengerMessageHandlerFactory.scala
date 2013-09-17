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
import play.api.Play.current
import org.apache.commons.mail.SimpleEmail
import org.apache.commons.mail.DefaultAuthenticator
import models.EmailContents
import models.MailinglistMembership
import models.Mailinglist

import com.typesafe.plugin._

// For a more advanced message handling, check https://code.google.com/p/subetha/source/browse/trunk/src/org/subethamail/core/smtp/SMTPHandler.java

/**
 *
 */
class MessengerMessageHandlerFactory extends SimpleMessageListenerAdapter(new MessengerSimpleMessageListener) {

	//Actor  to deal with messages
	//Receive the parsed message
}

/**
 *
 * Implements the interface org.subethamail.smtp.helper.SimpleMessageListener
 */
class MessengerSimpleMessageListener extends SimpleMessageListener {

  /**
   * Implementation of org.subethamail.smtp.helper.SimpleMessageListener.accept
   *
   * Check if mailinglist exists.
   */
  def accept(from: String, recipient: String): Boolean = {
    if (Mailinglist.findByEmail(recipient).nonEmpty) {
      Logger.info("Receiving message from: " + from + " for recipient: " + recipient + " found mailinglist.")
      true
    } else {
      Logger.info("Receiving message from: " + from + " for recipient: " + recipient + " mailinglist not found.")
      false
    }
  }

  /**
   * Implementation of org.subethamail.smtp.helper.SimpleMessageListener.deliver
   */
  def deliver(from: String, recipient: String, data: InputStream): Unit = {
    //Here send the message to be parsed
    //then creates all the necessary copies,  Another actor
    //then send all the messages

    // TODO: Send mail to members of mailinglist with the name of "recipient"
    // val members = MailinglistMembers.findByMailinglist(recipient)
    // foreach member send mail
    // MailinglistMembership.
    // Just for testing: Return message to sender
  	val members = Mailinglist.findUsers(recipient)
  	val emailContents = EmailContents(data);
  	members foreach (sendMail(_, from, emailContents))
  	data.close()
  }

  def sendMail(from: String, to: String, emailContents: EmailContents) {

    // Sample from https://github.com/typesafehub/play-plugins/tree/master/mailer#using-it-from-scala
    val mail = use[MailerPlugin].email
    mail.setSubject(emailContents.subject)
    mail.addRecipient(to)
    //or use a list
    //mail.setBcc(List("Dummy <example@example.org>", "Dummy2 <example@example.org>"):_*)
    mail.addFrom(from)
    //sends html
    //mail.sendHtml("<html>html</html>")
    //sends text/text
    mail.send(emailContents.txtBody)
    //sends both text and html
    //mail.send( "text", "<html>html</html>")

    // Replaced code
    // val email = new SimpleEmail();
    // val (host, auth, port) = getProperties
    // email.setHostName(host);
    // email.setSmtpPort(port);

    // if (auth) {
    //   email.setAuthenticator(new DefaultAuthenticator("username", "password"));
    // }

    // //email.setSSLOnConnect(true);
    // email.setFrom(from);
    // email.setSubject(emailContents.subject);
    // //email.setContent(multilData)//for multi part if needed
    // email.setMsg(emailContents.txtBody);
    // email.addTo(to);
    // email.send();
  }

  private def getProperties = {
    val props = new Properties();
    val host = Play.current.configuration.getString("mail.host").getOrElse("localhost")
    val auth = Play.current.configuration.getBoolean("mail.smtp.auth").getOrElse(false)
    val port = Play.current.configuration.getInt("mail.smtp.port").getOrElse(25).toString
    Logger.logger.debug("Send email properties: HOST: {}", host)
    Logger.logger.debug(" PORT: {}  AUTH: {} ", port, auth)
    (host, auth, port.toInt)
  }

  private def createMailAuthenticator(props: Properties) =
    if (props.getProperty("mail.smtp.auth").toBoolean) {
      javax.mail.Session.getInstance(props)
    } else {
      javax.mail.Session.getInstance(props, new javax.mail.Authenticator() {
        override def getPasswordAuthentication(): PasswordAuthentication = {
          new PasswordAuthentication(
            Play.current.configuration.getString("mail.smtp.username").get,
            Play.current.configuration.getString("mail.smtp.password").get)
        }
      })
    }
}
