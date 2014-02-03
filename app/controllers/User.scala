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

// Use H2Driver to connect to an H2 database
import scala.slick.driver.JdbcDriver.simple._
import scala.slick.lifted._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

import javax.ws.rs.{ QueryParam, PathParam }

import com.wordnik.swagger.annotations._

@Api(value = "/user", description = "User operations")
object User extends Controller with Secured {

  object UserService extends UserService

  /**
   * Index view (no data).
   */
  def index = IsAuthenticated { username =>
    implicit request =>
      UserService.findByEmail(username).map { user =>
        Ok(views.html.User.index(user))
      }.getOrElse(Forbidden)
// Anorm
//    models.User.findByEmail(username).map { user =>
//      Ok(views.html.User.index(user))
//    }.getOrElse(Forbidden)
  }

  /**
   * Get the user information.
   */
  def detailView(email: String) = IsAuthenticated { username =>
    _ => {
      UserService.findByEmail(username).map { user =>
        UserService.findByEmail(email).map { detailUser =>
          Ok(views.html.User.detail(user, detailUser))
        }.getOrElse(play.api.mvc.Results.NotFound("User not found"))
      }.getOrElse(Forbidden("Must be logged in"))
    }
  }

  /**
   * List of users (JSON).
   */
  @ApiOperation(value = "Get users", notes = "Returns all users", httpMethod = "GET")
  def list = Authenticated { implicit request =>
    Ok(Json.toJson(UserService.table.list()))
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
          if (UserService.table.insert(m) > 0) {
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
    @ApiParam(value = "Id of the user to delete")@PathParam("id") id: Long)
      = Authenticated { username =>
        val d = UserService.table.where(_.id === UserId(id)).mutate(_.delete)
        Option(d) match {
          case Some(value) => Ok(Json.toJson(id))
          case None => BadRequest(Json.toJson("User not found"))
        }
  }

  def getUserTypeahead = IsAuthenticated { username =>
    implicit request =>
    try {
      request.body.asJson.map { json =>
        val typeahead = (json \ "typeahead").as[String]
        Ok(Json.toJson(UserService.findByTypeahead(typeahead)))
      }.getOrElse(BadRequest("second json"))
    } catch {
      case e: Exception => BadRequest(e.toString())
    }
  }

}