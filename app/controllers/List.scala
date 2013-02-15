package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent._
import models.Mailinglist

object List extends Controller {
  val listForm = Form("name" -> nonEmptyText)

  /**
   *
   */
  def index = Action {
    Ok(views.html.index(Mailinglist.all(), listForm))
  }

  /**
   *
   * http://stackoverflow.com/questions/10898719/how-to-handle-exceptions-in-a-playframework-2-async-block-scala
   */
  def newList = Action { implicit request =>
    Async {
      listForm.bindFromRequest.fold(
        errors => Promise.pure(BadRequest(views.html.index(Mailinglist.all(), errors))),
        name => {
          try {
            Mailinglist.create(name)
            Promise.pure(Redirect(routes.List.index))
          } catch {
            case e: Exception => {
              Promise.pure(BadRequest(views.html.index(Mailinglist.all(), listForm.withGlobalError(e.toString()))))
            }
          }
        })
    }
  }

  /**
   *
   */
  def edit(name: String) = TODO

  /**
   *
   */
  def delete(name: String) = Action {
    Mailinglist.delete(name)
    Redirect(routes.List.index)
  }

}
