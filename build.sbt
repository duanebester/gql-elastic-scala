name := "gql-elastic"
version := "0.1"
scalaVersion := "2.13.0"

resolvers += "mvnrepository".at("http://mvnrepository.com/artifact/")
resolvers += Resolver.sonatypeRepo("public")
resolvers += Resolver.bintrayRepo("hseeberger", "maven")

scalacOptions ++= Seq("-deprecation", "-feature")

val elastic4sVersion = "7.1.0"
libraryDependencies ++= Seq(
  "org.sangria-graphql" %% "sangria" % "2.0.0-M1",
  "org.sangria-graphql" %% "sangria-spray-json" % "1.0.2",
  "com.typesafe.akka" %% "akka-http" % "10.1.10",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.10",
  "com.typesafe.akka" %% "akka-stream" % "2.5.26",
  "org.slf4j" % "slf4j-nop" % "1.6.6",
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-client-esjava" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-json-spray" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-testkit" % elastic4sVersion % Test
)

Revolver.settings
