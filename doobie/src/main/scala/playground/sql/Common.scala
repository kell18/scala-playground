package playground.sql

import cats.effect.{IO, ContextShift}
import doobie.Transactor

object Common {
  def transactor(implicit cs: ContextShift[IO]) = Transactor.fromDriverManager[IO](
    driver = "com.mysql.cj.jdbc.Driver",
    url = "jdbc:mysql://root@127.0.0.1:3306/test?generateSimpleParameterMetadata=true"
  )

  val KazanCity = City(CityId(1), "Kazan'", 1251969, 425.3f, Some("https://www.kzn.ru/?lang=en"))
  val KazanMetro = MetroSystemWithCity(CityId(1), "Казанское метро", "Kazan'", 79500)
}
