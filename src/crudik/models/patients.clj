(ns crudik.models.patient
  (:require [clojure.java.jdbc :as sql]))

(def spec (or (System/getenv "DATABASE_URL")
              "postgresql://localhost:5432/patients"))

(defn all []
  (into [] (sql/query spec ["select * from patients order by id desc"])))

(defn add-patient [patient]
  (sql/insert! spec :patients [:body] [patient]))
