import play.api._

import models._
import anorm._
import org.subethamail.smtp.server._

object Global extends GlobalSettings {

  val smtpServer = new SMTPServer(new MessengerMessageHandlerFactory)
  smtpServer.setPort(8025)

  override def onStart(app: Application) {
    Logger.info("Starting application")

    Logger.info("Configuration db.default.driver: " + Play.current.configuration.getString("db.default.driver"))
    Logger.info("Configuration mail.host: " + Play.current.configuration.getString("mail.host").getOrElse("INVALID"))
    InitialData.insert()

    Logger.info("Starting SMTP-server on port " + smtpServer.getPort() + "...")
    smtpServer.start()
  }

  override def onStop(app: Application) {
    Logger.info("Stopping application...")

    Logger.info("Stopping SMTP-server...")
    smtpServer.stop()
  }

  //  override def onError(request, ex) {
  //    return notFound
  //  }
}

/**
 * Initial set of data to be imported
 * in the sample application.
 */
object InitialData {

  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)

  def insert() = {
    if (User.findAll.isEmpty) {
      Logger.info("Inserting initial user data...")
      Seq(
        User("mattanja.kern@gmail.com", "Matt", "secret"),
        User("test@test.test", "test", "secret")).foreach(User.create)
    }

  }

}