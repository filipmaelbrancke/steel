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
import play.api.db.DB

trait ExerciseTypesComponent { 
  class ExerciseTypes(tag: Tag) extends Table[ExerciseType](tag, "exercise_type") {
    def id           = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name         = column[String]("name")
    def description  = column[String]("description")
    def createdAt    = column[Option[DateTime]]("created_at")
    def updatedAt    = column[Option[DateTime]]("updated_at")
    def *            = (id,name, description, createdAt, updatedAt) <> (ExerciseType.tupled, ExerciseType.unapply _)
  }
}

class ExerciseTypesDAO extends ExerciseTypesComponent with HasDatabaseConfig[JdbcProfile] {

//  private def db: Database = Database.forDataSource(DB.getDataSource())

  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val exerciseTypes =  TableQuery[ExerciseTypes]

  def options: Future[Seq[(String, String)]] = {
    val query = (for {
      exercise <- exerciseTypes
    } yield ( exercise.id, exercise.name)).sortBy(_._2)

    db.run(query.result).map(rows => rows.map { case (id, name) => (id.toString, name) })
  }

  def findByName(name: String): Future[Option[ExerciseType]] = 
    db.run(exerciseTypes.filter(_.name === name).result.headOption)

   /** Insert a new exerciseType. */
   def insert(exerciseType: ExerciseType): Future[Unit] =
     db.run(exerciseTypes += exerciseType).map(_ => ())

   /** Insert new computers. */
   def insert(exerciseTypes: Seq[ExerciseType]): Future[Unit] =
     db.run(this.exerciseTypes ++= exerciseTypes).map(_ => ())
}
