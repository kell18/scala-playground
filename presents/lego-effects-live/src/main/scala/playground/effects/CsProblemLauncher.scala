package playground.effects

import java.util.concurrent.Executors
import cats.effect._
import scala.concurrent.ExecutionContext

object CsProblemLauncher extends IOApp {

  val printThread = IO {
    println(Thread.currentThread().getName)
  }

  val ec1 = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
  val cs1 = IO.contextShift(ec1)

  def csProblem =
    printThread *> cs1.evalOn(ec1)(printThread) *> printThread *> IO(ExitCode.Success)

  override def run(args: List[String]) = csProblem
}
