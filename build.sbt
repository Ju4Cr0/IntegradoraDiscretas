
ThisBuild / scalaVersion := "2.13.16"
ThisBuild / organization := "edu.icesi"
ThisBuild / version := "1.0.0"

Compile / run / fork := true
Compile / run / javaOptions ++= Seq("-Xms256m", "-Xmx2g")

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.13.2" % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test
)

ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked"
)
