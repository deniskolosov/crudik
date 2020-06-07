(ns crudik.integration-test
  "A module for integration tests"
  (:require [clojure.test :refer :all]
            [crudik.test-utils :as tu] 
            [etaoin.api :refer :all]))


(use-fixtures :once tu/test-db-fixture) 
 
(def ffox-driver (chrome {:headless true}))

(deftest test-create-patient-frontend
   (go ffox-driver "http://localhost:3000/")
   (wait-visible ffox-driver [{:tag :input}])
   (fill ffox-driver {:tag :input :name :fullname} "Test Test")
   (fill ffox-driver  {:tag :input :name :address} "Test Address")
   (fill ffox-driver  {:tag :input :name :sex} "male")
   (fill ffox-driver  {:tag :input :name :insurance} "testinsurance123")
   (click ffox-driver {:tag :button :name :add})
   (refresh ffox-driver)
   (wait-visible ffox-driver [{:tag :input}])
   (is (= (has-text? ffox-driver "Test Test") true))
   (is (= (has-text? ffox-driver "Test Address") true))
   (is (= (has-text? ffox-driver "male") true))
   (is (= (has-text? ffox-driver "testinsurance123") true)))


(deftest test-update-patient-frontend
	(testing "Updating patients on frontend" 
    (go ffox-driver "http://localhost:3000/")
    (wait-visible  ffox-driver [{:tag :input}])
    (is (= (has-text? ffox-driver "integration test") true))))

(deftest test-delete-patient-frontend
	(testing "Updating patients on frontend" 
    (go ffox-driver "http://localhost:3000/")
    (wait-visible  ffox-driver [{:tag :input}])
    (is (= (has-text? ffox-driver "integration test") true))))
