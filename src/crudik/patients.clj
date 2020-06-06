(ns crudik.patients 
 (:require [crudik.db :as db ]
 										[clj-time.core :as t]
 										[clj-time.format :as f]
 										[clj-time.coerce :as c])
 (:import [java.time LocalDate]))

(defn add-patient 
	[patient-data]
	(db/insert-patient db/spec (assoc patient-data :birthdate (c/to-sql-date (f/parse (f/formatters :year-month-day) (patient-data :birthdate)))))) ; Cast birthdate to Date and pass to db

(defn format-date [v] (f/unparse (f/formatters :year-month-day) (c/from-date v)))

(defn get-patient
 [id-data]
 (db/patient-by-id db/spec id-data))

(defn get-patients
 []
 (map #(update % :birthdate format-date) (db/get-patients db/spec)))

(defn update-patient
 [patient-data]
 (db/update-patient db/spec (assoc patient-data :birthdate (c/to-sql-date (f/parse (f/formatters :year-month-day) (patient-data :birthdate))))))

(defn delete-patient
 [id-data]
 (db/delete-patient db/spec id-data))
