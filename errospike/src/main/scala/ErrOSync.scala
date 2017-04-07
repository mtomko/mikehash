/*
 * Copyright 2017 Genetic Perturbation Platform, The Broad Institute of Harvard and MIT.
 * http://www.broadinstitute.org
 */
import java.nio.ByteBuffer

import boopickle.Default._
import com.aerospike.client.policy.WritePolicy
import com.aerospike.client.{AerospikeClient, Bin, Key}
import gpp.mikehash.share.IntegerBox

object ErrOSync {

  def main(args: Array[String]): Unit = {
    val client = new AerospikeClient("127.0.0.1", 3000)

    // Initialize policy.
    val policy = new WritePolicy
    policy.timeout = 50 // 50 millisecond timeout

    // namespaces may have to be created in advance, which is not totally awesome
    val namespace = "test"
    val setname = "vintage"
    val targetSeq = "AAAAAAACCTGTCGATGATA"

    // define a key for a threat matrix
    val key = new Key(namespace, setname, targetSeq)

    // put a simplified threat matrix
    val tm0 = new Bin("Tier 1 Bin 1", Pickle.intoBytes(IntegerBox(0)).array())
    val tm1 = new Bin("Tier 1 Bin 2", 0)
    val tm2 = new Bin("Tier 1 Bin 3", 1)
    val tm3 = new Bin("Tier 2 Bin 1", 7)
    val tm4 = new Bin("Tier 2 Bin 2", 13)
    val tm5 = new Bin("Tier 2 Bin 3", 388)

    client.put(policy, key, tm0, tm1, tm2, tm3, tm4, tm5)

    val rec = client.get(policy, key)
    val atm0 =
      Unpickle[IntegerBox]
        .fromBytes(ByteBuffer.wrap(rec.getValue("Tier 1 Bin 1").asInstanceOf[Array[Byte]]))
        .i
    //val atm0 = rec.getInt("Tier 1 Bin 1")
    val atm1 = rec.getInt("Tier 1 Bin 2")
    val atm2 = rec.getInt("Tier 1 Bin 3")
    val atm3 = rec.getInt("Tier 2 Bin 1")
    val atm4 = rec.getInt("Tier 2 Bin 2")
    val atm5 = rec.getInt("Tier 2 Bin 3")

    require(atm0 == 0)
    require(atm1 == 0)
    require(atm2 == 1)
    require(atm3 == 7)
    require(atm4 == 13)
    require(atm5 == 388)
    println("yay")
  }

}
