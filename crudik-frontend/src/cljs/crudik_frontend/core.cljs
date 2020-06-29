(ns crudik-frontend.core
  (:require-macros [cljs.core.async.macros :refer (go)])
  (:require
   [reagent.core :as reagent]
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [crudik-frontend.events :as events]
   [crudik-frontend.views :as views]
   [crudik-frontend.config :as config]
   [day8.re-frame.http-fx]
   ))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-patients])
  (dev-setup)
  (mount-root))
