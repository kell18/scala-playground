package playground.sql

case class CityId(id: Int) extends AnyVal
case class City(id: CityId, name: String, population: Int, area: Float, link: Option[String])

case class MetroSystem(id: Int, cityId: CityId, name: String, dailyRidership: Int)

case class MetroSystemWithCity(cityId: CityId, metroSystemName: String, cityName: String, dailyRidership: Int)
