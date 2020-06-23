package playground.sql

import doobie._
import doobie.implicits._
import cats.effect.implicits._
import cats.effect.{Blocker, IO}
import scala.concurrent.Future

object Queries {

  def selectCity(cName: String) =
    sql"SELECT id, name, population, area, link FROM city WHERE name = $cName ".query[City]

  def selectMetroSystem(cityId: CityId) =
    sql"SELECT id, city_id, name, daily_ridership FROM metro_system WHERE city_id = $cityId".query[MetroSystemWithCity]

  def selectMetroSystemsWithCityNames =
    sql"""
         |SELECT c.id, ms.name, c.name, ms.daily_ridership
         |FROM metro_system as ms
         |LEFT JOIN city as c ON ms.city_id = c.id
         |""".stripMargin
      .query[MetroSystemWithCity]

  def insertCity(name: String, population: Int, area: Float, link: Option[String]) =
    sql"INSERT INTO city(name, population, area, link) VALUES ($name, $population, $area, $link)".update

}
