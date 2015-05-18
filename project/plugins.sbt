resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
  "Typesafe Maven Repository" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  "Typesafe ivy" at "http://dl.bintray.com/typesafe/ivy-releases",
  "Flyway" at "http://flywaydb.org/repo"
)

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.0-RC1")

// web plugins

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.0.0")

// flyaway
addSbtPlugin("org.flywaydb" % "flyway-sbt" % "3.2.1")
