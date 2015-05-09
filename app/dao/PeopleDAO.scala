package dao

import scala.concurrent.Future

import models.Person

import play.api.Play.current
import slick.lifted.Tag
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.db.DB
import slick.driver.PostgresDriver.api._
import com.github.tototoshi.slick.PostgresJodaSupport._
import org.joda.time.DateTime

trait PeopleComponent { 
  class People(tag: Tag) extends Table[Person](tag, "person") {
    def id        = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def email     = column[String]("email")
    def password  = column[String]("password")
    def createdAt = column[Option[DateTime]]("created_at")
    def updatedAt = column[Option[DateTime]]("updated_at")
    def *         = (id, email, password, createdAt, updatedAt) <> (Person.tupled, Person.unapply _)
  }
}

class PeopleDAO extends PeopleComponent {

  private def db: Database = Database.forDataSource(DB.getDataSource())

  val people =  TableQuery[People]

  def options: Future[Seq[(String, String)]] = {
    val query = (for {
      person <- people
    } yield ( person.id, person.email)).sortBy(_._2)

    db.run(query.result).map(rows => rows.map { case (id, email) => (id.toString, email) })
  }
}
