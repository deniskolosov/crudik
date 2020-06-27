(ns crudik-frontend.events
  (:require
   [re-frame.core :as re-frame]
   [crudik-frontend.db :as db]
   ))

(defn allocate-next-id
  "Returns the next todo id.
  Assumes todos are sorted.
  Returns one more than the current largest id."
  [db]
  )

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::edit-patient
 (fn [db [_ data]]
   (println "Hello from edit patient")
   (assoc-in db [(:id data)] data)))

(re-frame/reg-event-db                     ;; given patient data, create a patient
 ::add-patient
 (fn [db [_ data]]
   (let [next-id ((fnil inc 0) (last (keys (:patients db))))]
     (assoc-in db [:patients next-id] data))))
