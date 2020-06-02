(ns cljs-src.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [reagent.dom :as rdom]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(enable-console-print!)

(println "This text is printed from src/cljs-src/core.cljs. Go ahead and edit it and see reloading in action.")

(def patients-atom (reagent/atom ()))

(defn make-remote-call [endpoint]
  (go (let [response (<! (http/get endpoint {:with-credentials? false}))]
    (js/console.log (clj->js (:body response))) (swap! patients-atom conj (:body response)))))

(defn lister [items]
  [:ul
   (for [item items]
     ^{:key item} [:li "Item " item])])

(defn app []
  [:div
   "Here is a list:"
   [lister (first @patients-atom)]])

(defn start []
(rdom/render [app]                   
  (js/document.getElementById "app"))
(make-remote-call "http://localhost:3000/patients"))
(start)