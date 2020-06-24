package playground.scalikejdbc

import playground.scalikejdbc.ScalikeSpecific.City1
import scalikejdbc._
import playground.sql.{City, CityId}

object Queries {

  def selectCity(cName: String) =
    sql"SELECT id, name, population, area, link FROM city WHERE name = $cName".map(City1.apply)

  def selectCityTyped(cName: String) = {
    val rn = City1.syntax("rn")
    sql"SELECT ${rn.result.*} FROM ${City1.as(rn)} WHERE name = $cName".map(rs => City1.apply(rs, rn.resultName))
  }

  def insertCity(name: String, population: Int, area: Float, link: Option[String]) =
    sql"INSERT INTO city(name, population, area, link) VALUES ($name, $population, $area, $link)".update

}
