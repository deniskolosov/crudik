(ns crudik-frontend.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::patients
 (fn [db]
   (:patients db)))

