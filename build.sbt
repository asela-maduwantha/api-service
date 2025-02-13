ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.15"

lazy val root = (project in file("."))
  .settings(
    name := "api-service"
  )


libraryDependencies ++= Seq(
  "com.rabbitmq" % "amqp-client" % "5.5.0" ,
  "org.slf4j" % "slf4j-simple" % "1.7.30",
  "io.circe" %% "circe-core" % "0.14.5",
  "io.circe" %% "circe-generic" % "0.14.5",
  "io.circe" %% "circe-parser" % "0.14.5",
  "com.typesafe" % "config" % "1.4.2",
  "org.json4s" %% "json4s-native" % "4.0.3",
  "org.json4s" %% "json4s-jackson" % "4.0.3",
  "org.apache.pekko" %% "pekko-actor-typed" % "1.1.2",
  "org.apache.pekko" %% "pekko-stream" % "1.1.2",
  "org.apache.pekko" %% "pekko-http" % "1.1.0",
  "org.slf4j" % "slf4j-simple" % "2.0.16",
"org.apache.pekko" %% "pekko-http-spray-json" % "1.1.0"
)