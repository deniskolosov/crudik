(ns migrations.001-create-tables 
  (:require [crudik.db :as db]))
 
(defn up 
  [] 
  (db/create-patients-table db/spec)) 
 
(defn down 
  [] 
  (db/drop-patients-table db/spec))