CREATE TABLE IF NOT EXISTS patients (
  id serial PRIMARY KEY,
  fullname varchar NOT NULL,
  sex varchar NOT NULL,
  address text NOT NULL,
  insurance varchar NOT NULL,
  birthdate date NOT NULL,
  created_at timestamp NOT NULL default current_timestamp
)
