package dao

import scala.concurrent.Future

import models.Exercise


import play.api.Play.current
import slick.lifted.Tag
import java.util.Date
import java.sql.{ Date => SqlDate }
import java.sql.Timestamp
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.db.DB

trait ExercisesComponent { 
  class Exercises(tag: Tag) extends Table[Exercise](tag, "exercise") {
    
    implicit val dateColumnType = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))
    // implicit val personMapper = 
    //    MappedColumnType.base[Person, Long](_.id.get, PeopleDAO.findById(_))
    // implicit val kindMapper = 
    //     MappedColumnType.base[ExerciseType, Long](_.id.get, ExerciseTypesDAO.findById(_))
    

    def id        = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name      = column[String]("name")
    def kind      = column[Long]("kind")
    def reps      = column[Long]("reps")
    def weight    = column[Long]("weight")
    def time      = column[Long]("time")
    def notes     = column[String]("notes")
    def person    = column[Long]("person")
    def createdAt = column[Option[Date]]("created_at")
    def updatedAt = column[Option[Date]]("updated_at")
    def *         = (id.?, 
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
