package playground.scalikejdbc

import java.sql.ResultSet
import playground.sql.{City, CityId}
import scalikejdbc._

object ScalikeSpecific {

  case class City1(id: CityId, name: String, population: Int, area: Float, link: Option[String])

  object City1 extends SQLSyntaxSupport[City1] {
    override def tableName = "city"

    implicit val memberIdTypeBinder: TypeBinder[CityId] = new TypeBinder[CityId] {
      def apply(rs: ResultSet, label: String): CityId = CityId(rs.getInt(label))
      def apply(rs: ResultSet, index: Int): CityId = CityId(rs.getInt(index))
    }

    def apply(rs: WrappedResultSet, rn: ResultName[City1]) = autoConstruct(rs, rn)

    def apply(rs: WrappedResultSet) =
      new City1(CityId(rs.int("id")), rs.string("name"), rs.int("population"), rs.float("area"), rs.stringOpt("link"))
  }

}
