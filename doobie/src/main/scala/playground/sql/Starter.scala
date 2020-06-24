package playground.sql

import Queries._
import doobie._
import doobie.implicits._
import cats._
import cats.effect._
import cats.implicits._
import cats.effect.{Blocker, IO, IOApp}

object Starter extends IOApp {
  val dbCityService = new DBCityService(Common.transactor)
  val program = new Program(dbCityService)

  Common.transactor.rawTrans

  override def run(args: List[String]) = selectCity

  def selectCity =
    Queries
      .selectMetroSystem(CityId(1))
      .to[List]
      .map(println)
      .transact(Common.transactor)
      .map(_ => ExitCode.Success)

  def insertKazan() =
    Queries
      .insertCity("Kazan'", 1251969, 425.3f, Some("https://visit-tatarstan.com/en/"))
      .run

  def insertBarcelona() =
    Queries
      .insertCity("Barcelona", 1620343, 101.4f, Some("https://www.barcelona.com"))
      .run

  def insertTx() = {
    val twoInserts = for {
      _ <- insertKazan()
      _ <- insertBarcelona()
    } yield ()

    twoInserts.transact(Common.transactor)
  }



  def doStuff(args: List[String]) =
    program
      .computeMetroPopularity("Paris")
      .map(p => println(s"Paris metro popularity is ${p * 100}%"))
      .map(_ => ExitCode.Success)

}
