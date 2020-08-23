import com.typesafe.sbt.SbtScalariform._

import scalariform.formatter.preferences._

name := """silhouette"""
organization := "silhouette"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

resolvers += Resolver.jcenterRepo
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
// Resolver is needed only for SNAPSHOT versions
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

lazy val defaultDependencies = {
  val scalikeJdbcDependencies = {
    val scalikeJdbcVersion = "3.2.3"
    List(
      "org.scalikejdbc" %% "scalikejdbc"        % scalikeJdbcVersion,
      "org.scalikejdbc" %% "scalikejdbc-config" % scalikeJdbcVersion,
      "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.6.0-scalikejdbc-3.2"
    )
  }

  List(
    evolutions, jdbc, guice,
    "com.h2database"  %  "h2"                 % "1.4.197",
    "ch.qos.logback"  %  "logback-classic"    % "1.2.+",
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
  ) ++ scalikeJdbcDependencies
}

libraryDependencies ++= defaultDependencies

libraryDependencies ++= Seq(
  evolutions, jdbc, guice, ehcache, filters,
  "org.reactivemongo" %% "reactivemongo-play-json-compat" % "0.20.12-play26",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.20.11-play26",
  "com.mohiva" %% "play-silhouette" % "6.1.1",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "6.1.1",
  "com.mohiva" %% "play-silhouette-persistence" % "6.1.1",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "6.1.1",
  "com.mohiva" %% "play-silhouette-totp" % "6.1.1",
  "org.webjars" %% "webjars-play" % "2.8.0",
  "org.webjars" % "bootstrap" % "4.4.1" exclude("org.webjars", "jquery"),
  "org.webjars" % "jquery" % "3.2.1",
  "net.codingwell" %% "scala-guice" % "4.2.6",
  "com.iheart" %% "ficus" % "1.4.7",
  "com.typesafe.play" %% "play-mailer" % "7.0.1",
  "com.typesafe.play" %% "play-mailer-guice" % "7.0.1",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.8.3-akka-2.6.x",
  "com.adrianhurt" %% "play-bootstrap" % "1.5.1-P27-B4",
  "com.mohiva" %% "play-silhouette-testkit" % "6.1.1" % "test",
  specs2 % Test,
)

libraryDependencies ++= defaultDependencies

// sbt scalafmtでコードフォーマット
//scalafmtConfig := Some(file(".scalafmt.conf"))
//scalafmtOnCompile := true // compile時に自動でコードフォーマット

evictionWarningOptions in update := EvictionWarningOptions.default.withWarnEvictionSummary(false)

libraryDependencies += "javax.xml.bind" % "jaxb-api" % "2.2.12"

//********************************************************
// Scalariform settings
//********************************************************

scalariformAutoformat := true

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(FormatXml, false)
  .setPreference(DoubleIndentConstructorArguments, false)
  .setPreference(DanglingCloseParenthesis, Preserve)