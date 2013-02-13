package controllers


import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Mailinglist

object List extends Controller {
	val listForm = Form("name" -> nonEmptyText)
  
  def index = Action {
    
    Ok(views.html.index(Mailinglist.all(), listForm))
  }
  
  def newList = Action { implicit request =>
    listForm.bindFromRequest.fold(
        errors => BadRequest(views.html.index(Mailinglist.all(), errors)),
        name => {
          Mailinglist.create(name)
          Redirect(routes.List.index)
        }
    )
    
  }
  
  def edit(name: String) = TODO
  
}
