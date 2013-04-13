package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import play.api.libs.concurrent._
import models._
import models.ViewModel._
//import org.omg.CosNaming.NamingContextPackage.NotFound

object List extends Controller with Secured {

  /**
   * Define the form object (kind of "view model")
   * http://www.playframework.com/documentation/2.1.0/ScalaForms
   */
  val listForm = Form("email" -> email)

  /**
   *
   */
  def index = IsAuthenticated { username =>
    implicit request =>
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
  def detail(email: String) = IsAuthenticated { username =>
    implicit request =>
      Async {
        models.User.findByEmail(username).map { user =>
          Promise.pure(
            render {
              case Accepts.Html() => Ok(views.html.List.detail(email, models.Mailinglist.findByEmailWithUsers(email), user))
              case Accepts.Json() => Ok(Json.toJson(models.Mailinglist.findByEmailWithUsers(email)))
            })
        }.getOrElse(Promise.pure(Forbidden))
      }
  }

  def update(email: String) = IsAuthenticated { username =>
    implicit request =>
      models.User.findByEmail(username).map { user =>
        // JSON
        request.body.asJson.map { json =>
          json.validate(ListUpdateViewModel.fmt).map { m =>
            m.addMembers.map { member =>
              models.User.create(models.User(member.email, member.name, member.password))
              MailinglistMembership.create(m.email, member.email)
            }
            m.removeMembers.map { member =>
              MailinglistMembership.delete(m.email, member.email)
            }
            Ok(Json.toJson(models.Mailinglist.findByEmailWithUsers(email)))
          }.recoverTotal {
            e => BadRequest("Detected error: " + JsError.toFlatJson(e))
          }
        }.getOrElse(BadRequest("no json"))
      }.getOrElse(BadRequest("List not found"))
  }

  // def update(email: String) = Action(parse.json) { request => {
  //   request.body.validate(ListUpdateViewModel.fmt).map { m =>
  //     Ok("Test: " + m.email)
  //   }.recoverTotal { error =>
  //     BadRequest("Detected error: " + JsError.toFlatJson(error))
  //   }
  // }}

  def addMember() = IsAuthenticated { username =>
    implicit request =>
      Async {
        models.User.findByEmail(username).map { user =>
          // Authenticated async action
          Promise.pure(Ok)
        }.getOrElse(Promise.pure(Forbidden))
      }
  }

  def removeMember(mailinglist_email: String, user_email: String) = IsAuthenticated { username =>
    implicit request =>
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
