package dao

import scala.concurrent.Future

import models.ExerciseType

import play.api.Play.current
import slick.lifted.Tag
import java.util.Date
import java.sql.{ Date => SqlDate }
import java.sql.Timestamp
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.db.DB

trait ExerciseTypesComponent { 
  class ExerciseTypes(tag: Tag) extends Table[ExerciseType](tag, "exercise_type") {
    implicit val dateColumnType = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))
    def id           = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name         = column[String]("name")
    def description  = column[String]("description")
    def createdAt = column[Option[Date]]("created_at")
    def updatedAt = column[Option[Date]]("updated_at")
    def *         = (id.?, name, description, createdAt, updatedAt) <> (ExerciseType.tupled, ExerciseType.unapply _)
  }
}

class ExerciseTypesDAO extends ExerciseTypesComponent {

  private def db: Database = Database.forDataSource(DB.getDataSource())

  val exerciseTypes =  TableQuery[ExerciseTypes]

  def options: Future[Seq[(String, String)]] = {
    val query = (for {
      exercise <- exerciseTypes
    } yield ( exercise.id, exercise.name)).sortBy(_._2)

    db.run(query.result).map(rows => rows.map { case (id, name) => (id.toString, name) })
  }
}
