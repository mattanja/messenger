package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent._
import play.api.libs.json._
import models._
import org.omg.CosNaming.NamingContextPackage.NotFound
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiErrors
import com.wordnik.swagger.annotations.ApiError

object User extends Controller with Secured {

  def index = IsAuthenticated { username =>
    implicit request =>
      Async {
        models.User.findByEmail(username).map { user =>
          Promise.pure(
            render {
              case Accepts.Html() => Ok(views.html.User.index(user, models.User.findAll))
              case Accepts.Json() => Ok(Json.toJson(""))
              //Ok(views.html.User.index() models.User.findAll
            })
        }.getOrElse(Promise.pure(Forbidden))
      }
  }

  /**
   * Get the user information.
   */
  def detail(email: String) = IsAuthenticated { username =>
    _ =>
      models.User.findByEmail(username).map { user =>
        models.User.findByEmail(email).map { detailUser =>
          Ok(views.html.User.detail(user, detailUser))
        }.getOrElse(play.api.mvc.Results.NotFound("User not found"))
      }.getOrElse(Forbidden("Must be logged in"))
  }

  @ApiOperation(value = "Add new user", notes = "Returns the new users details", responseClass = "User", httpMethod = "POST")
  @ApiErrors(Array(
    new ApiError(code = 400, reason = "Email already existing"),
    new ApiError(code = 400, reason = "Invalid data")))
  def newUser() = IsAuthenticated { username =>
    implicit request =>
    try {
      BadRequest("test")
    } catch {
      case e: Exception => BadRequest(e.toString())
    }
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