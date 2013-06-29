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

import javax.ws.rs.{ QueryParam, PathParam }

import com.wordnik.swagger.annotations._

@Api(value = "/user", listingPath = "/api-docs.{format}/user", description = "User operations")
object User extends Controller with Secured {

  @ApiOperation(value = "Get users", notes = "Returns all users", responseClass = "models.User", multiValueResponse=true, httpMethod = "GET")
  def index = IsAuthenticated { username =>
    implicit request =>
      Async {
        models.User.findByEmail(username).map { user =>
          Promise.pure(
            render {
              case Accepts.Html() => Ok(views.html.User.index(user)) //, models.User.findAll))
              case Accepts.Json() => Ok(Json.toJson(models.User.findAll))
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
    new ApiError(code = 400, reason = "Email already exists"),
    new ApiError(code = 400, reason = "Invalid data")))
  @ApiParamsImplicit(Array(
    new ApiParamImplicit(value = "User object", required = true, dataType = "models.User", paramType = "body")))
  def newUser() = IsAuthenticated { username =>
    implicit request =>
    try {
      request.body.asJson.map { json =>
        json.validate(models.User.fmt).map { m =>
          if (models.User.insert(m) == 1) {
            Ok(Json.toJson(m))
          } else {
            BadRequest("Error creating user")
          }
        }.recoverTotal {
          e => BadRequest(Json.toJson(JsError.toFlatJson(e)))
        }
      }.getOrElse(BadRequest("second json"))
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
  @ApiOperation(value = "Delete user", notes = "Deletes the specified user", responseClass = "User", httpMethod = "GET")
  @ApiErrors(Array(
    new ApiError(code = 404, reason = "User not found")
  ))
  def delete(
    @ApiParam(value = "Email of the user to delete")@PathParam("email") email: String) = IsAuthenticated { username =>
  	_ => {
  	  models.User.findByEmail(email).map { userToDelete =>
  	    models.User.delete(userToDelete.email);
        Ok(Json.toJson(userToDelete))
      }.getOrElse(BadRequest("User not found"))
  	}
  }

  def getUserTypeahead = IsAuthenticated { username =>
    implicit request =>
    try {
      request.body.asJson.map { json =>
        val typeahead = (json \ "typeahead").as[String]
        Ok(Json.toJson(models.User.findByTypeahead(typeahead)))
      }.getOrElse(BadRequest("second json"))
    } catch {
      case e: Exception => BadRequest(e.toString())
    }
  }

}