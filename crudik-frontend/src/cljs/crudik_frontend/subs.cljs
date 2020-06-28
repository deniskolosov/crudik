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
