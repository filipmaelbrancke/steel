sbt -Dflyway.user=steel_test -Dflyway.password=test -Dflyway.url=jdbc:postgresql:steel_test flywayMigrate
sbt +test
