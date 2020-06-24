package playground.scalikejdbc

import org.scalatest.{fixture, _}
import org.scalatest.fixture.FlatSpec
import playground.scalikejdbc.ScalikeSpecific.City1
import playground.sql.CityId
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc._
import scala.concurrent.ExecutionContext

class QueriesTest extends fixture.FlatSpec with Matchers with AutoRollback {

  Class.forName("com.mysql.cj.jdbc.Driver")
  ConnectionPool.singleton(
    "jdbc:mysql://root@127.0.0.1:3306/test?generateSimpleParameterMetadata=true&useSSL=false",
    "root",
    ""
  )

  behavior of "Queries"

  it should "Insert" in { implicit session =>
    Queries.insertCity("Test", 0, 0.0f, None)
  }
}
