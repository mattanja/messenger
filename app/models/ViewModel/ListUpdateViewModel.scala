package models.ViewModel

import play.api.libs.json._

case class ListUpdateViewModel(
	email: String,
	addMembers: List[String] = Nil,
	removeMembers: List[String] = Nil
)


object ListUpdateViewModel {
//	implicit val listUpdateViewModelWrites = Json.writes[ListUpdateViewModel]
//	implicit val listUpdateViewModelReads = Json.reads[ListUpdateViewModel]
//	implicit val listUpdateViewModelFormat = Format(listUpdateViewModelReads, listUpdateViewModelWrites)
  implicit val fmt = Json.format[ListUpdateViewModel]
  
//	implicit object ListUpdateViewModelFormat extends Format[ListUpdateViewModel] {
//		def reads(json: JsValue) = JsSuccess(ListUpdateViewModel(
//			(json \ "email").validate[String],
//			(json \ "addMembers").validate[Option[models.User]],
//			(json \ "removeMembers").validate[Option[models.User]]
//		))
//
//		def writes(m: ListUpdateViewModel): JsValue = JsObject(List(
//			"email" -> JsString(m.email),
//			"addMembers" -> JsArray()
//		))
//	}
}
