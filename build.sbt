import com.typesafe.sbt.SbtScalariform._
import play.sbt.routes.RoutesKeys
import scalariform.formatter.preferences._
import play.twirl.sbt.Import.TwirlKeys

name := """silhouette"""
organization := "silhouette"

version := "1.0-SNAPSHOT"

// Salat Settings
lazy val root = (project in file(".")).enablePlugins(PlayScala).settings(
  routesImport += "se.radley.plugin.salat.Binders._",
  TwirlKeys.templateImports += "org.bson.types.ObjectId"
)

version := "4.0.0"

scalaVersion := "2.11.8"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += Resolver.jcenterRepo
// Resolver is needed only for SNAPSHOT versions
resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
//  "org.reactivemongo" %% "reactivemongo-play-json" % "0.20.11-play25",
  "org.reactivemongo" %% "reactivemongo-bson-api" % "0.18.0",
//  "org.reactivemongo" %% "reactivemongo" % "0.11.14",
  "org.mongodb.scala" %% "mongo-scala-driver" % "1.1.1",
  "net.cloudinsights" %% "play-plugins-salat" % "1.5.9",
  "com.github.shayanlinux" % "play-plugins-salat_2.11" % "1.6.0",
//Thanks for using https://jar-download.com                ,
  "com.novus" %% "salat" % "1.9.9",
  "se.radley" %% "play-plugins-salat" % "1.5.0",
  "org.mongodb" %% "casbah" % "3.1.1",
  "com.typesafe.play" %% "play-json" % "2.5.19",
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
  specs2 % Test,
  cache,
  filters
)

fork in run := false

libraryDependencies += "javax.xml.bind" % "jaxb-api" % "2.2.12"
routesGenerator := InjectedRoutesGenerator

RoutesKeys.routesImport += "play.modules.reactivemongo.PathBindables._"