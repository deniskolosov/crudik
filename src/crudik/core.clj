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
		 [crudik.patients :as patients ]))


(defn add-patient 
  [{:keys [parameters]}]
  (patients/add-patient (:body parameters))
  {:status 200 :body "Ok"})

(defn get-patient-by-id 
  [{:keys [parameters]}]
    {:status 200 :body (patients/get-patient (parameters :path))})

(defn get-patients [_]
{:status 200 :headers {"Access-Control-Allow-Origin" "http://localhost:3449" "Access-Control-Allow-Methods" "GET, PUT, POST, DELETE"} :body (patients/get-patients _) })

(defn update-patient 
[{:keys [parameters]}]
		(println parameters)
  (patients/update-patient (assoc (:body parameters) :id (get-in parameters [:path :id])))
  {:status 200 :headers {"Access-Control-Allow-Origin" "http://localhost:3449" "Access-Control-Allow-Methods" "GET, PUT, POST, DELETE"} :body "Ok"})

(defn delete-patient-by-id
 [{:keys [parameters]}]
 (patients/delete-patient (parameters :path))
  {:status 200 :body "Ok" })

(def routes
	[
	["/swagger.json"
	  {:get {:handler (swagger/create-swagger-handler)}}]

	  ["/patients"
			  {:swagger {:tags ["patients"]}}

					  [""
						  {:get {:summary "Get all patients"
						         :handler get-patients}
						   :post {:summary "Create new patient record"
						   							:parameters {:body {:fullname string? 
						   								                   :sex string?
						   								                   :address string?
						   								                   :insurance string?
						   								                   :birthdate string?}}
						   							:responses {200 {:body string?}}
												    :handler add-patient}}]

						 ["/id/:id"
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
						   							:responses {200 {:body string?}}
												    :handler update-patient	}

						    :delete {:summary "Delete patient record"
															    :parameters {:path {:id int?}}
														     :handler delete-patient-by-id }}]]])



(def router
 (ring/router routes
               {:data {
               	:coercion reitit.coercion.spec/coercion
               	:muuntaja  m/instance
               	:middleware [swagger/swagger-feature
													               	muuntaja/format-middleware
													               	exception/exception-middleware
													               	coercion/coerce-request-middleware
													               	; coercion/coerce-response-middleware
													               	]}}))

(def app
  (ring/ring-handler router
                     (ring/routes 
                       (swagger-ui/create-swagger-ui-handler
                         {:path "/"}))
                     {:middleware [[wrap-cors :access-control-allow-origin [#"http://localhost:3000"]
                                              :access-control-allow-methods [:get :put :post :delete]]]}))

