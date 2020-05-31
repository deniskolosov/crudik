(ns crudik.core-test 
 (:require [clojure.test :refer :all] 
           [crudik.test-utils :as tu] 
           [crudik.patients :as patients])) 
 
(use-fixtures :once tu/test-db-fixture) 
 
(deftest patient-test 
  (testing "Adding/getting patients" 
    (patients/add-patient {:birthdate "2000-10-10", :address "string", :insurance "string", :sex "string", :fullname "string"}) 
    (let [{:keys [id fullname address]} (patients/get-patient {:id 1})] 
      (is (= id 1)) 
      (is (= fullname "string")) 
      (is (= address "string")))))
