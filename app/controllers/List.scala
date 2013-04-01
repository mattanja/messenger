package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import play.api.libs.concurrent._
import models._
import models.ViewModel._
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
       Ok(views.html.List.index(Mailinglist.findAll, listForm, user))
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
            errors => Promise.pure(BadRequest(views.html.List.index(Mailinglist.findAll, errors, user))),
            email => {
              try {
                Mailinglist.create(email)
                Promise.pure(Redirect(routes.List.index))
              } catch {
                case e: Exception => {
                  Promise.pure(BadRequest(views.html.List.index(Mailinglist.findAll, listForm.withGlobalError(e.toString()), user)))
                }
              }
            })
        }.getOrElse(Promise.pure(Forbidden))
      }
  }

  /**
   * The list detail view.
   */
  def detail(email: String) = IsAuthenticated { username => implicit request =>
      Async {
        models.User.findByEmail(username).map { user =>
          Promise.pure(
	          render {
	            case Accepts.Html() => Ok(views.html.List.detail(email, models.Mailinglist.findByEmailWithUsers(email), user))
	            case Accepts.Json() => Ok(Json.toJson(models.Mailinglist.findByEmailWithUsers(email)))
	          }
		  )
        }.getOrElse(Promise.pure(Forbidden))
      }
  }

  def test = Action {
    Ok(Json.toJson(ListUpdateViewModel("test@test.com", models.User("test@test.com", "", "") :: models.User("test2@test.com", "", "") :: Nil)))
  }

  def update(email: String) = IsAuthenticated { username => implicit request =>
    Async {
      models.User.findByEmail(username).map { user =>
        // JSON
          request.body.asJson.map { json =>
              json.validate(ListUpdateViewModel.fmt).map { m =>
                m.addMembers.map { member =>
                  models.User.create(models.User(member.email, member.name, member.password))
                  MailinglistMembership.create(m.email, member.email)
                }
                Promise.pure(Ok("Updated... " + m.email))
            }.recoverTotal {
              e => Promise.pure(BadRequest("Detected error: " + JsError.toFlatJson(e)))
            }
          }.getOrElse(Promise.pure(BadRequest("no json")))
      }.getOrElse(Promise.pure(BadRequest("List not found")))
    }
  }

  // def update(email: String) = Action(parse.json) { request => {
  //   request.body.validate(ListUpdateViewModel.fmt).map { m =>
  //     Ok("Test: " + m.email)
  //   }.recoverTotal { error =>
  //     BadRequest("Detected error: " + JsError.toFlatJson(error))
  //   }
  // }}

  def addMember() = IsAuthenticated { username => implicit request =>
    Async {
      models.User.findByEmail(username).map { user =>
        // Authenticated async action
        Promise.pure(Ok)
      }.getOrElse(Promise.pure(Forbidden))
    }
  }

  def removeMember(mailinglist_email: String, user_email: String) = IsAuthenticated { username => implicit request =>
    Async {
      models.User.findByEmail(username).map { user =>
        // Authenticated async action

        //MailinglistMembership.delete(mailinglist_email, user_email)

        Promise.pure(Ok)
      }.getOrElse(Promise.pure(Forbidden))
    }
  }

  /**
   *
   */
  def delete(email: String) = Action {
    Mailinglist.delete(email)
    Redirect(routes.List.index)
  }
}
