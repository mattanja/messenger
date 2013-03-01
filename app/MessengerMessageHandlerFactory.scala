
import org.subethamail.smtp._
import scala.collection.immutable.Nil
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter
import org.subethamail.smtp.helper.SimpleMessageListener
import java.io.InputStream
import play.api.Logger
import java.io.InputStreamReader
import java.io.BufferedReader

class MessengerMessageHandlerFactory extends SimpleMessageListenerAdapter(new MessengerSimpleMessageListener) {  //extends MessageHandlerFactory {
}

class MessengerSimpleMessageListener extends SimpleMessageListener {
  def accept(from: String, recipient: String): Boolean = {
    Logger.info("Receiving message from: " + from + " for recipient: " + recipient)
    true
  }
  
  def deliver(from: String, recipient: String, data: InputStream): Unit = {
	  Logger.info("Receiving message from: " + from + " for recipient: " + recipient + " with data:" + readInputStream(data))  
  }
  
  def readInputStream(stream: InputStream): String = {
	val is = new InputStreamReader(stream);
	val sb=new StringBuilder();
	val br = new BufferedReader(is);
	var read = br.readLine();

	while(read != null) {
	    //System.out.println(read);
	    sb.append(read);
	    read = br.readLine();
	}
	
	sb.toString();
  }
}
