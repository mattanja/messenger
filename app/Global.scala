import play.api._
import models._
import service._
import org.subethamail.smtp.server._
import play.api.Play.current

// Database
import play.api.db.slick.DB
import scala.slick.driver.H2Driver.simple._

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

  //val db = Database.forURL(url, driver = "org.h2.Driver")

  def insert() = {
    Logger.info("InitialData.insert - checking data")

    DB.withSession {
      implicit session => {
        //object UserService extends UserService

        val users = TableQuery[Users]

        // Check for empty table
        if (!(for{mt <- users} yield mt).firstOption.isDefined) {
          Logger.trace("Inserting initial user data...")
          users.insertAll(
            User(UserId(-1), "user1@kernetics.de", "User 1", "secret"),
            User(UserId(-1), "user2@kernetics.de", "User 2", "secret"),
            User(UserId(-1), "user3@kernetics.de", "User 3", "secret"),
            User(UserId(-1), "user4@kernetics.de", "User 4", "secret")
          )
        }

        val mailinglists = TableQuery[Mailinglists]
        if (!(for {l <- mailinglists} yield l).firstOption.isDefined) {
          Logger.trace("Inserting initial list data...")
          mailinglists.insertAll(
            Mailinglist(MailinglistId(-1), "list1@kernetics.de", "list1@kernetics.de"),
            Mailinglist(MailinglistId(-1), "list2@kernetics.de", "list2@kernetics.de"),
            Mailinglist(MailinglistId(-1), "list3@kernetics.de", "list3@kernetics.de"),
            Mailinglist(MailinglistId(-1), "list4@kernetics.de", "list4@kernetics.de")
          )

          val memberships = TableQuery[MailinglistMemberships]
          Logger.trace("Inserting initial list membership data...")
          val q1 = for {
            l <- mailinglists
            u <- users
          } memberships += MailinglistMembership(l.id, u.id, true, true)
        }
      }
    }
  }
}