package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent._
import models._
import org.omg.CosNaming.NamingContextPackage.NotFound

object User extends Controller with Secured {

  def index = TODO
  
  /**
   * Get the user information.
   */
  def detail(email: String) = IsAuthenticated { username =>
    _ =>
      models.User.findByEmail(username).map { user =>
        models.User.findByEmail(email).map { detailUser =>
          Ok(views.html.User.index(user, detailUser))
        }.getOrElse(play.api.mvc.Results.NotFound("User not found"))
      }.getOrElse(Forbidden("Must be logged in"))
  }

  /**
   * Update user data and lists.
   */
  def update(email: String) = TODO

  /**
   * Delete user.
   */
  def delete(email: String) = TODO

}