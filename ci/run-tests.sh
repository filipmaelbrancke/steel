if [ "$STEEL_TEST_LOCAL" == "1" ]; then 
  echo "DROPPING DBs"; 
  psql -Upostgres -c "drop table exercise; drop table person; drop table exercise_type; drop table schema_history ;" -d steel_test; 
fi
sbt -Dflyway.user=steel_test -Dflyway.password=test -Dflyway.url=jdbc:postgresql:steel_test flywayMigrate
sbt +test
