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
import models.Person
import models.Exercise
import models.ExerciseType
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

     "be retrieved by name" in new WithApplication {
       val exerciseType = Await.result(exerciseTypeDao.findByName("barbell squats"), Duration.Inf).get
       exerciseType.name must equalTo("barbell squats")

       Await.result(personDao.insert(Person(0, "fart@fart.com", "password123", Option(new DateTime()), Option(new DateTime()))), Duration.Inf)

       val person = Await.result(personDao.findByEmail("fart@fart.com"), Duration.Inf).get

       person.email must equalTo("fart@fart.com")

       //                                       id  fk -> type       reps       weight       time
       //                                       notes               fk -> person createdAt updatedAt
       Await.result(exerciseDao.insert(Exercise(0, exerciseType.id, Option(3), Option(285), Option(0), Option("Squats a lots"), person.id, Option(new DateTime()), Option(new DateTime())).pp), Duration.Inf)

       val exercise = Await.result(exerciseDao.findLast(1), Duration.Inf).head
       
       exercise.kind must equalTo(exerciseType.id)
     }

     "be retrieved with ExerciseTypes" in new WithApplication {
       val exercisesWithType = Await.result(exerciseDao.findWithType(1), Duration.Inf)

       exercisesWithType.items must have length(1)

       // can't figure out how to iterate over this properly
       exercisesWithType.items.map { ex =>
         ex.pp /*.map {
           case (exercise, exerciseType) => {
             exerciseType.name must equalTo("barbell squats")
           }
         }*/
       }
     }
   }
}
