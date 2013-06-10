package models.JsonModel

import play.api.libs.json._

/**
 * This is the base trait for JsonResponse models to be handled on the client.
 */
trait JsonResponse {
  def success: Boolean
  def messages: Seq[String]
}
