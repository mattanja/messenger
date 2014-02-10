package models

// DB
import scala.slick.driver.H2Driver.simple._

object Models {
	val users = TableQuery[Users]
	val mailinglists = TableQuery[Mailinglists]
	val mailinglistMemberships = TableQuery[MailinglistMemberships]
}