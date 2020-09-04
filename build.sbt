import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._

name := """silhouette"""
organization := "silhouette"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

version := "4.0.0"

scalaVersion := "2.11.8"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += Resolver.jcenterRepo
// Resolver is needed only for SNAPSHOT versions
resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "reactivemongo-play-json" % "0.20.11-play25",
  "org.reactivemongo" %% "reactivemongo-bson-api" % "0.18.0",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.12.1",
  "com.mohiva" %% "play-silhouette" % "4.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0",
  "com.mohiva" %% "play-silhouette-persistence-reactivemongo" % "4.0.1",
  "org.webjars" %% "webjars-play" % "2.5.0-2",
  "net.codingwell" %% "scala-guice" % "4.0.1",
  "com.iheart" %% "ficus" % "1.2.6",
  "com.typesafe.play" %% "play-mailer" % "5.0.0",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.5.0-akka-2.4.x",
  "com.adrianhurt" %% "play-bootstrap" % "1.0-P25-B3",
  "com.mohiva" %% "play-silhouette-testkit" % "4.0.0" % "test",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.9.0",
  specs2 % Test,
  cache,
  filters
)

fork in run := false

libraryDependencies += "javax.xml.bind" % "jaxb-api" % "2.2.12"
routesGenerator := InjectedRoutesGenerator