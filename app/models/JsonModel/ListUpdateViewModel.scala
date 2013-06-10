package models.JsonModel

import play.api.libs.json._

case class ListUpdateViewModel(
	email: String,
	addMembers: List[String] = Nil,
	removeMembers: List[String] = Nil
)

object ListUpdateViewModel {
  implicit val fmt = Json.format[ListUpdateViewModel]
}
