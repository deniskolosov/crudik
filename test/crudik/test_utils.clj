(ns crudik.test-utils 
 (:require [crudik.db :as db] 
										 [drift.core :as drift]
										 [drift.runner :as runner]
           [migrations.001-create-tables :as create-tables])) 
 
(def test-db-spec 
   {:subprotocol "postgresql" 
    :subname "//localhost:5432/patients_test" 
    :user "test_user" 
    :password ""}) 
 
(defn test-db-fixture 
  [tests] 
  (with-redefs [crudik.db/spec test-db-spec] 
    (drift/run-init nil) 
    (runner/migrate-up-all) 
    (try 
      (tests) 
      (finally 
        (runner/migrate-down-all)))))