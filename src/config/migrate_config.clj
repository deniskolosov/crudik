(ns config.migrate-config 
 (:require [config.db.schema-migrations :as db-schema-migrations] 
           [crudik.db :as db] 
           [drift.builder :refer [incremental-migration-number-generator]])) 
 
(defn current-db-version 
  [] 
  (or (:version (db-schema-migrations/current-db-version db/spec)) 
      0)) 
 
(defn update-db-version 
 [version] 
 (db-schema-migrations/insert-db-version db/spec {:version version})) 
 
(defn migrate-config 
 [] 
 {:directory "/src/migrations" 
  :current-version current-db-version 
  :update-version update-db-version 
  :init (fn [_] (db-schema-migrations/create-schema-migrations-table db/spec)) 
  :migration-number-generator incremental-migration-number-generator})