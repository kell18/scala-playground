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

  // More nested types
  val inputs: List[A]
  val resultMany: List[Either[Error, IO[C]]] = inputs.map(a => f(a).map(g))


  // Solution #1 - using Traverse type-class
  val resultsSingle1: IO[Either[Error, C]] = f(a).traverse(g)

  // Solution #2 - simplifying functions in favor of more powerful type
  // --- Recal that they are all monads and they all have the same failure type by means of MonadError
  def fg(a: A): IO[C] = IO.fromEither(f(a)).flatMap(g)
  val resultMany1: IO[List[C]] = inputs.traverse(fg)

  // --- Note that IO is based on throwable


  /******* Performance ********/
   /*
   * 1. More copies of short-living objects to collect
   * 2. More wrappers
   * 3. More closures
   */

  /*
  * Yes, but it’s not that bad at all:
  *   - Modern GCs are very fast and optimised for short-living objects collection
  *   - Most importantly - in majority of cases bottlenecks will be in network or heavy algorithms
  */


  /******* Unsafe IO context shift API (not true for Monix and Zio)  ********/

  implicit val contextShift: ContextShift[IO]

  def complexCPU: IO[Unit]
  def regularIO: IO[String]

  val WorkStealingEC: ExecutionContext
  implicit val GlobalEC: ExecutionContext

   val shiftedResult = for {
     _ <- contextShift.evalOn(WorkStealingEC)(complexCPU)
     r <- regularIO // will be executed on WorkStealingEC because evalOn does not shift it back!
   } yield r

  // Fixed for Monix Task
  // Monix Task is older and has richer interface, IO is kind of reference implementation

}
