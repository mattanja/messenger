package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent._
import play.api.libs.json._
import models._
import service._
import org.omg.CosNaming.NamingContextPackage.NotFound
import com.wordnik.swagger.annotations.ApiOperation
import scala.slick.session.Database.threadLocalSession

import javax.ws.rs.{ QueryParam, PathParam }

import com.wordnik.swagger.annotations._

@Api(value = "/user", description = "User operations")
object User extends Controller with Secured {

  /**
   * Index view (no data).
   */
  def index = IsAuthenticated { username =>
    implicit request =>
    models.User.findByEmail(username).map { user =>
      Ok(views.html.User.index(user))
    }.getOrElse(Forbidden)
  }

  /**
   * Get the user information.
   */
  def detailView(email: String) = IsAuthenticated { username =>
    _ =>
      models.User.findByEmail(username).map { user =>
        models.User.findByEmail(email).map { detailUser =>
          Ok(views.html.User.detail(user, detailUser))
        }.getOrElse(play.api.mvc.Results.NotFound("User not found"))
      }.getOrElse(Forbidden("Must be logged in"))
  }

  /**
   * List of users (JSON).
   */
  @ApiOperation(value = "Get users", notes = "Returns all users", httpMethod = "GET")
  def list = Authenticated { implicit request =>
    object UserService extends UserService
    Ok(Json.toJson(UserService.findAll))
//    implicit request =>
//      Async {
//        models.User.findByEmail(username).map { user =>
//          Promise.pure(
//            render {
//              case Accepts.Json() => Ok(Json.toJson(models.User.findAll))
//            })
//        }.getOrElse(Promise.pure(Forbidden))
//      }
  }

  @ApiOperation(value = "Add new user", notes = "Returns the new users details", httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Email already exists"),
    new ApiResponse(code = 400, message = "Invalid data")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(value = "User object", required = true, dataType = "models.User", paramType = "body")))
  def create() = IsAuthenticated { username =>
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
   * Get the user details (JSON).
   */
  def detail(email: String) = TODO

  /**
   * Update user data and lists.
   */
  def update(email: String) = TODO

  /**
   * Delete user.
   */
  @ApiOperation(value = "Delete user", notes = "Deletes the specified user", httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "User not found")
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