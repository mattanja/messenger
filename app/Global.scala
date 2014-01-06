import play.api._
import models._
import service._
import anorm._
import org.subethamail.smtp.server._
import play.api.db.DB
import play.api.Play.current

// Database
import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
import scala.slick.lifted.Query

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
    Database.forDataSource(DB.getDataSource()) .withSession {
  	  object UserService extends UserService

  	  // Check for empty table
      if ((for{mt <- Users} yield mt).firstOption.isDefined) {
        Logger.trace("Inserting initial user data...")
        Users.insertAll(
            (User(None, "user1@kernetics.de", "User 1", "secret")),
            (User(None, "user2@kernetics.de", "User 2", "secret")),
            (User(None, "user3@kernetics.de", "User 3", "secret")),
            (User(None, "user4@kernetics.de", "User 4", "secret"))
        )

        if ((for {l <- Mailinglists} yield l).firstOption.isDefined) {
          Logger.trace("Inserting initial list data...")
          Mailinglists.insertAll(
            Mailinglist(None, "list1@kernetics.de", "list1@kernetics.de"),
            Mailinglist(None, "list2@kernetics.de", "list2@kernetics.de"),
            Mailinglist(None, "list3@kernetics.de", "list3@kernetics.de"),
            Mailinglist(None, "list4@kernetics.de", "list4@kernetics.de")
            )

          Logger.trace("Inserting initial list membership data...")
          val q1 = for {
            l <- Mailinglists
            u <- Users
          } yield (l.id, u.id)
          q1.foreach(x => MailinglistMemberships.insertOne(
              MailinglistMembership(None, x._1, x._2, true, true)))
        }
      }
    }
  }
}