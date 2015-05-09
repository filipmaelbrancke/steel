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

@RunWith(classOf[JUnitRunner])
class ModelSpec extends Specification {

  import models._

  // -- Date helpers

   def dateIs(date: java.util.Date, str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str

   "Exercise model" should {
     def exercisesDao    = new ExercisesDAO
     def exerciseTypeDao = new ExerciseTypesDAO
     def peopleDao       = new PeopleDAO

     "be retrieved by name" in new WithApplication {
       Await.result(exerciseTypeDao.insert(ExerciseType("lift", "resistance training with weights")), Duration.Inf)
       val exerciseType = Await.result(exerciseTypeDao.findByName("lift"), Duration.Inf).get

       exerciseType.name must equalTo("lift")
       exerciseType.description must equalTo("resistance training with weights")
     }

   }
}
