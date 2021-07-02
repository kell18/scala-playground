package playground.effects

import java.util.concurrent.Executors
import cats.implicits._
import cats.effect._
import cats.effect.implicits._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

object Fs2Test extends IOApp { // used for Fs2-bases solution for parallel Atlas search function
  import fs2.{Stream => FStream}
  val s1 = FStream("A","B","C","D","E").evalTap(v => IO(println(s"$v in S1")))

  val s2 = FStream(1, 2, 3, 4, 5).evalTap(v => IO(println(s"$v in S22")))
  val s3 = FStream(".", "..", "...").evalTap(v => IO(println(s"$v in S333")))

  val sl: List[FStream[IO, List[Any]]] = List(
    s1.map(List(_)),
    s2.map(List(_)),
    s3.map(List(_))
  )

  override def run(args: List[String]) = {
    val r = sl.reduceLeft[FStream[IO, List[Any]]] {
      case (acc, next) => acc.padZip(next).map { case (b1, b2) => b1.toList.flatten ++ b2.toList.flatten }
    }

    r.take(2).compile.toList.map { res =>
      println(s"RES = $res")
      ExitCode.Success
    }
  }
}

object Test extends App {
  val listA = List(1, 2, 3)
  val listB = List(4, 5, 6)

  val r1 = for {
    a <- listA
    if a > 1
    b <- listB
  } yield {
    a * b
  }

  val r2 = listA.flatMap { a =>
    listB.map { b => a * b }
  }

  println(r1)
  println(r2)
}

object FutureTest extends App {
  val fA = Future { 2 }
  val fB: Future[Int] = Future.failed(new Exception("test"))

  val r1 = for {
    a <- fA
    b <- fB
  } yield a * b

  val r2 = fA.flatMap { a =>
    fB.map { b => a * b }
  }

  println(r1)
  println(r2)
}