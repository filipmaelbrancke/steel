CREATE TABLE person (
  id serial NOT NULL,
  email varchar(255) NOT NULL,
  password varchar(255) NOT NULL,
  created_at timestamp with time zone default (now() at time zone 'utc'),
  updated_at timestamp with time zone,
  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX person_email_idx ON person (email);

CREATE TABLE exercise_type (
  id serial NOT NULL,
  kind varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  description TEXT,
  created_at timestamp with time zone default(now() at time zone 'utc'),
  updated_at timestamp with time zone,
  PRIMARY KEY (id)
);

CREATE TABLE exercise (
  id serial NOT NULL,
  kind int references exercise_type(id) NOT NULL,
  reps int,
  weight numeric(4,2),
  time numeric(4,2),
  notes text,
  person int references person(id),
  created_at timestamp with time zone default(now() at time zone 'utc'),
  updated_at timestamp with time zone,
  PRIMARY KEY (id)
);
  
