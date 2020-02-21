package playground.sql

case class CityId(id: Int) extends AnyVal
case class City(id: CityId, name: String, population: Int, area: Float, link: Option[String])

case class MetroSystemWithCity(cityId: CityId, metroSystemName: String, cityName: String, dailyRidership: Int)

// Try sealed trait
object TrackType extends Enumeration {
  type TrackType = Value
  val Rail = Value(1)
  val Monorail = Value(2)
  val Rubber = Value(3)

  def byId(id: Int): Option[TrackType] = TrackType.values.find(_.id == id)
  def byIdOrThrow(id: Int): TrackType = byId(id).getOrElse(throw new IllegalArgumentException(s"Unknown track type: $id"))
}