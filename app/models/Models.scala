package models
import org.joda.time.DateTime

case class Page[A](items: Seq[A], page: Long, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

case class Person(id: Long, email: String, password: String, createdAt: Option[DateTime], updatedAt: Option[DateTime])

case class ExerciseType(id: Long, name: String, description: String, createdAt: Option[DateTime], updatedAt: Option[DateTime])

case class Exercise(id: Long, kind: Long, name: String, reps: Option[Long], weight: Option[Long], time: Option[Long], notes: Option[String], person: Long, createdAt: Option[DateTime], updatedAt: Option[DateTime])
