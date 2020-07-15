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

  def f(implicit ec: ExecutionContext): Future[String]

  def ioWithEC(implicit ec: ExecutionContext) = IO.fromFuture(IO(f))


  def taskWithEC(implicit ec: ExecutionContext) = Task.deferFuture(f)
  def taskWithoutEC = Task.deferFutureAction { implicit scheduler => f }

  // It will be injected "at the end of the world" when we will run our side-effects in Tasks, eg:
  implicit val scheduler = monix.execution.Scheduler.global
  taskWithoutEC.runToFuture

  // --- Note that it seems like there is no way to achieve the same in IO


  /******* IO/Task to Future ********/
  def io: IO[String]
  val futureFromIo = io.unsafeToFuture()

  def task: Task[String]
  val taskFromFuture: CancelableFuture[String] = task.runToFuture // scheduler here
  taskFromFuture.cancel() // <- extends future with cancellable

  // Note that Scheduler from Monix is better version of Scala execution context, it providing reachier API and
  //  BatchedExecution, the Monix default, specifies a mixed execution mode under which tasks are executed synchronously in batches up to a maximum size, after which an asynchronous boundary is forced. This execution mode is recommended because we don’t want to block threads / run-loops indefinitely, especially on top of Javascript where a long loop can mean that the UI gets frozen and where we need to be cooperative.
  //  AlwaysAsyncExecution specifies that units of work within a loop should always execute asynchronously on each step, being basically the mode of operation for Scala’s Future.
  //  SynchronousExecution specifies that synchronous execution should always be preferred, for as long as possible, being basically the mode of operation for the Scalaz Task


  /******* Blocker  ********/
  // There is a blocker

}
