package dao

import scala.concurrent.Future

import models.Person

import play.api.Play
import slick.lifted.Tag
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.PostgresDriver.api._
import com.github.tototoshi.slick.PostgresJodaSupport._
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

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

  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val people =  TableQuery[People]

  def options: Future[Seq[(String, String)]] = {
    val query = (for {
      person <- people
    } yield ( person.id, person.email)).sortBy(_._2)

    dbConfig.db.run(query.result).map(rows => rows.map { case (id, email) => (id.toString, email) })
  }

  def findByEmail(email: String): Future[Option[Person]] = 
    dbConfig.db.run(people.filter(_.email === email).result.headOption)

  /** Insert a new person. */
  def insert(person: Person): Future[Unit] =
    dbConfig.db.run(people += person).map(_ => ())

  /** Insert new people. */
  def insert(people: Seq[Person]): Future[Unit] =
    dbConfig.db.run(this.people ++= people).map(_ => ())
}
