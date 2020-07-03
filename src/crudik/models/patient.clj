(ns crudik.models.patient
  (:require [clojure.java.jdbc :as sql]
            [jdbc.pool.c3p0 :as pool]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.coerce :as c]))

(def spec
  (pool/make-datasource-spec
   {:subprotocol "postgresql"
    :subname (format "//%s:5432/crudik" (get (System/getenv) "DB_HOST" "localhost")) ;get postgres host from environment var
    :user "postgres"
    :password "postgres"}))

(defn all []
  (into [] (sql/query spec ["select * from patients order by id desc"])))

(defn bd-to-sql-date
  [patient-data]
  (assoc patient-data :birthdate
         (c/to-sql-date (f/parse (f/formatters :year-month-day) (patient-data :birthdate)))))

(defn add-patient
  [patient-data]
  (let [patient (bd-to-sql-date patient-data)]
    (first (sql/insert! spec :patients patient))))

(defn get-patient
  [id]
  (sql/query spec ["select * from patients where id= ?" id]))

(defn update-patient [patient-data]
  (let [patient (bd-to-sql-date patient-data)]
    (sql/update! spec :patients patient ["id = ?" (:id patient-data)])))

(defn delete-patient
  [id]
  (sql/delete! spec :patients ["id = ?" id]))
