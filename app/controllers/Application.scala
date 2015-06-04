package controllers

import views._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import org.joda.time.DateTime

import java.util.concurrent.TimeoutException

import models.Exercise
import dao.PeopleDAO
import dao.ExercisesDAO
import dao.ExerciseTypesDAO

import play.api.i18n.Messages.Implicits._
import Play.current
import play.twirl.api.Content

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {
  def exerciseDao     = new ExercisesDAO
  def exerciseTypeDao = new ExerciseTypesDAO

  val Home = Redirect(routes.Application.index)

  def index = Action.async { implicit request => 
    val workouts = exerciseDao.findWithType(10)
    workouts.map(e => Ok(html.index(e)))
  }

  val workoutForm = Form(
    mapping(
      "id"        -> longNumber,
      "kind"      -> longNumber,
      "sets"      -> optional(longNumber),
      "reps"      -> optional(longNumber),
      "weight"    -> optional(of(floatFormat)),
      "time"      -> optional(of(floatFormat)),
      "notes"     -> optional(text),
      "person"    -> longNumber,
      "createdAt" -> optional(jodaDate),
      "updatedAt" -> optional(jodaDate))(Exercise.apply)(Exercise.unapply))

  def create = Action.async { implicit rs =>
    exerciseTypeDao.options.map(options => Ok(html.createForm(workoutForm, options)))
  }

  def save = Action.async { implicit rs => 
    Logger.debug("save action hit")

    workoutForm.bindFromRequest.fold(
      formWithErrors => exerciseTypeDao.options.map(options => BadRequest(html.createForm(formWithErrors, options))),
      exercise => {
        for {
          _ <- exerciseDao.insert(exercise)
        } yield Home.flashing("success" -> "Exercise added")
      }
    )
  }

}
