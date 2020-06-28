(ns crudik-frontend.events
  (:require
   [re-frame.core :as re-frame]
   [crudik-frontend.db :as db]
   ))

(defn allocate-next-id
  [db]
  )

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

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
