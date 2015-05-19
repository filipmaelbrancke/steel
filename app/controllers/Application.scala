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
import dao.ExercisesDAO

object Application extends Controller {
  def exerciseDao = new ExercisesDAO

  def index = Action.async {
    val workouts = exerciseDao.findLast(10)
    workouts.map(ex => Ok(views.html.index(ex)))
  }

}
