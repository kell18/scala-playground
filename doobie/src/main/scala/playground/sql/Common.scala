package playground.sql

import cats.effect.{IO, ContextShift}
import doobie.Transactor

object Common {
  def transactor(implicit cs: ContextShift[IO]) = Transactor.fromDriverManager[IO](
    driver = "com.mysql.cj.jdbc.Driver",
    url = "jdbc:mysql://root@127.0.0.1:3306/test?generateSimpleParameterMetadata=true&useSSL=false"
  )

  val KazanCity = City(CityId(1), "Kazan'", 1251969, 425.3f, Some("https://www.kzn.ru/?lang=en"))
  val BarcelonaCity = City(CityId(2), "Barcelona", 1620343, 101.4f, Some("https://www.barcelona.com"))

  val KazanMetro = MetroSystemWithCity(CityId(1), "Казанское метро", "Kazan'", 79500)
  val BarcelonaMetro = MetroSystemWithCity(CityId(4), "Metro de Barcelona", "Barcelona", 1116438)
}
