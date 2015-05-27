package dao

import scala.concurrent.Future

import models.ExerciseType

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import slick.lifted.Tag
import slick.driver.PostgresDriver.api._
import com.github.tototoshi.slick.PostgresJodaSupport._
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

trait ExerciseTypesComponent { 
  class ExerciseTypes(tag: Tag) extends Table[ExerciseType](tag, "exercise_type") {
    def id           = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def kind         = column[String]("kind")
    def name         = column[String]("name")
    def description  = column[String]("description")
    def createdAt    = column[Option[DateTime]]("created_at")
    def updatedAt    = column[Option[DateTime]]("updated_at")
    def *            = (id, kind, name, description, createdAt, updatedAt) <> (ExerciseType.tupled, ExerciseType.unapply _)
  }
}

class ExerciseTypesDAO extends ExerciseTypesComponent with HasDatabaseConfig[JdbcProfile] {

  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val exerciseTypes =  TableQuery[ExerciseTypes]

  def options: Future[Seq[(String, String)]] = {
    val query = (for {
      exercise <- exerciseTypes
    } yield ( exercise.id, exercise.name)).sortBy(_._2)

    dbConfig.db.run(query.result).map(rows => rows.map { case (id, name) => (id.toString, name) })
  }

  def findByName(name: String): Future[Option[ExerciseType]] = 
    dbConfig.db.run(exerciseTypes.filter(_.name === name).result.headOption)

   /** Insert a new exerciseType. */
   def insert(exerciseType: ExerciseType): Future[Unit] =
     dbConfig.db.run(exerciseTypes += exerciseType).map(_ => ())

   /** Insert new exerciseTypes. */
   def insert(exerciseTypes: Seq[ExerciseType]): Future[Unit] =
     dbConfig.db.run(this.exerciseTypes ++= exerciseTypes).map(_ => ())
}
