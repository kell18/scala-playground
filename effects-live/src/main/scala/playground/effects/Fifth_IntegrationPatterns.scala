package playground.effects

import cats.implicits._
import cats.effect.{ContextShift, IO}
import scala.concurrent.{ExecutionContext, Future}
import cats.effect.implicits._
import monix.eval.Task
import monix.execution.{CancelableFuture, Scheduler}

trait Fifth_IntegrationPatterns {

  implicit val contextShift: ContextShift[IO]

  /******* Future to IO/Task  ********/


  /******* IO/Task to Future ********/

}
