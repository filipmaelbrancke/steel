# --- !Ups
 
CREATE TABLE person (
  id serial NOT NULL,
  email varchar(255) NOT NULL,
  password varchar(255) NOT NULL,
  created_at timestamp with time zone default (now() at time zone 'utc'),
  updated_at timestamp with time zone,
  PRIMARY KEY (id)
);

CREATE INDEX idx_person_email ON person (email);

CREATE TABLE exercise_type (
  id serial NOT NULL,
  name varchar(255) NOT NULL,
  description TEXT,
  created_at timestamp with time zone default(now() at time zone 'utc'),
  updated_at timestamp with time zone
);

CREATE TABLE exercise (
  id serial NOT NULL,
  type int references exercise_type(id) NOT NULL,
  name varchar(255) NOT NULL,
  reps int,
  weight int,
  time int,
  notes text,
  person int references person(id),
  created_at timestamp with time zone default(now() at time zone 'utc'),
  updated_at timestamp with time zone
);
  

# --- !Downs
 
DROP TABLE person;

DROP INDEX idx_person_email;

DROP TABLE exercise_type;

DROP TABLE exercise;
