if [ "$STEEL_TEST_LOCAL" == "1" ]; then 
  echo "DROPPING DBs"; 
  psql -Upostgres -c "drop table if exists exercise; drop table if exists person cascade; drop table if exists exercise_type; drop table if exists schema_history; drop table if exists workout ;" -d steel_test; 
fi
sbt -Dflyway.user=steel_test -Dflyway.password=test -Dflyway.url=jdbc:postgresql:steel_test flywayMigrate
sbt +test
