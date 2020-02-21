package playground.sql

import cats.implicits._
import cats.Monad
import cats.effect.IO
import doobie._
import doobie.implicits._
import doobie.free.connection.ConnectionIO
import doobie.util.transactor.Transactor
import CityService._
import scala.concurrent.Future
import scala.language.higherKinds

class Program[F[_]: Monad](cityService: CityService[F]) {

  def computeMetroPopularity(cityName: String): F[Float] = for {
    city <- cityService.cityByName(cityName)
    metro <- cityService.cityMetroById(city.id)
  } yield metro.dailyRidership / city.population.toFloat

}

trait CityService[F[_]] {
  def cityByName(name: String): F[City]
  def cityMetroById(id: CityId): F[MetroSystemWithCity]
}

object CityService {
  sealed trait Error extends Throwable with Product with Serializable
  case class CityNotFound(name: String) extends Error
}

class DBCityService(transactor: Transactor[IO]) extends CityService[IO] {
  override def cityByName(name: String): IO[City] =
    Queries
      .selectCity(name)
      .to[List]
      .transact(transactor)
      .flatMap(_.headOption match {
        case None => CityNotFound(name).raiseError[IO, City]
        case Some(c) => c.pure[IO]
      })

  // With plain example on how you can post-process things
  override def cityMetroById(cityId: CityId): IO[MetroSystemWithCity] =
    Queries.selectMetroSystem(cityId)
      .unique
      .transact(transactor)
}
