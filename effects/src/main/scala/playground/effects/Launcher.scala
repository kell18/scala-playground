package playground.effects

import java.util.concurrent.Executors
import cats.effect._
import cats.effect.implicits._
import scala.concurrent.ExecutionContext

object Launcher extends IOApp {

  val printThread = IO { println(Thread.currentThread().getName) }

  val ec1 = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
  val cs1 = IO.contextShift(ec1)

  def csProblem =
    printThread *> cs1.evalOn(ec1)(printThread) *> printThread *> IO(ExitCode.Success)

  def acquire(r: String): IO[String] = IO {
    println(s"Acquire $r")
    r
  }

  def release(r: String): IO[Unit] = IO {
    println(s"Release $r")
  }

  override def run(args: List[String]) = {
    val res = Resource
      .make(acquire("Res1"))(release)
      .map(_ + "_map")
      .flatMap(r => Resource.make(acquire(s"$r--Res2(flatMap)"))(release))

    val io = res
      .use { r =>
        IO {
          println(s"Using $r")
          r
        }
      }

    io
      .map(_ + "_mapIO")
      .flatMap(r => IO {
        println(s"flatMapIO: $r")
        ExitCode.Success
      })
  }
}
