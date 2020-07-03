(ns crudik.models.migration
  (:require [clojure.java.jdbc :as sql]
            [crudik.models.patient :as patient]))

(defn migrated? []
  (-> (sql/query patient/spec
                 [(str "select count(*) from information_schema.tables "
                       "where table_name='patients'")])
      first :count pos?))

(defn migrate []
  (when (not (migrated?))
    (print "Creating database structure...") (flush)
    (sql/db-do-commands patient/spec
                        (sql/create-table-ddl
                         :patients
                         [[:id :serial "PRIMARY KEY"]
                          [:fullname :varchar "NOT NULL"]
                          [:sex :varchar "NOT NULL"]
                          [:address :varchar "NOT NULL"]
                          [:insurance :varchar "NOT NULL"]
                          [:birthdate :date "NOT NULL"]
                          [:created_at :timestamp
                           "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]]))
    (println " done")))
