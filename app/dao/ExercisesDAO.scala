package dao

import scala.concurrent.Future

import models.Exercise
import models.Person
import models.ExerciseType
import models.Set
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
import scala.collection.mutable.ListBuffer

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait ExercisesComponent { 
  class Exercises(tag: Tag) extends Table[Exercise](tag, "exercise") {
    
    def id        = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def kind      = column[Long]("kind")
    def time      = column[Float]("time", O.SqlType("numeric(4,2)"))
    def notes     = column[String]("notes")
    def person    = column[Long]("person")
    def createdAt = column[Option[DateTime]]("created_at")
    def updatedAt = column[Option[DateTime]]("updated_at")
    def *         = (
      id, 
      kind, 
      time.?, 
      notes.?, 
      person,
      createdAt, 
      updatedAt ) <> (Exercise.tupled, Exercise.unapply _)
    
  }
}

class ExercisesDAO extends ExercisesComponent with ExerciseTypesComponent 
                                              with SetsComponent 
                                              with PeopleComponent {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val exercises     = TableQuery[Exercises]
  val exerciseTypes = TableQuery[ExerciseTypes]
  val sets          = TableQuery[Sets]

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

  /** Insert a new exercise. */
  def insert(exercise: Exercise): Future[Unit] =
    dbConfig.db.run(exercises += exercise).map(_ => ())

  /** Insert new exercises. */
  def insert(exercises: Seq[Exercise]): Future[Unit] =
    dbConfig.db.run(this.exercises ++= exercises).map(_ => ())

  def createWorkout(workoutMapping: Map[Exercise, Set]): Future[Unit] = {
    // split apart the Map into its individual parts
    val s      = new ListBuffer[Set]
    workoutMapping.keys.foreach { exercise => 
      // create the exercise before we create the Set object
      // since we rely on the exercise ID
      val exerciseId = Await.result(dbConfig.db.run((exercises returning exercises.map(_.id)) += exercise), Duration.Inf)
      
      // update the exercise ID in the Set object
      val set = workoutMapping(exercise)
      s += Set(0, Option(exerciseId), set.reps, set.weight, Option(new DateTime()), Option(new DateTime()))
    }

    // create the sets
    dbConfig.db.run(this.sets ++= s.toSeq).map(_ => ())
  }

  def dates(): Future[Seq[String]] =
    dbConfig.db.run(sql"""select distinct(created_at::date) from exercise""".as[String])

  def workouts(limit: Int): Future[Page[(Exercise, ExerciseType, Set)]] = {
    val workoutData = (for( 
      e  <- exercises;
      et <- exerciseTypes if e.kind === et.id;
      s  <- sets          if s.exercise === e.id
    ) yield (e, et, s)).take(limit)

    Logger.debug("workouts: " + workoutData.result.statements)  
    for {
      totalRows <- count()
      list = workoutData.result.map { rows => 
        rows.collect { 
          case (exercise, exerciseType, set) => (exercise, exerciseType, set)
        }
      }
      result <- dbConfig.db.run(list)
    } yield Page(result, 1, limit, totalRows)
  }

}
