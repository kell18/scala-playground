package playground.effects

import cats.implicits._
import cats.effect._
import cats.effect.implicits._
import cats.data._
import cats.Eval
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

trait DataTypes {

  implicit val contextShift: ContextShift[IO]

  /******* IO/Task/Zio IE proper Futures (performant, lazy and cancellable) ********/


  /// Predictable concurrency

  val request1: IO[String]
  def request2: IO[String]
  // Concurrent or sequential?
  val result = for {
    _ <- request1
    _ <- request2
  } yield "Done."

  val concurResultMany = List(request1, request1, request2, request2).parSequence
  val firstAvailable: IO[Either[String, String]] = IO.race(request1, request2)


  /// Re-usable patterns like CircuitBreaker

  implicit val timer: Timer[IO] // <- needed for pure timeouts, handy for testing purposes

  def retryWithBackoff[A](ioa: IO[A], initialDelay: FiniteDuration, maxRetries: Int)
                         (implicit timer: Timer[IO]): IO[A] = {

    ioa.handleErrorWith { error =>
      if (maxRetries > 0)
        IO.sleep(initialDelay) *> retryWithBackoff(ioa, initialDelay * 2, maxRetries - 1)
      else
        IO.raiseError(error)
    }
  }

  val retriedRequest = retryWithBackoff(request1, 10.seconds, 5)


  /// A bit more predictable execution contexts

  def regularIO: IO[Unit]
  def complexCPU: IO[String]

  val WorkStealingEC: ExecutionContext
  implicit val GlobalEC: ExecutionContext

  val shiftedResult = for {
    _ <- regularIO
    r <- contextShift.evalOn(WorkStealingEC)(complexCPU)
  } yield r
  // !! this work properly for Monix Task and Zio but not for IO
  // .. show example

  val r: IO[Fiber[IO, String]] = shiftedResult.start(IO.contextShift(GlobalEC))
  // External IO is side effect of running that which we than hand to IOApp


  /******* NonEmptyList ********/

  val first: A
  val nel: NonEmptyList[A] = NonEmptyList(first, Nil)
  // Also a monad, so nel.map/flatMap/...


  /******* Ior or Either with `Both` option ********/

  val second: B
  val ior1 = Ior.left(first)
  val ior2 = Ior.right(second)
  val ior3 = Ior.Both(first, second)
  // Also a monad, so ior3.map/flatMap/...

  /******* Eval - way to make sure your will not get StackOverflow in recursion  ********/
  object MutualRecursion {
    def even(n: Int): Eval[Boolean] =
      Eval.always(n == 0).flatMap {
        case true => Eval.now(true)
        case false => odd(n - 1)
      }

    def odd(n: Int): Eval[Boolean] =
      Eval.always(n == 0).flatMap {
        case true => Eval.now(false)
        case false => even(n - 1)
      }
  }
  // Also a monad

  // And more...

}
