import play.api._
import models._
import anorm._
import org.subethamail.smtp.server._
import play.api.db.DB

object Global extends GlobalSettings {

  var smtpServer: SMTPServer = _
  var h2webserver: org.h2.server.web.WebServer = _
  
  override def onStart(app: Application) {
    Logger.info("Starting application")
    Logger.info("Configuration db.default.driver: " + Play.current.configuration.getString("db.default.driver"))
    Logger.info("Configuration mail.host: " + Play.current.configuration.getString("smtp.host").getOrElse("INVALID"))
    val port = Play.current.configuration.getInt("smtp.port").getOrElse(66666)
    Logger.info("Configuration mail.host.port: " + port)
    InitialData.insert()
    //Actor here, with the Messenger Handler
    smtpServer = new SMTPServer(new MessengerMessageHandlerFactory)
    smtpServer.setPort(port)
    Logger.info("Starting SMTP-server on port " + smtpServer.getPort() + "...")
    smtpServer.start()

    h2webserver = new org.h2.server.web.WebServer()
    h2webserver.init("-web,-webAllowOthers,true,-webPort,8082")
    h2webserver.start()
    Logger.info("Starting h2 dev server: " + h2webserver.getPort())
  }

  override def onStop(app: Application) {
    Logger.info("Stopping application...")
  
    Logger.info("Stopping SMTP-server...")
    smtpServer.stop()
   
    Logger.info("Stopping H2-webserver...")
    h2webserver.stop()
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