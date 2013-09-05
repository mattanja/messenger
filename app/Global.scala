import play.api._
import models._
import anorm._
import org.subethamail.smtp.server._
import play.api.db.DB

object Global extends GlobalSettings {

  var smtpServer: SMTPServer = _

  override def onStart(app: Application) {
    Logger.trace("Starting application")
    Logger.trace("Configuration db.default.driver: " + Play.current.configuration.getString("db.default.driver"))
    val port = Play.current.configuration.getInt("mailserver.port").getOrElse(25)
    Logger.trace("Configuration mailserver.port: " + port)

    // Initial data
    InitialData.insert()

    // Actor here, with the Messenger Handler
    smtpServer = new SMTPServer(new MessengerMessageHandlerFactory)
    smtpServer.setPort(port)
    Logger.trace("Starting SMTP-server on port " + smtpServer.getPort() + "...")
    smtpServer.start()
  }

  override def onStop(app: Application) {
    Logger.trace("Stopping application...")

    Logger.trace("Stopping SMTP-server...")
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
      Logger.trace("Inserting initial user data...")
      Seq(
        User("mattanja.kern@gmail.com", "Matt", "secret"),
        User("test@test.test", "test", "secret")).foreach(User.create)
    }
  }
}