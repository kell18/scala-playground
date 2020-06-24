package playground.scalikejdbc

import scalikejdbc._

object Starter extends App {

  Class.forName("com.mysql.cj.jdbc.Driver")
  ConnectionPool.singleton(
    "jdbc:mysql://root@127.0.0.1:3306/test?generateSimpleParameterMetadata=true&useSSL=false",
      "root",
      ""
  )

  val r = DB.localTx { implicit session =>
    Queries.selectCityTyped("Kazan'").list.apply()
  }

  println(r)

}
