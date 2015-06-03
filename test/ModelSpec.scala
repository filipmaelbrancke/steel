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
import dao.WorkoutsDAO

import models.Person
import models.Exercise
import models.ExerciseType
import models.Workout

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
     def workoutDao      = new WorkoutsDAO

     "be retrieved by name" in new WithApplication {
       Await.result(personDao.insert(Person(0, "fart@fart.com", "password123", Option(new DateTime()), Option(new DateTime()))), Duration.Inf)

       val person = Await.result(personDao.findByEmail("fart@fart.com"), Duration.Inf).get
       Await.result(workoutDao.insert(Workout(0, person.id, Option(new DateTime()), Option(new DateTime()))), Duration.Inf)
       val workout      = Await.result(workoutDao.findLast(1), Duration.Inf).head
       val exerciseType = Await.result(exerciseTypeDao.findByName("barbell squats"), Duration.Inf).get
       exerciseType.name must equalTo("barbell squats")

       person.email must equalTo("fart@fart.com")

       //                                       id workout fk -> type    sets   reps       weight       time
       //                                       notes               fk -> person createdAt updatedAt
       Await.result(exerciseDao.insert(Exercise(0, workout.id, exerciseType.id, Option(3), Option(3), Option(285), Option(0), Option("Squats a lots"), person.id, Option(new DateTime()), Option(new DateTime()))), Duration.Inf)

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
             exercise.sets must equalTo(Some(3))
             exercise.reps must equalTo(Some(3))
             exercise.weight must equalTo(Some(285))
           }
         }
       }
     }

     "retrieve workouts" in new WithApplication {

       val person  = Await.result(personDao.findByEmail("fart@fart.com"), Duration.Inf).get
       Await.result(workoutDao.insert(Workout(0, person.id, Option(new DateTime()), Option(new DateTime()))), Duration.Inf)
       val squats  = Await.result(exerciseTypeDao.findByName("barbell squats"), Duration.Inf).get
       val bench   = Await.result(exerciseTypeDao.findByName("bench press"), Duration.Inf).get
       val workout = Await.result(workoutDao.findLast(2), Duration.Inf)
       // move me to a method
       Await.result(
         exerciseDao.insert(
           Seq(
             Exercise(0, workout(0).id, squats.id, Option(3), Option(3), Option(285), Option(0), Option("Squats a lots"), person.id, Option(new DateTime()), Option(new DateTime())), 
             Exercise(0, workout(0).id, bench.id, Option(3), Option(3), Option(200), Option(0), Option("benched"), person.id, Option(new DateTime()), Option(new DateTime())),
             Exercise(0, workout(1).id, squats.id, Option(3), Option(3), Option(285), Option(0), Option("Squats a lots"), person.id, Option(new DateTime().plusDays(1)), Option(new DateTime().plusDays(1))), 
             Exercise(0, workout(1).id, bench.id, Option(3), Option(3), Option(200), Option(0), Option("benched"), person.id, Option(new DateTime().plusDays(1)), Option(new DateTime().plusDays(1)))
           )
         ), 
         Duration.Inf
       )

       val workouts = Await.result(workoutDao.findLast(2), Duration.Inf)
       workouts.length must equalTo(2)
       //workouts.items must have length(4)

     }
   }
}
