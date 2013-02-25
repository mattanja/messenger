import play.api._

import models._
import anorm._
import org.subethamail.smtp.server._

object Global extends GlobalSettings {
  
  override def onStart(app: Application) {
    InitialData.insert()
       
    val handlerFactory = new MessengerMessageHandlerFactory
    val smtpServer = new SMTPServer(handlerFactory)
    smtpServer.setPort(8025)
    smtpServer.start()
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
    
    if(User.findAll.isEmpty) {
      Seq(
        User("mattanja.kern@gmail.com", "Matt", "secret")
      ).foreach(User.create)
    }
    
  }
  
}