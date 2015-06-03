package dao

import scala.concurrent.Future

import models.Exercise
import models.Person
import models.ExerciseType
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

trait ExercisesComponent { 
  class Exercises(tag: Tag) extends Table[Exercise](tag, "exercise") {
    
    def id        = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def workout   = column[Long]("workout")
    def kind      = column[Long]("kind")
    def sets      = column[Long]("sets")
    def reps      = column[Long]("reps")
    def weight    = column[Float]("weight", O.SqlType("numeric(4,2)"))
    def time      = column[Float]("time", O.SqlType("numeric(4,2)"))
    def notes     = column[String]("notes")
    def person    = column[Long]("person")
    def createdAt = column[Option[DateTime]]("created_at")
    def updatedAt = column[Option[DateTime]]("updated_at")
    def *         = (id, 
      workout,
      kind, 
      reps.?, 
      sets.?,
      weight.?, 
      time.?, 
      notes.?, 
      person,
      createdAt, 
      updatedAt ) <> (Exercise.tupled, Exercise.unapply _)
    
  }
}

class ExercisesDAO extends ExercisesComponent with ExerciseTypesComponent {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val exercises     = TableQuery[Exercises]
  val exerciseTypes = TableQuery[ExerciseTypes]

  def count(): Future[Int] = 
    dbConfig.db.run(exercises.length.result)

  def findLast(limit: Int): Future[List[Exercise]] =
    dbConfig.db.run(exercises.sortBy(_.createdAt.desc).take(limit).result).map(_.toList)

  def findWithType(limit: Int): Future[Page[(Exercise, ExerciseType)]] = {
    val withExerciseType = 
      (for {
        (e, et) <- exercises join exerciseTypes on (_.kind === _.id)
      } yield (e, et))
      .take(limit)
    Logger.debug("withExerciseType: " + withExerciseType.result.statements)  
    
    for {
      totalRows <- count()
      list = withExerciseType.result.map { rows => 
        rows.collect { 
          case (exercise, exerciseType) => (exercise, exerciseType)
        }
      }
      result <- dbConfig.db.run(list)
    } yield Page(result, 1, limit, totalRows)
  }

  def dates():Unit = {
    /*val dates = sql"select distinct(created_at::date) from exercise".as[String]
    dbConfig.db.run(dates)
    */
   return ()
  }
  

  /** Insert a new exercise. */
  def insert(exercise: Exercise): Future[Unit] =
    dbConfig.db.run(exercises += exercise).map(_ => ())

  /** Insert new exercises. */
  def insert(exercises: Seq[Exercise]): Future[Unit] =
    dbConfig.db.run(this.exercises ++= exercises).map(_ => ())

}
