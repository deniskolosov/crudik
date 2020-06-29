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

(re-frame/reg-event-db
 ::insert-patients
 (fn [db [_ result]]
   (println "in good http")
   (assoc db :patients (into {} (for [p result] {(:id p) p})))))

(re-frame/reg-event-db
 ::bad-http-result
 (fn [db [_ result]]
   ;; result is a map containing details of the failure
   (assoc db :bad-http-result result)))

(re-frame/reg-event-db
 ::add-patient
 (fn [db [_ data]]
   (let [next-id ((fnil inc 0) (last (keys (:patients db))))
         data (assoc data :id next-id)]
     (assoc-in db [:patients next-id] data))))

(re-frame/reg-event-db
 ::edit-patient
 (fn [db [_ id data]]
   (println "Hello edit" data)
   (assoc-in db [:patients id] data)))

(re-frame/reg-event-db
 ::delete-patient
 (fn [db [_ id]]
   (update-in db [:patients] dissoc id)))
