(ns crudik.core-test
  (:require [clojure.test :refer :all]
            [crudik.test-utils :as tu]
            [crudik.core :as core]
            [crudik.patients :as patients]
            [ring.mock.request :as mock]
            [muuntaja.core :as m]))

(def m (m/create))

(use-fixtures :once tu/test-db-fixture)
(defn make-request
  "Helper function for making requests"
  ([uri meth]
   (core/app {:request-method meth :uri uri}))
  ([uri meth body]
   (core/app {:request-method meth :uri uri :body body})))

(deftest test-index
  (testing "Test index"
    (let [resp (make-request "/" :get)]
      (is (= (resp :status) 302))
      (is (= (resp :headers) {"Location" "/index.html"}))
      (is (= (resp :body) "")))))

(deftest test-swagger
  (testing "Test swagger endpoint"
    (let [resp (make-request "/swagger" :get)]
      (is (= (resp :status) 302))
      (is (= (resp :headers) {"Location" "/swagger/index.html"}))
      (is (= (resp :body) "")))))

(deftest test-get-patients
  (testing "Test getting patients with mock"
    (let [patients-list [{:fullname "Test Test"
                          :sex "male"
                          :address "My address"
                          :insurance "foobar123"
                          :birthdate "1990-10-10"}]]
         (with-redefs
           [patients/get-patients (constantly patients-list)]
           (let [req (mock/request :get "/patients")
                 resp (core/app req)]
             (is (= (resp :status) 200))
             (is (= (m/decode "application/json" (resp :body)) patients-list))
             (is (= (resp :headers) {"Content-Type" "application/json; charset=utf-8"})))))))

(deftest test-get-patient
  (testing "Test getting single patient with mock"
    (let [patient {:fullname "Test Test"
                    :sex "male"
                    :address "My address"
                    :insurance "foobar123"
                    :birthdate "1990-10-10"}]
      (with-redefs
        [patients/get-patient (constantly patient)]
        (let [req (mock/request :get "/patients/1")
              resp (core/app req)]
          (is (= (resp :status) 200))
          (is (= (m/decode "application/json" (:body resp)) patient))
          (is (= (resp :headers) {"Content-Type" "application/json; charset=utf-8"})))))))

(deftest test-add-patient
  (testing "Test adding patient with mock"
    (let [patient {:fullname "Test Test"
                   :sex "male"
                   :address "My address"
                   :insurance "foobar123"
                   :birthdate "1990-10-10"}]
      (with-redefs
        [patients/add-patient (constantly patient)]
        (let [resp (core/app (-> (mock/request :post "/patients")
                                 (mock/json-body patient)))]
          (is (= (resp :status) 200))
          (is (= (m/decode "application/json" (:body resp)) (assoc patient :id nil)))
          (is (= (resp :headers) {"Content-Type" "application/json; charset=utf-8"})))))))


(deftest test-update-patient
  (testing "Test update patient with mock"
    (let [updated-patient {:fullname "Test Test"
                           :sex "male"
                           :address "My address"
                           :insurance "foobar123update"
                           :birthdate "1990-10-10"}]
      (with-redefs
        [patients/update-patient (constantly updated-patient)]
        (let [resp (core/app (-> (mock/request :put "/patients/42")
                                 (mock/json-body updated-patient)))]
          (is (= (resp :status) 200))
          (is (= (m/decode "application/json" (:body resp)) (assoc updated-patient :id 42)))
          (is (= (resp :headers) {"Content-Type" "application/json; charset=utf-8"})))))))

(deftest test-delete-patient
  (testing "Test update patient with mock"
    (let [updated-patient {:fullname "Test Test"
                           :sex "male"
                           :address "My address"
                           :insurance "foobar123update"
                           :birthdate "1990-10-10"}]
      (let [resp (core/app (mock/request :delete "/patients/42"))]
        (is (= (resp :status) 200))
        (is (= (m/decode "application/json" (:body resp)) {:status "Ok"}))
        (is (= (resp :headers) {"Content-Type" "application/json; charset=utf-8"}))))))
