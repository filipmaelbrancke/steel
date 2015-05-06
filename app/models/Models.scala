package models

import java.util.Date

case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

case class Person(id: Option[Long], email: String, password: String, createdAt: Option[Date], updatedAt: Option[Date])

case class ExerciseType(id: Option[Long], name: String, description: String, createdAt: Option[Date], updatedAt: Option[Date])

case class Exercise(id: Option[Long], kind: Int, name: String, reps: Option[Int], weight: Option[Float], time: Option[Float], notes: Option[String], person: Int, createdAt: Option[Date], updatedAt: Option[Date])
