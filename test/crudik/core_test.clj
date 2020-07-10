(ns crudik.core-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [crudik.models.patient :as models]
            [clojure.java.jdbc :as sql]
            [muuntaja.core :as m]
            [crudik.core :as core]))


(def test-db-spec
  {:subprotocol "postgresql"
   :subname "//localhost:5432/patients_test"
   :user "test_user"
   :password ""})

(def test-patient
  {:fullname "IVAN IVANOVICH"
   :sex "male"
   :address "My address"
   :insurance "foobar123"
   :birthdate "1990-10-09"})

(defn init-test-db []
  (sql/db-do-commands test-db-spec
                      (sql/create-table-ddl
                       :patients
                       [[:id :serial "PRIMARY KEY"]
                        [:fullname :varchar "NOT NULL"]
                        [:sex :varchar "NOT NULL"]
                        [:address :varchar "NOT NULL"]
                        [:insurance :varchar "NOT NULL"]
                        [:birthdate :date "NOT NULL"]
                        [:created_at :timestamp
                         "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]]
                       {:conditional? true}
                       )))

(defn drop-test-table []
  (println "dropping test table")
  (sql/db-do-commands test-db-spec
                      (sql/drop-table-ddl :patients)))

(defn delete-all-rows []
  (sql/execute! test-db-spec ["DELETE FROM patients"]))

(defn create-test-patient [db]
  (models/add-patient test-patient db))

(defn test-db-fixture-each [test]
  (with-redefs [models/spec test-db-spec]
    (create-test-patient test-db-spec)
    (test)
    (delete-all-rows)))

(defn test-db-fixture-once [tests]
  (with-redefs [models/spec test-db-spec]
    (init-test-db)
    (tests)
    (drop-test-table)))

(use-fixtures :each test-db-fixture-each)
(use-fixtures :once test-db-fixture-once)


(deftest get-patients-test
  (testing "Get patients from test db"
      (let [req (mock/request :get "/patients")
            resp (core/app req)
            decoded-resp (m/decode "application/json" (resp :body))]
        (is (= 200 (resp :status)))
        (is (= test-patient (dissoc (first decoded-resp) :id))))))

(deftest add-patient-test
  (testing "Add patient to test db")
  (let [patient {:fullname "Test Test"
                 :sex "male"
                 :address "My address"
                 :insurance "foobar123"
                 :birthdate "1990-10-10"}
        resp (core/app (-> (mock/request :post "/patients")
                           (mock/json-body patient)))
        decoded-resp (m/decode "application/json" (resp :body))]
    (is (= (resp :status) 200))
    (is (= (dissoc (first (models/get-patient (decoded-resp :id) test-db-spec)) :id ) patient))))

(deftest get-patient-test
  (testing "Get single patient from test db")
  (let [patient (create-test-patient test-db-spec)
        test-id (:id patient)
        req (mock/request :get (str "/patients/" test-id))
        resp (core/app req)
        decoded-resp (m/decode "application/json" (resp :body))]
    (println req test-id)
    (is (= 200 (resp :status)))
    (is (= patient (first (models/get-patient test-id test-db-spec))))))

(deftest update-patient-test
  (testing "Edit patient in test db")
  (let [patient (assoc (create-test-patient test-db-spec) :fullname "Edited Test")
        test-id (:id patient)
        resp (core/app (-> (mock/request :put (str "/patients/" test-id))
                           (mock/json-body patient)))
        decoded-resp (m/decode "application/json" (resp :body))]
    (is (= 200 (resp :status)))
    (is (= patient (first (models/get-patient test-id test-db-spec))))))

(deftest delete-patient-test
  (testing "Delete patient in test db")
  (let [patient (create-test-patient test-db-spec)
        test-id (:id patient)
        req (mock/request :delete (str "/patients/" test-id))
        resp (core/app req)
        decoded-resp (m/decode "application/json" (resp :body))]
    (is (= 200 (resp :status)))
    (is (= [] (models/get-patient test-id test-db-spec)))))


