package dao

import scala.concurrent.Future

import models.Exercise
import models.Person
import models.ExerciseType


import play.api.Play.current
import slick.lifted.Tag
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.db.DB
import slick.driver.PostgresDriver.api._
import com.github.tototoshi.slick.PostgresJodaSupport._
import org.joda.time.DateTime
trait ExercisesComponent { 
  class Exercises(tag: Tag) extends Table[Exercise](tag, "exercise") {
    
    def id        = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name      = column[String]("name")
    def kind      = column[Long]("kind")
    def reps      = column[Long]("reps")
    def weight    = column[Long]("weight")
    def time      = column[Long]("time")
    def notes     = column[String]("notes")
    def person    = column[Long]("person")
    def createdAt = column[Option[DateTime]]("created_at")
    def updatedAt = column[Option[DateTime]]("updated_at")
    def *         = (id, 
      kind, 
      name, 
      reps.?, 
      weight.?, 
      time.?, 
      notes.?, 
      person,
      createdAt, 
      updatedAt ) <> (Exercise.tupled, Exercise.unapply _)
    
  }
}

class ExercisesDAO extends ExercisesComponent {

  private def db: Database = Database.forDataSource(DB.getDataSource())

  val exercises =  TableQuery[Exercises]

  def options: Future[Seq[(String, String)]] = {
    val query = (for {
      exercise <- exercises
    } yield ( exercise.id, exercise.name)).sortBy(_._2)

    db.run(query.result).map(rows => rows.map { case (id, name) => (id.toString, name) })
  }
}
