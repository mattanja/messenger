package controllers

import play.api._
import play.api.db.slick._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.Play.current
import models._
import service._
import scala.slick.driver.H2Driver.simple._

trait Application extends Controller {
  this: Controller =>

  object UserService extends UserService

  /**
   * Index page
   */
  def index = DBAction { implicit request =>
    request.session.get("email") flatMap (email => UserService.findByEmail(email)) map { user =>
      Ok(views.html.index(user))
    } getOrElse {
      Ok(views.html.index(null))
    }
  }

  // -- Authentication

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text) verifying ("Invalid email or password", result => result match {
        case (email, password) => play.api.db.slick.DB.withSession { implicit session =>
          UserService.authenticate(email, password).isDefined
        }
      }))

  /**
   * Login page.
   */
  def login = DBAction { implicit request =>
    Ok(views.html.login(loginForm))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = DBAction { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => Redirect(routes.List.index).withSession("email" -> user._1))
  }

  /**
   * Logout and clean the session.
   */
  def logout = DBAction {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out")
  }
}

object Application extends Controller with Application

