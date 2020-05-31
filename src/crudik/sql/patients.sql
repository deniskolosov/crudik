-- :name create-patients-table :! 
CREATE TABLE IF NOT EXISTS patients (
  id serial PRIMARY KEY,
  fullname varchar NOT NULL,
  sex varchar NOT NULL,
  address text NOT NULL,
  insurance varchar NOT NULL,
  birthdate date NOT NULL,
  created_at timestamp NOT NULL default current_timestamp
)
 
-- :name drop-patients-table :! 
DROP TABLE IF EXISTS patients

-- :name get-patients :? :*
SELECT * FROM patients;
 
-- :name insert-patient :! :n 
INSERT INTO patients (fullname, sex, address, insurance, birthdate)
VALUES (:fullname, :sex, :address, :insurance, :birthdate)
 
-- :name patient-by-id :? :1
-- :doc Get patient by id
SELECT * FROM patients
WHERE id = :id

-- :name update-patient :! :n
UPDATE patients
SET fullname = :fullname,
sex = :sex,
address = :address,
insurance = :insurance,
birthdate = :birthdate
WHERE id = :id
 
-- :name delete-patient :! :n
DELETE FROM patients WHERE id = :id