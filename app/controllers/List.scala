package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent._
import models._

object List extends Controller with Secured {
  val listForm = Form("name" -> nonEmptyText)

  /**
   *
   */
  def index = IsAuthenticated { username =>
    _ =>
      User.findByEmail(username).map { user =>
        Ok(views.html.index(Mailinglist.all(), listForm, user))
      }.getOrElse(Forbidden)
  }

  /**
   *
   * http://stackoverflow.com/questions/10898719/how-to-handle-exceptions-in-a-playframework-2-async-block-scala
   */
  def newList = IsAuthenticated { username =>
    implicit request =>
      Async {
        User.findByEmail(username).map { user =>
          listForm.bindFromRequest.fold(
            errors => Promise.pure(BadRequest(views.html.index(Mailinglist.all(), errors, user))),
            name => {
              try {
                Mailinglist.create(name)
                Promise.pure(Redirect(routes.List.index))
              } catch {
                case e: Exception => {
                  Promise.pure(BadRequest(views.html.index(Mailinglist.all(), listForm.withGlobalError(e.toString()), user)))
                }
              }
            })
        }.getOrElse(Promise.pure(Forbidden))
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
