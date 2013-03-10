package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent._
import models._
import org.omg.CosNaming.NamingContextPackage.NotFound

object List extends Controller with Secured {

  /**
   * Define the form object (kind of "view model")
   * http://www.playframework.com/documentation/2.1.0/ScalaForms
   */
  val listForm = Form("email" -> email)

  /**
   *
   */
  def index = IsAuthenticated { username => implicit request =>
      models.User.findByEmail(username).map { user =>
        Ok(views.html.List.index(Mailinglist.all(), listForm, user))
      }.getOrElse(Forbidden)
  }

  /**
   *
   * http://stackoverflow.com/questions/10898719/how-to-handle-exceptions-in-a-playframework-2-async-block-scala
   */
  def newList = IsAuthenticated { username =>
    implicit request =>
      Async {
        models.User.findByEmail(username).map { user =>
          listForm.bindFromRequest.fold(
            errors => Promise.pure(BadRequest(views.html.List.index(Mailinglist.all(), errors, user))),
            email => {
              try {
                Mailinglist.create(email)
                Promise.pure(Redirect(routes.List.index))
              } catch {
                case e: Exception => {
                  Promise.pure(BadRequest(views.html.List.index(Mailinglist.all(), listForm.withGlobalError(e.toString()), user)))
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
  def delete(email: String) = Action {
    Mailinglist.delete(email)
    Redirect(routes.List.index)
  }
}
