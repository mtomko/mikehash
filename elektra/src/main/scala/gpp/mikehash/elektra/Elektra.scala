/*
 * Copyright 2017 Genetic Perturbation Platform, The Broad Institute of Harvard and MIT.
 * http://www.broadinstitute.org
 */
package gpp.mikehash.elektra

import io.getquill._

import scala.concurrent.ExecutionContext.Implicits.global

object Elektra {

  case class Person(name: String, age: Int)

  def main(args: Array[String]): Unit = {

    val db = new CassandraAsyncContext[SnakeCase]("ctx")

    import db._

    case class WeatherStation(country: String,
                              city: String,
                              stationId: String,
                              entry: Int,
                              value: Int)


    val getAllByCountry = quote { (country: String) =>
      query[WeatherStation].filter(_.country == country)
    }

    val result = db.run(getAllByCountry(lift("UK")))

     result.onComplete(_ => db.close())
  }

}
