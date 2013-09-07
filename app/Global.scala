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
    
    // TODO this sql result might become big someday
    if (User.findAll.isEmpty) {
      Logger.trace("Inserting initial user data...")
      val users = Seq(
        User("user1@kernetics.de", "User 1", "secret"),
        User("user2@kernetics.de", "User 2", "secret"),
        User("user3@kernetics.de", "User 3", "secret"),
        User("user4@kernetics.de", "User 4", "secret")
        )
      users.foreach(User.create)

      if (Mailinglist.findAll.isEmpty) {
        Logger.trace("Inserting initial list data...")
        val lists = Seq(
          Mailinglist("list1@kernetics.de"),
          Mailinglist("list2@kernetics.de"),
          Mailinglist("list3@kernetics.de"),
          Mailinglist("list4@kernetics.de")
          )
        lists.foreach(l => Mailinglist.create(l.email))

        if (MailinglistMembership.findAll.isEmpty) {
          Logger.trace("Inserting initial list membership data...")
          for (l <- lists; u <- users) { MailinglistMembership.create(l.email, u.email) }
        }
      }
    }
  }
}