(ns crudik.patients 
 (:require [crudik.db :as db ])
 (:import [java.time LocalDate]))

(defn add-patient 
	[patient-data]
	(db/insert-patient db/spec (assoc patient-data :birthdate (LocalDate/parse (patient-data :birthdate))))) ; Cast birthdate to Date and pass to db

(defn get-patient
 [id-data]
 (db/patient-by-id db/spec id-data))

(defn get-patients
 [_]
 (db/get-patients  db/spec ))

(defn update-patient
 [patient-data]
 (def v (db/update-patient db/spec (assoc patient-data :birthdate (LocalDate/parse (patient-data :birthdate))))))

(defn delete-patient
 [id-data]
 (db/delete-patient db/spec id-data))
