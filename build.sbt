name := """steel"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)


scalaVersion := "2.11.1"
//scalaVersion := "2.10.4"
resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
  "Typesafe Maven Repository" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
)

libraryDependencies ++= Seq(
  jdbc,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  cache,
  ws,
  "com.typesafe.slick" %% "slick"      % "3.0.0-RC1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.specs2" %% "specs2-core" % "3.6" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")

fork in run := false
