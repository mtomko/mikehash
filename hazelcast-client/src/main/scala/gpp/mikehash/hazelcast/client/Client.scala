/*
 * Copyright 2017 Genetic Perturbation Platform, The Broad Institute of Harvard and MIT.
 * http://www.broadinstitute.org
 */
package gpp.mikehash.hazelcast.client

import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.ClientConfig
import gpp.mikehash.share.IntegerBox
import resource._

import scala.language.{implicitConversions, reflectiveCalls}

object Client {

  type ReflectiveShutdownable = { def shutdown(): Unit }
  implicit def reflectiveDisposableResource[A <: ReflectiveShutdownable] = new Resource[A] {
    override def close(r: A) = r.shutdown()
    override def toString = "Resource[{ def shutdown() : Unit }]"
  }

  def main(args: Array[String]): Unit = {
    val clientConfig = new ClientConfig
    for (client <- managed(HazelcastClient.newHazelcastClient(clientConfig))) {
      val map = client.getMap[Int, IntegerBox]("customers")

      val r = 1 to 1000
      val t0 = System.currentTimeMillis()
      for (_ <- r) {
        map.get(3)
      }
      val dt = System.currentTimeMillis() - t0

      System.out.println(s"1000 gets took $dt millis")

      System.out.println("Map Size: " + map.size)
    }

  }
}
