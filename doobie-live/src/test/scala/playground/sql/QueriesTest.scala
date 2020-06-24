package playground.sql

import cats.effect.{ContextShift, IO}
import doobie.HC
import doobie.implicits._
import doobie.util.transactor.Transactor
import org.scalatest.{AsyncFeatureSpec, Matchers, WordSpec}
import scala.concurrent.ExecutionContext

class QueriesTest extends WordSpec with Matchers with doobie.scalatest.IOChecker {
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)


  val transactor = Transactor.after.set(Common.transactor, HC.rollback)

  "Queries test" should {
    "selectCity" in {
    }

    "insertCity should not throw" in {
    }
  }
}
