package playground.effects

import cats.implicits._
import cats.effect.{ContextShift, IO}
import scala.concurrent.ExecutionContext
import cats.effect.implicits._

trait Fourth_Problems {

  def f(a: A): Either[Error, B]
  def g(b: B): IO[C]

  /******* Complex types ********/
  val a: A
  val resultsSingle: Either[Error, IO[C]] = f(a).map(g)


}
