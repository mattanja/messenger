package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent._
import play.api.libs.json._

import models._
// you need this import to have combinators
import play.api.libs.functional.syntax._

trait Application extends Controller with Secured {
  this: Controller =>

  /**
   * Index page
   */
  def index = Action { implicit request =>
    request.session.get("email") flatMap (email => models.User.findByEmail(email)) map { user =>
      Ok(views.html.index(user))
    } getOrElse {
      Ok(views.html.index(null))
    }
  }

  /**
   * Sample / testing
   * 
   * JSON user data
   * 
   */
  def test() = IsAuthenticated { username => implicit request =>
    models.User.findByEmail(username).map { user =>
      Ok(Json.toJson(user))
    }.getOrElse(Forbidden)
  }

  // -- Authentication

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text) verifying ("Invalid email or password", result => result match {
        case (email, password) => models.User.authenticate(email, password).isDefined
      }))

  /**
   * Login page.
   */
  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => Redirect(routes.List.index).withSession("email" -> user._1))
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out")
  }
}

object Application extends Controller with Application

