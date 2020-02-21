package playground.sql

import Queries._
import TrackType.TrackType
import doobie._
import doobie.implicits._
import cats._
import cats.effect._
import cats.implicits._
import cats.effect.{Blocker, IO, IOApp}

object Starter extends IOApp {
  val dbCityService = new DBCityService(Common.transactor)
  val program = new Program(dbCityService)

  def run(args: List[String]) =
    program
      .computeMetroPopularity("Paris")
      .map(p => println(s"Paris metro popularity is ${p * 100}%"))
      .map(_ => ExitCode.Success)

  def run1(args: List[String]) =
    Queries
      .insertCity("Kazan'", 1251969, 425.3f, Some("https://www.kzn.ru/?lang=en"))
      .run
      .transact(Common.transactor)
      .map(_ => ExitCode.Success)

}
