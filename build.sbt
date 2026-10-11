ThisBuild / scalaVersion := "2.13.16"
ThisBuild / organization := "edu.icesi"
ThisBuild / version := "1.0.0"
Compile / run / fork := true
Compile / run / javaOptions ++= Seq("-Xms256m", "-Xmx2g")
Test / test := (Test / runMain).toTask(" ti1.AlgorithmTests").value

ThisBuild / scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")
