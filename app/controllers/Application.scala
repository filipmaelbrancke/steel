package controllers

import views._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import java.util.concurrent.TimeoutException
import dao.PeopleDAO
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  def index = Action {
    Ok(views.html.index(""))
  }

}
