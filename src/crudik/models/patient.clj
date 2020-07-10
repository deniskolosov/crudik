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


(defn bd-to-sql-date
  [patient-data]
  (assoc patient-data :birthdate
         (c/to-sql-date
          (f/parse (f/formatters :year-month-day) (patient-data :birthdate)))))

(def tz-formatter (f/with-zone (f/formatters :year-month-day) (t/default-time-zone)))

(defn format-birthdate [v] (f/unparse tz-formatter
                                      (c/from-sql-date
                                       (:birthdate v))))

(defn parse-bdate [patient-data]
  (let [formatted-bdate (format-birthdate patient-data)]
    (dissoc (assoc patient-data :birthdate formatted-bdate) :created_at)))

(defn all [db-spec]
  (into [] (map parse-bdate (sql/query db-spec ["select * from patients order by id desc"]))))

(defn add-patient
  [patient-data db-spec]
  (let [patient (bd-to-sql-date patient-data)]
    (parse-bdate (first (sql/insert! db-spec :patients patient)))))


(defn get-patient
  [id db-spec]
  (map parse-bdate (sql/query db-spec ["select * from patients where id= ?" id])))


(defn update-patient [patient-data db-spec]
  (let [patient (bd-to-sql-date patient-data)]
    (map parse-bdate (sql/update! db-spec :patients patient ["id = ?" (:id patient-data)]))))

(defn delete-patient
  [id db-spec]
  (sql/delete! db-spec :patients ["id = ?" id]))
