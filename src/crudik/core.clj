(ns crudik.core
  (:require
   [reitit.core :as r]
   [reitit.ring :as ring]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.coercion.spec]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.exception :as exception]
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [muuntaja.core :as m]
   [ring.middleware.cors :refer [wrap-cors]]
   [ring.adapter.jetty :as jetty]
   [crudik.models.patient :as models]))

;; handlers
(defn add-patient
  [{:keys [parameters]}]
  (let [patient-data (:body parameters)]
    {:status 200 :body (models/add-patient patient-data models/spec)}))

(defn get-patient-by-id
  [{:keys [parameters]}]
  {:status 200 :body (first (models/get-patient (get-in parameters [:path :id]) models/spec))})

(defn get-patients [_]
{:status 200
 :body (models/all models/spec)})

(defn update-patient
[{:keys [parameters]}]
  (let [patient-data (assoc (:body parameters) :id (get-in parameters [:path :id]))]
   (models/update-patient patient-data models/spec)
  {:status 200
   :body patient-data}))

(defn delete-patient-by-id
 [{:keys [parameters]}]
  (let [id (get-in parameters [:path :id])]
    (models/delete-patient id models/spec)
    {:status 200 :body {:status "Ok" :id id}}))

;; routes
(def routes
 [["/swagger.json"
   {:get {:handler (swagger/create-swagger-handler)}}]
   ["/patients"
       [""
        {:get {:summary "Get all patients"
               :handler get-patients}
         :post {:summary "Create new patient record"
                :parameters {:body {:fullname string?
                                    :sex string?
                                    :address string?
                                    :insurance string?
                                    :birthdate string?}}
                :responses {200 {:body {:fullname string?
                                        :sex string?
                                        :address string?
                                        :insurance string?
                                        :birthdate string?}}}
                :handler add-patient}}]

       ["/:id"
         {:get {:summary "Get patient record"
                :parameters {:path {:id int?}}
                :responses {200 {:body {:fullname string?
                                        :sex string?
                                        :address string?
                                        :insurance string?
                                        :birthdate string?}}}
                :handler get-patient-by-id}
          :put {:summary "Update patient record"
                :parameters {:path {:id int?}
                             :body {:fullname string?
                                    :sex string?
                                    :address string?
                                    :insurance string?
                                    :birthdate string?}}
                :responses {200 {:body {:fullname string?
                                        :sex string?
                                        :address string?
                                        :insurance string?
                                        :birthdate string?}}}
                :handler update-patient }

          :delete {:summary "Delete patient record"
                   :parameters {:path {:id int?}}
                   :responses {200 {:body string?}}
                   :handler delete-patient-by-id }}]]])



(def router
 (ring/router routes
               {:data {
                :coercion reitit.coercion.spec/coercion
                :muuntaja  m/instance
                :middleware [swagger/swagger-feature
                             muuntaja/format-middleware
                             ;exception/exception-middleware
                             coercion/coerce-request-middleware
                             ]}}))

(def app
  (ring/ring-handler router
                     (ring/routes
                       (swagger-ui/create-swagger-ui-handler
                         {:path "/swagger"})
                       (ring/create-resource-handler {:path "/" })
                       (ring/create-default-handler))))

