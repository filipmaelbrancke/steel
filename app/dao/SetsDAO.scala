package dao

import scala.concurrent.Future

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

trait SetsComponent { 
  class Sets(tag: Tag) extends Table[Set](tag, "set") {
    
    def id        = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def exercise  = column[Long]("exercise")
    def reps      = column[Long]("reps")
    def completed = column[Long]("completed")
    def createdAt = column[Option[DateTime]]("created_at")
    def updatedAt = column[Option[DateTime]]("updated_at")
    def *         = (id, 
      exercise,
      reps,
      completed, 
      createdAt, 
      updatedAt ) <> (Set.tupled, Set.unapply _)
    
  }
}

class SetsDAO extends SetsComponent {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val sets     = TableQuery[Sets]

  def count(): Future[Int] = 
    dbConfig.db.run(sets.length.result)

  def findLast(limit: Int): Future[List[Set]] =
    dbConfig.db.run(sets.sortBy(_.createdAt.desc).take(limit).result).map(_.toList)


  /** Insert a new set. */
  def insert(set: Set): Future[Unit] =
    dbConfig.db.run(sets += set).map(_ => ())

  /** Insert new sets. */
  def insert(sets: Seq[Set]): Future[Unit] =
    dbConfig.db.run(this.sets ++= sets).map(_ => ())

}
