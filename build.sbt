name := "mikehash"

version := "1.0"

scalaVersion in ThisBuild := "2.11.9"

// Dependencies
lazy val aerospike = "com.aerospike" % "aerospike-client" % "latest.integration"
lazy val boopickle = "io.suzaku" %% "boopickle" % "1.2.6"
lazy val rxaerospike = "io.tabmo" %% "reactive-aerospike" % "1.0.7"
lazy val hazelcastDep = "com.hazelcast" % "hazelcast" % "3.8"
lazy val hazelcastClientDep = "com.hazelcast" % "hazelcast-client" % "3.8"
lazy val hazelcastScalaDep = "com.hazelcast" %% "hazelcast-scala" % "3.7.2" withSources ()
lazy val monix = "io.monix" %% "monix" % "2.2.4"
lazy val quill = "io.getquill" %% "quill" % "1.1.0"
lazy val quillCassandra = "io.getquill" %% "quill-cassandra" % "1.1.0"
lazy val scalaArmDep = "com.jsuereth" %% "scala-arm" % "2.0"

// Projects
lazy val mikehash =
  project
    .in(file("."))
    .aggregate(elektra, errospike, hazelcastClient, hazelcastNode, share)

lazy val elektra =
  project
    .in(file("elektra"))
    .dependsOn(share)
    .settings(libraryDependencies ++= Seq(quill, quillCassandra))

lazy val errospike =
  project
    .in(file("errospike"))
    .dependsOn(share)
    .settings(
      resolvers += "Tabmo Bintray" at "https://dl.bintray.com/tabmo/maven",
      libraryDependencies ++= Seq(aerospike, boopickle, monix, rxaerospike)
    )

lazy val hazelcastClient =
  project
    .in(file("hazelcast-client"))
    .dependsOn(share)
    .settings(
      resolvers += Resolver.jcenterRepo,
      libraryDependencies ++= Seq(hazelcastClientDep, hazelcastScalaDep, scalaArmDep)
    )

lazy val hazelcastNode =
  project
    .in(file("hazelcast-node"))
    .dependsOn(share)
    .settings(
      resolvers += Resolver.jcenterRepo,
      libraryDependencies ++= Seq(hazelcastDep, hazelcastScalaDep, scalaArmDep)
    )

lazy val share = project.in(file("share"))
