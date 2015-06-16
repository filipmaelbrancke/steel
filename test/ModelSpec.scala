package test
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import scala.concurrent.Await
import scala.concurrent.duration.Duration


import dao.PeopleDAO
import dao.ExercisesDAO
import dao.ExerciseTypesDAO
import dao.SetsDAO

import models.Person
import models.Exercise
import models.ExerciseType
import models.Set

import org.joda.time.DateTime


@RunWith(classOf[JUnitRunner])
class ModelSpec extends Specification {

  import models._

  // -- Date helpers

   def dateIs(date: java.util.Date, str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str

   "ExerciseType model" should {
     def exerciseTypeDao = new ExerciseTypesDAO

     "be retrieved by name" in new WithApplication {
       val exerciseType = Await.result(exerciseTypeDao.findByName("barbell squats"), Duration.Inf).get

       exerciseType.name must equalTo("barbell squats")
       exerciseType.description must equalTo("squats with a weighted barbell across the back of the shoulders")
     }
   }

   "Exercise model" should {
     def exerciseTypeDao = new ExerciseTypesDAO
     def exerciseDao     = new ExercisesDAO
     def personDao       = new PeopleDAO
     def setDao          = new SetsDAO

     "be retrieved by name" in new WithApplication {
       Await.result(personDao.insert(Person(0, "fart@fart.com", "password123", Option(new DateTime()), Option(new DateTime()))), Duration.Inf)

       val person = Await.result(personDao.findByEmail("fart@fart.com"), Duration.Inf).get
       val exerciseType = Await.result(exerciseTypeDao.findByName("barbell squats"), Duration.Inf).get
       exerciseType.name must equalTo("barbell squats")

       person.email must equalTo("fart@fart.com")

       //                                       id workout fk -> type    weight       time
       //                                       notes               fk -> person createdAt updatedAt
       Await.result(exerciseDao.insert(Exercise(0, exerciseType.id, Option(0), Option("Squats a lots"), person.id, Option(new DateTime()), Option(new DateTime()))), Duration.Inf)

       val exercise = Await.result(exerciseDao.findLast(1), Duration.Inf).head
       
       exercise.kind must equalTo(exerciseType.id)
     }

     "be retrieved with ExerciseTypes" in new WithApplication {

       val exercisesWithType = Await.result(exerciseDao.findWithType(1), Duration.Inf)

       exercisesWithType.items must have length(1)

       Option(exercisesWithType.items).map { ex =>
         ex.map {
           case (exercise, exerciseType) => {
             exerciseType.name must equalTo("barbell squats")
           }
         }
       }
     }

     "retrieve workouts" in new WithApplication {

       val person  = Await.result(personDao.findByEmail("fart@fart.com"), Duration.Inf).get
       val squats  = Await.result(exerciseTypeDao.findByName("barbell squats"), Duration.Inf).get
       val bench   = Await.result(exerciseTypeDao.findByName("bench press"), Duration.Inf).get

       // move me to a method

       val exerciseSetMapping :Map[Exercise, Set] = Map(
             Exercise(0, squats.id, Option(0), Option("Squats a lots"), person.id, Option(new DateTime()), Option(new DateTime())) -> Set(0, Option(0), 3, 300, Option(new DateTime()), Option(new DateTime())),
             Exercise(0, bench.id, Option(0), Option("benched"), person.id, Option(new DateTime()), Option(new DateTime())) -> Set(0, Option(0), 3, 300, Option(new DateTime()), Option(new DateTime())),
             Exercise(0, squats.id, Option(0), Option("Squats a lots"), person.id, Option(new DateTime().plusDays(1)), Option(new DateTime().plusDays(1))) -> Set(0, Option(0), 3, 300, Option(new DateTime()), Option(new DateTime())),
             Exercise(0, bench.id, Option(0), Option("benched"), person.id, Option(new DateTime().plusDays(1)), Option(new DateTime().plusDays(1))) -> Set(0, Option(0), 3, 300, Option(new DateTime()), Option(new DateTime()))
           )
       // i should move the set tests into another scenario 
       Await.result(exerciseDao.createWorkout(exerciseSetMapping), Duration.Inf)
       val e = Await.result(exerciseDao.findLast(10), Duration.Inf)
       val s = Await.result(setDao.findLast(10), Duration.Inf)

       e.length must equalTo(5)
       s.length must equalTo(4)

       val dates = Await.result(exerciseDao.dates(), Duration.Inf)
       dates.length must equalTo(2)


       val workouts = Await.result(exerciseDao.workouts(2), Duration.Inf)

       workouts.items must have length(2)

     }
   }
}
