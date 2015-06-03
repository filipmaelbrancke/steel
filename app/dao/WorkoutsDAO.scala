package dao

import scala.concurrent.Future

import models.Workout
import models.Person
import models.Page


import play.api.Play
import slick.lifted.Tag
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.PostgresDriver.api._
import com.github.tototoshi.slick.PostgresJodaSupport._
import org.joda.time.DateTime
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

trait WorkoutsComponent { 
  class Workouts(tag: Tag) extends Table[Workout](tag, "workout") {
    
    def id        = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def person    = column[Long]("person")
    def createdAt = column[Option[DateTime]]("created_at")
    def updatedAt = column[Option[DateTime]]("updated_at")
    def *         = (id, 
      person,
      createdAt, 
      updatedAt ) <> (Workout.tupled, Workout.unapply _)
    
  }
}

class WorkoutsDAO extends WorkoutsComponent with ExercisesComponent {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val workouts  = TableQuery[Workouts]

  val exercises = TableQuery[Exercises]

  def count(): Future[Int] = 
    dbConfig.db.run(workouts.length.result)

  def findLast(limit: Int): Future[List[Workout]] =
    dbConfig.db.run(
      workouts.sortBy(_.createdAt.desc)
              .take(limit).result
    ).map(_.toList)

  /** Insert a new workout. */
  def insert(workout: Workout): Future[Unit] =
    dbConfig.db.run(workouts += workout).map(_ => ())

  /** Insert new workouts. */
  def insert(workouts: Seq[Workout]): Future[Unit] =
    dbConfig.db.run(this.workouts ++= workouts).map(_ => ())

}
