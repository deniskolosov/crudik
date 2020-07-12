(ns crudik-frontend.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::patients
 (fn [db]
   (:patients db)))


(re-frame/reg-sub
 ::patient
 (fn [db [_ patient-id]]
   (get-in db [:patients patient-id])))

(re-frame/reg-sub
 ::patient-value-by-key-id
 (fn [db [_ patient-id key]]
   (get-in db [:patients patient-id key])))
