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
    def kind      = column[Long]("kind")
    def reps      = column[Long]("reps")
    def weight    = column[Float]("weight", O.SqlType("numeric(4,2)"))
    def time      = column[Float]("time", O.SqlType("numeric(4,2)"))
    def notes     = column[String]("notes")
    def person    = column[Long]("person")
    def createdAt = column[Option[DateTime]]("created_at")
    def updatedAt = column[Option[DateTime]]("updated_at")
    def *         = (id, 
      kind, 
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

  def findLast(limit: Int): Future[Seq[Exercise]] =
    db.run(exercises.sortBy(_.createdAt.desc).take(limit).result)

   /** Insert a new exercise. */
   def insert(exercise: Exercise): Future[Unit] =
     db.run(exercises += exercise).map(_ => ())

   /** Insert new exercises. */
   def insert(exercises: Seq[Exercise]): Future[Unit] =
     db.run(this.exercises ++= exercises).map(_ => ())

}
