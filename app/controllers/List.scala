/**
 *  Copyright 2013 kernetics
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import play.api.libs.concurrent._
import models._
import models.JsonModel._
//import org.omg.CosNaming.NamingContextPackage.NotFound

import javax.ws.rs.{ QueryParam, PathParam }

import com.wordnik.swagger.annotations._

@Api(value = "/data/list", listingPath = "/api-docs.{format}/list", description = "List operations")
object List extends Controller with Secured {

  /**
   * Define the form object (kind of "view model")
   * http://www.playframework.com/documentation/2.1.0/ScalaForms
   */
  val listForm = Form("email" -> email)

  /**
   * Index view (no data).
   */
  def index = IsAuthenticated { username =>
    implicit request =>
    models.User.findByEmail(username).map { user =>
      Ok(views.html.List.index(user))
    }.getOrElse(Forbidden)
  }

  /**
   * Detail view (no data).
   */
  def detailView(email: String) = IsAuthenticated { username =>
    implicit request =>
    models.User.findByEmail(username).map { user =>
      Ok(views.html.List.detail(user, email))
    }.getOrElse(Forbidden)
  }

  /**
   * List of lists (JSON).
   */
  @ApiOperation(value = "Get mailinglists", notes = "Returns all mailinglists", responseClass = "String", multiValueResponse = true, httpMethod = "GET")
  def list = IsAuthenticated { username =>
    implicit request =>
      Async {
        models.User.findByEmail(username).map { user =>
          Promise.pure(
            render {
              case Accepts.Json() => Ok(Json.toJson(models.Mailinglist.findAll))
            })
        }.getOrElse(Promise.pure(Forbidden))
      }
  }

  /**
   *
   * http://stackoverflow.com/questions/10898719/how-to-handle-exceptions-in-a-playframework-2-async-block-scala
   */
  @ApiOperation(value = "Create new list", notes = "Creates a new mailing list", responseClass = "models.Mailinglist", httpMethod = "GET")
  @ApiErrors(Array(
    new ApiError(code = 400, reason = "List with this email address already exists")))
  @ApiParamsImplicit(Array(
    new ApiParamImplicit(value = "List object with the email address", required = true, dataType = "models.Mailinglist", paramType = "body")))
  def create = IsAuthenticated { username =>
    implicit request =>
      try {
        request.body.asJson.map { json =>
          json.validate(models.Mailinglist.fmt).map { m =>
            if (models.Mailinglist.create(m.email) == 1) {
              Ok(Json.toJson(m))
            } else {
              BadRequest("Error creating new list")
            }
          }.recoverTotal {
            e => BadRequest(Json.toJson(JsError.toFlatJson(e)))
          }
        }.getOrElse(BadRequest("Invalid data"))
      } catch {
        case e: Exception => BadRequest(e.toString())
      }
  }

  /**
   * The list detail view.
   */
  @ApiOperation(value = "Get list details", notes = "Returns the details of the list", responseClass = "Mailinglist", httpMethod = "GET")
  @ApiErrors(Array(
    new ApiError(code = 404, reason = "List not found")))
  def detail(
    @ApiParam(value = "Email of the list")@PathParam("email") email: String) = IsAuthenticated { username =>
    implicit request =>
      Async {
        models.User.findByEmail(username).map { user =>
          Promise.pure(
            render {
              // For regular request render empty view only
              case Accepts.Html() => Ok(views.html.List.detail(user, email)) //, models.Mailinglist.findByEmailWithUsers(email), user))
              // Get data for JSON request
              case Accepts.Json() => Ok(Json.toJson(models.Mailinglist.findByEmailWithUsers(email)))
            })
        }.getOrElse(Promise.pure(Forbidden))
      }
  }

  /**
   * Update the content of a list
   *
   * The request expects an object of type ListUpdateViewModel.
   * Returns ListUpdateResponse
   *
   * @param email the email-address of the list to be updated
   */
  @ApiOperation(value = "Update list", notes = "Returns the new list values", responseClass = "models.JsonModel.ListUpdateResponse", httpMethod = "POST")
  @ApiErrors(Array(
    new ApiError(code = 400, reason = "Invalid ID supplied"),
    new ApiError(code = 404, reason = "List not found")))
  @ApiParamsImplicit(Array(
    new ApiParamImplicit(value = "List object with the information to update", required = true, dataType = "models.JsonModel.ListUpdateViewModel", paramType = "body")))
  def update(
    @ApiParam(value = "Email of the list to update")@PathParam("email") email: String) = IsAuthenticated { username =>
    implicit request =>
      try {
        models.User.findByEmail(username).map { user =>
          models.Mailinglist.findByEmailWithUsers(email).map { currentList =>
            // JSON
            request.body.asJson.map { json =>
              json.validate(ListUpdateViewModel.fmt).map { m =>
                m.addMembers.map { memberEmail =>
                  // Validate member
                  MailinglistMembership.create(
                    m.email,
                    models.User.findByEmail(memberEmail) getOrElse (throw new Exception("No user with this email found: " + memberEmail)))
                }
                m.removeMembers.map { member =>
                  MailinglistMembership.delete(m.email, member)
                }
                Ok(Json.toJson(new ListUpdateResponse(models.Mailinglist.findByEmailWithUsers(email))))
              }.recoverTotal {
                e => BadRequest(Json.toJson(new ListUpdateResponse(Option.empty, false, Seq(e.toString()))))
              }
            }.getOrElse(BadRequest(Json.toJson(new ListUpdateResponse(Option.empty, false, Seq("Invalid JSON data")))))
          }.getOrElse(BadRequest(Json.toJson(new ListUpdateResponse(Option.empty, false, Seq("List not found")))))
        }.getOrElse(BadRequest(Json.toJson(new ListUpdateResponse(Option.empty, false, Seq("User not found")))))
      } catch {
        case e: Exception => BadRequest(Json.toJson(new ListUpdateResponse(Option.empty, false, Seq(e.toString))))
      }
  }

  @ApiOperation(value = "Delete mailing list", notes = "Deletes the specified mailing list", responseClass = "models.Mailinglist", httpMethod = "GET")
  @ApiErrors(Array(
    new ApiError(code = 404, reason = "List not found")))
  def delete(
    @ApiParam(value = "Email of the mailing list to delete")@PathParam("email") email: String) = IsAuthenticated { username =>
    _ => {
      models.Mailinglist.findByEmailWithUsers(email).map { listToDelete =>
        models.Mailinglist.delete(listToDelete.email)
        Ok(Json.toJson(listToDelete))
      }.getOrElse(BadRequest("List not found"))
    }
  }
}
