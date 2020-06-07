(ns crudik.integration-test
  "A module for integration tests"
  (:require [clojure.test :refer :all]
            [crudik.test-utils :as tu] 
            [etaoin.api :refer :all]))


(use-fixtures :once tu/test-db-fixture) 
 
(def chrome-driver (chrome {:headless true}))

(deftest test-create-update-delete-patient-frontend
   (go chrome-driver "http://localhost:3000/")
   (wait-visible chrome-driver [{:tag :input}])
   (fill chrome-driver {:tag :input :name :fullname} "Test Test")
   (fill chrome-driver  {:tag :input :name :address} "Test Address")
   (fill chrome-driver  {:tag :input :name :sex} "male")
   (fill chrome-driver  {:tag :input :name :insurance} "testinsurance123")
   (fill chrome-driver  {:tag :input :name :birthdate} "10-10-2020")
   (click chrome-driver {:tag :button :name :add})
   (refresh chrome-driver)
   (wait-visible chrome-driver [{:tag :input}])
   (is (= (has-text? chrome-driver "Test Test") true))
   (is (= (has-text? chrome-driver "Test Address") true))
   (is (= (has-text? chrome-driver "male") true))
   (is (= (has-text? chrome-driver "testinsurance123") true))
   (click chrome-driver {:tag :button :name :edit})
   (fill chrome-driver {:tag :input :name :fullname} "Test Test Update")
   (fill chrome-driver  {:tag :input :name :address} "Test Address Update")
   (fill chrome-driver  {:tag :input :name :sex} "female")
   (fill chrome-driver  {:tag :input :name :insurance} "testinsurance123update")
   (fill chrome-driver  {:tag :input :name :birthdate} "11-10-2020")
   (click chrome-driver {:tag :button :name :edit})
   (refresh chrome-driver)
   (wait-visible chrome-driver [{:tag :input}])
   (wait 1)
   (is (= (has-text? chrome-driver "Test Test Update") true))
   (is (= (has-text? chrome-driver "Test Address Update") true))
   (is (= (has-text? chrome-driver "female") true))
   (is (= (has-text? chrome-driver "testinsurance123update") true))
   (click chrome-driver {:tag :button :name :delete})
   (refresh chrome-driver)
   (wait-visible chrome-driver [{:tag :input}])
   (is (= (has-text? chrome-driver "Test Test") false)))

