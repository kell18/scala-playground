package playground.sql

import cats.effect.{ContextShift, IO}
import Queries.insertCity
import doobie.HC
import doobie.implicits._
import doobie.util.transactor.Transactor
import org.scalatest.{AsyncFeatureSpec, Matchers, WordSpec}
import scala.concurrent.ExecutionContext

class QueriesTest extends WordSpec with Matchers with doobie.scalatest.IOChecker {
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  // .. Research this part, maybe it's possible to set logging like that
  // .. Think about Zio
  val transactor = Transactor.after.set(Common.transactor, HC.rollback)

  "Queries test" should {
    "selectCity" in {
      checkOutput(Queries.selectCity("NoCity"))
    }

    "selectMetroSystem" in {
      checkOutput(Queries.selectMetroSystem(CityId(0)))
    }

    "selectMetroSystemsWithCityNames" in {
      checkOutput(Queries.selectMetroSystemsWithCityNames)
    }

    "insertCity should not throw" in {
      insertCity("Test", 123, 456.0f, None)
        .withUniqueGeneratedKeys[CityId]("id")
        .map(_ => println("Insert is successful"))
        .transact(transactor)
        .unsafeRunSync()
    }
  }
}
