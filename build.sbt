name := """steel"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.5"
resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
  "Typesafe Maven Repository" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "Typesafe ivy" at "http://dl.bintray.com/typesafe/ivy-releases",
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
)

libraryDependencies ++= Seq(
  jdbc,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  cache,
  ws,
  "com.typesafe.play" %% "play-slick" % "1.0.0-RC1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "joda-time" % "joda-time" % "2.7",
  "org.joda" % "joda-convert" % "1.7",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.0.0",
  "net.sourceforge.htmlunit" % "htmlunit" % "2.14" % "test",
  specs2 % Test,
  "com.adrianhurt" %% "play-bootstrap3" % "0.4.2"
)

scalacOptions in Test ++= Seq("-Yrangepos")
javaOptions in Test ++= Seq("-Dconfig.file=conf/test.conf")

Seq(flywaySettings: _*)

flywayUrl  := "jdbc:postgresql:steel_dev"

flywayUser := "steel_dev"

flywayPassword := "huehuehue"

flywaySchemas := Seq("public")

flywayTable := "schema_history"

flywayLocations := Seq("filesystem:sql")

fork in run := false
