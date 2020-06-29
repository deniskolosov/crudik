(ns crudik-frontend.events
  (:require
   [re-frame.core :as re-frame]
   [crudik-frontend.db :as db]
   [ajax.core :as ajax]))

(re-frame/reg-event-fx
 ::initialize-patients
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :get
                 :uri             "/patients"
                 :timeout         8000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [::insert-patients]
                 :on-failure      [::bad-http-result]}}))

(re-frame/reg-event-fx
 ::add-patient-http
 (fn [{:keys [db]} [_ data]]
   {:http-xhrio {:method          :post
                 :uri             "/patients"
                 :timeout         8000
                 :params          data
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [::add-patient]
                 :on-failure      [::bad-http-result]}}))

(re-frame/reg-event-fx
 ::edit-patient-http
 (fn [{:keys [db]} [_ data]]
   {:http-xhrio {:method          :put
                 :uri             (str "/patients/" (:id data))
                 :timeout         8000
                 :params          data
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [::edit-patient]
                 :on-failure      [::bad-http-result]}}))

(re-frame/reg-event-fx
 ::delete-patient-http
 (fn [{:keys [db]} [_ id]]
   {:http-xhrio {:method          :delete
                 :uri             (str "/patients/" id)
                 :timeout         8000
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [::delete-patient]
                 :on-failure      [::bad-http-result]}}))

(re-frame/reg-event-db
 ::insert-patients
 (fn [db [_ result]]
   (assoc db :patients (into {} (for [p result] {(:id p) p})))))

(re-frame/reg-event-db
 ::bad-http-result
 (fn [db [_ result]]
   ;; result is a map containing details of the failure
   (assoc db :bad-http-result result)))

(re-frame/reg-event-db
 ::add-patient
 (fn [db [_ data]]
   (let [id (:id data)]
     (assoc-in db [:patients id] data))))

(re-frame/reg-event-db
 ::edit-patient
 (fn [db [_ data]]
   (let [id (:id data)]
   (assoc-in db [:patients id] data))))

(re-frame/reg-event-db
 ::delete-patient
 (fn [db [_ data]]
   (update-in db [:patients] dissoc (:id data))))
