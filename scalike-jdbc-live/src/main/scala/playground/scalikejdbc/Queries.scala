package playground.scalikejdbc

import playground.scalikejdbc.ScalikeSpecific.City1
import scalikejdbc._
import playground.sql.{City, CityId}

object Queries {

  def selectCity(cName: String) =
    sql"SELECT id, name, population, area, link FROM city WHERE name = $cName".map(City1.apply)

}
