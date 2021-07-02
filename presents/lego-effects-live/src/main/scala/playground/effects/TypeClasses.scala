package playground.effects

import cats.implicits._
import cats.effect.{IO, Resource}
import cats.effect.implicits._
import cats.Monad
import playground.sql.{City, CityId, MetroSystemWithCity}
import scala.io.Source
import scala.language.higherKinds

trait TypeClasses {

  /******* Canonical type-classes example ********/

  // OOP
  case class Cat(name: String) {
    def encode: String = name
  }


}
trait TypeClassesExamples {

  /******* Traverse/Sequence ********/

  def f(a: A): Either[Error, B]

  val inputs: List[A]




}
