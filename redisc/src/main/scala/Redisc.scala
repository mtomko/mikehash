/*
 * Copyright 2017 Genetic Perturbation Platform, The Broad Institute of Harvard and MIT.
 * http://www.broadinstitute.org
 */
import java.nio.ByteBuffer

import boopickle.Default._
import com.redis.RedisClient
import gpp.mikehash.share.IntegerBox

// TODO: not working yet
object Redisc {

  def main(args: Array[String]): Unit = {

    val client = new RedisClient("127.0.0.1", 6379)

    val targetSeq = "AAAAAAACCTGTCGATGATA"

    // put a simplified threat matrix
    client.hset(targetSeq, "Tier 1 Bin 1", Pickle.intoBytes(IntegerBox(0)).array())
    client.hset(targetSeq,"Tier 1 Bin 2", 0)
    client.hset(targetSeq, "Tier 1 Bin 3", 1)
    client.hset(targetSeq, "Tier 2 Bin 1", 7)
    client.hset(targetSeq, "Tier 2 Bin 2", 13)
    client.hset(targetSeq, "Tier 2 Bin 3", 388)

    val atm0 =
      Unpickle[IntegerBox]
        .fromBytes(ByteBuffer.wrap(client.hget(targetSeq, "Tier 1 Bin 1").asInstanceOf[Array[Byte]]))
        .i
    //val atm0 = rec.getInt("Tier 1 Bin 1")
    val atm1 = client.hget(targetSeq, "Tier 1 Bin 2").map { _.toInt }.get
    val atm2 = client.hget(targetSeq, "Tier 1 Bin 3").map { _.toInt }.get
    val atm3 = client.hget(targetSeq, "Tier 2 Bin 1").map { _.toInt }.get
    val atm4 = client.hget(targetSeq, "Tier 2 Bin 2").map { _.toInt }.get
    val atm5 = client.hget(targetSeq, "Tier 2 Bin 3").map { _.toInt }.get


    require(atm0 == 0)
    require(atm1 == 0)
    require(atm2 == 1)
    require(atm3 == 7)
    require(atm4 == 13)
    require(atm5 == 388)
    println("yay")

  }

}
