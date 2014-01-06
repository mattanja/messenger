package models

import java.io.InputStream
import scala.collection.JavaConverters._
import scala.util.parsing.input.StreamReader
import org.apache.james.mime4j.dom.Entity
import org.apache.james.mime4j.dom.Message
import org.apache.james.mime4j.dom.Multipart
import org.apache.james.mime4j.dom.TextBody
import org.apache.james.mime4j.message.BodyPart
import org.apache.james.mime4j.message.DefaultMessageBuilder
import java.nio.CharBuffer
import scala.runtime.ArrayCharSequence
import java.io.ByteArrayOutputStream
import play.api.Logger
import org.apache.james.mime4j.dom.Body

case class EmailContents(
		subject: String,
		txtBody: String,
		htmlBody: String,
		attachments: List[BodyPart])

object EmailContents {

  implicit def entityToMultipart(entity: Body) = entity.asInstanceOf[Multipart]

  def apply(in: InputStream): EmailContents = {
    val builder = new DefaultMessageBuilder();
    val message = builder.parseMessage(in)
    val subject = message.getSubject()
    val (txt, html, attachs) = parse(message)
    message.dispose
    EmailContents(subject, txt.toString(), html.toString(), attachs)
  }

  def parse(mimeMsg: Message) = {
    val txtBody: StringBuffer = new StringBuffer
    val htmlBody: StringBuffer = new StringBuffer
    val attachments: List[BodyPart] = List.empty
    //val subject = mimeMsg.getSubject()

    def parseBodyParts(multipart: Multipart) {

      def parsePart(part: Entity) = part.getMimeType match {
        case "text/plain" =>
          val txt = getTxtPart(part)
          txtBody.append(txt)
        case "text/html" =>
          val html = getTxtPart(part)
          htmlBody.append(html)

        case x: String => println("Qhat!   " + x)

        case _ => if (part.getDispositionType() != null &&
          !part.getDispositionType().equals(""))
          //If DispositionType is null or empty, it means that it's multipart, not attached file
          part :: attachments

      }
      val bodyParts = multipart.getBodyParts().asScala.toList
      bodyParts foreach { part =>
        parsePart(part)
        if (part.isMultipart) parseBodyParts(part.getBody)
      }

    }
    extractParts(mimeMsg, txtBody, parseBodyParts)

    Logger.debug("Text body: " + txtBody.toString());
    Logger.debug("Html body: " + htmlBody.toString());
    (txtBody, htmlBody, attachments)
  }

  private def getTxtPart(part: Entity) = {

    val body = part.getBody().asInstanceOf[TextBody]
    val out = new ByteArrayOutputStream
    body.writeTo(out)
    out

  }

  private def extractParts(mimeMsg: Message, txtBody: StringBuffer,
    parseBodyParts: Multipart => Unit): Any = {
    //If message contains many parts - parse all parts
    if (mimeMsg.isMultipart()) {
      val multipart = mimeMsg.getBody
      parseBodyParts(multipart);
    } else {
      //If it's single part message, just get text body
      val text = getTxtPart(mimeMsg)
      txtBody.append(text);
    }
  }

}
