package playground.sql

import cats.implicits._
import cats.Id
import org.scalatest.{Matchers, WordSpec}

class ProgramTest extends WordSpec with Matchers {
  import ProgramTest._

  "ProgramTest" should {
    val service = fakeCityService(Common.KazanCity :: Nil, Common.KazanMetro :: Nil)
    val program = new Program[Option](service)
    "computeMetroPopularity" in {
      val res = program.computeMetroPopularity("Kazan'")
      res shouldBe defined
      res.map(_ should be >= 0.0f)
      res.map(_ should be <= 1.0f)
    }
  }
}

object ProgramTest {
  def fakeCityService(cities: List[City], metroSystems: List[MetroSystemWithCity]): CityService[Option] =
    new CityService[Option] {
      override def cityByName(name: String) = cities.find(_.name == name)

      override def cityMetroById(id: CityId) = metroSystems.find(_.cityId == id)
    }
}
