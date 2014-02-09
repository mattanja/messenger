package controllers

import play.api.mvc._
import scala.slick.jdbc.JdbcBackend.SessionDef

// http://www.playframework.com/documentation/2.2.1/ScalaActionsComposition

trait BaseController extends Controller with Secured {
	import play.api.Play.current

  implicit def session: SessionDef = play.api.db.slick.DB.withSession {
    implicit session => {session}
  }

}