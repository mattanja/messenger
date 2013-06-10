package models.JsonModel

import models._
import play.api.libs.json._

/**
 * This is the base trait for JsonResponse models to be handled on the client.
 */
trait JsonResponse {
  def success: Boolean
  def messages: Seq[String]
}

case class ListUpdateResponse(
    mailinglist: Option[Mailinglist],
    success: Boolean = true,
    messages: Seq[String] = Nil
) extends JsonResponse

object ListUpdateResponse {
  implicit val fmt = Json.format[ListUpdateResponse]
}
