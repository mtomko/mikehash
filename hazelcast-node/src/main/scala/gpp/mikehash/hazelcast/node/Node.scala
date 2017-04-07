/*
 * Copyright 2017 Genetic Perturbation Platform, The Broad Institute of Harvard and MIT.
 * http://www.broadinstitute.org
 */
package gpp.mikehash.hazelcast.node

import com.hazelcast.Scala._
import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import gpp.mikehash.share.IntegerBox

object Node {

  //case class Foo(name: String)

  def main(args: Array[String]): Unit = {
    val config = new Config
    config.setInstanceName("mikehash")
    config.setProperty("hazelcast.phone.home.enabled", "false")
    serialization.Defaults.register(config.getSerializationConfig)
    config.getNetworkConfig.getJoin.getMulticastConfig.setEnabled(false)
    config.getNetworkConfig.getJoin.getTcpIpConfig.setEnabled(true)
    config.getNetworkConfig.getJoin.getTcpIpConfig.addMember("127.0.0.1")
    val hazelcastInstance = Hazelcast.newHazelcastInstance(config)
    val customers = hazelcastInstance.getMap[Int, IntegerBox]("customers")

    val n = args(0).toInt

    for (i <- n to 10000 by 3) {
      customers.put(i,  IntegerBox(i))
    }
    println("Customer with key 1: " + customers.get(1))
    println("Map Size:" + customers.size())

    //val foos = hazelcastInstance.getMap[Int, Foo]("foos")
    //foos.put(5, Foo("bob"))
  }

}
