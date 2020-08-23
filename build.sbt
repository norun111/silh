import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._

name := """silhouette"""
organization := "silhouette"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

version := "4.0.0"

scalaVersion := "2.11.8"

resolvers += Resolver.jcenterRepo
// Resolver is needed only for SNAPSHOT versions

libraryDependencies ++= Seq(
  specs2 % Test,
  filters,
)

routesGenerator := InjectedRoutesGenerator

libraryDependencies += "javax.xml.bind" % "jaxb-api" % "2.2.12"