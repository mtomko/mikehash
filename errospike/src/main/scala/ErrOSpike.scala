/*
 * Copyright 2017 Genetic Perturbation Platform, The Broad Institute of Harvard and MIT.
 * http://www.broadinstitute.org
 */
import com.aerospike.client.Bin
import io.tabmo.aerospike.client.ReactiveAerospikeClient
import io.tabmo.aerospike.converter.key._
import io.tabmo.aerospike.data.AerospikeKey
import monix.eval.Task

import scala.concurrent.Await
import scala.concurrent.duration._

object ErrOSpike {

  def main(args: Array[String]): Unit = {
    val asyncClient = ReactiveAerospikeClient.connect("127.0.0.1", 3000)

    // namespaces may have to be created in advance, which is not totally awesome
    val namespace = "test"
    val setname = "vintage"
    val targetSeq = "AAAAAAACCTGTCGATGATA"

    // define a key for a threat matrix
    val key = AerospikeKey(namespace, setname, targetSeq)

    // put a simplified threat matrix
    val tm0 = new Bin("Tier 1 Bin 1", 0)
    val tm1 = new Bin("Tier 1 Bin 2", 0)
    val tm2 = new Bin("Tier 1 Bin 3", 1)
    val tm3 = new Bin("Tier 2 Bin 1", 7)
    val tm4 = new Bin("Tier 2 Bin 2", 13)
    val tm5 = new Bin("Tier 2 Bin 3", 388)

    val putTask = Task.deferFuture {
      asyncClient.put(key, Seq(tm0, tm1, tm2, tm3, tm4, tm5))
    }

    val getTask = Task.deferFuture(asyncClient.get(key))

    import monix.execution.Scheduler.Implicits.global
    val rw = for {
      _ <- putTask
      t <- getTask
    } yield {
      val atm0 = t.getLong("Tier 1 Bin 1").toInt
      val atm1 = t.getLong("Tier 1 Bin 2").toInt
      val atm2 = t.getLong("Tier 1 Bin 3").toInt
      val atm3 = t.getLong("Tier 2 Bin 1").toInt
      val atm4 = t.getLong("Tier 2 Bin 2").toInt
      val atm5 = t.getLong("Tier 2 Bin 3").toInt
      (atm0, atm1, atm2, atm3, atm4, atm5)
    }

    val (atm0, atm1, atm2, atm3, atm4, atm5) = Await.result(rw.runAsync, 30.seconds)
    require(atm0 == 0)
    require(atm1 == 0)
    require(atm2 == 1)
    require(atm3 == 7)
    require(atm4 == 13)
    require(atm5 == 388)
    println("yay")

    asyncClient.close()
  }

}
