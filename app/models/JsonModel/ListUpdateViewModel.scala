package models.JsonModel

import com.wordnik.swagger.annotations._

import javax.xml.bind.annotation._

import play.api.libs.json._

@XmlRootElement(name = "ListUpdateViewModel")
case class ListUpdateViewModel(
	@XmlElement(name = "email")@ApiParam(value = "List email") email: String,
	@XmlElement(name = "addMembers") addMembers: List[String] = Nil,
	@XmlElement(name = "removeMembers") removeMembers: List[String] = Nil
)

object ListUpdateViewModel {
  implicit val fmt = Json.format[ListUpdateViewModel]
}
