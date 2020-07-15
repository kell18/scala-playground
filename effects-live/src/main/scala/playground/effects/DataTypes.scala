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

  // 1 - Set of type classes: Async/Effect/Bkacket
  // 2 - default implementation - IO

  /// Predictable concurrency



}
