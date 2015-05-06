name := """steel"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)


scalaVersion := "2.11.1"
//scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  jdbc,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  cache,
  ws,
  "com.typesafe.slick" %% "slick"      % "3.0.0-RC1",
  "org.slf4j" % "slf4j-nop" % "1.6.4"
)


fork in run := false
