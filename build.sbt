name := """steel"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)


scalaVersion := "2.11.1"
//scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  javaJdbc,
  cache,
  ws,
  "com.typesafe.play" % "play-slick_2.11" % "0.8.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4"
)


fork in run := false
