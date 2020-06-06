(ns cljs-src.core
  (:require-macros [cljs.core.async.macros :refer (go)])
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [cljs-http.client :as http]
   [cljs.core.async :refer (<!)]
   [schema.core :as s]))

(enable-console-print!)

(def Patients
  "A schema for patients state"
  #{{:id s/Int
     :sex s/Str
     :fullname s/Str
     :address s/Str
     :insurance s/Str
     :birthdate s/Str}})

(defonce patients-state
  (atom #{}
        :validator
        (fn [n]
          (s/validate Patients n))))

;; initial call to get patients from server
(go (let [response
          (<! (http/get "/patients"))
          data (:body response)]      
      (reset! patients-state (set data))))

;;; crud operations

(defn remove-by-id [s id]
  (set (remove #(= id (:id %)) s)))

(defn add-patient! [p]
  (go (let [response
            (<! (http/post "/patient" {:edn-params
                                       p}))]
        (swap! patients-state conj (:body response)))))

(defn remove-patient! [p]
  (go (let [response
            (<! (http/delete (str "/patients/id/"
                                  (:id p))))]
        (if (= 200 (:status response))
          (swap! patients-state remove-by-id (:id p))))))
 
(defn update-patient! [p]
  (go (let [response
            (<! (http/put (str "/patients/id/" (:id p))
                          {:edn-params p}))
            updated-patient (:body response)]
        (swap! patients-state
               (fn [old-state]
                 (conj
                  (remove-by-id old-state (:id p))
                  updated-patient))))))

;;; end crud operations

(defn editable-input [atom key]
  (if (:editing? @atom)
    [:input {:type     "text"
             :value    (get @atom key)
             :on-change (fn [e] (swap! atom
                                       assoc key
                                       (.. e -target -value)))}]
    [:p (get @atom key)]))

(defn input-valid? [atom]
  (and (seq (-> @atom :fullname))
       (seq (-> @atom :sex))
       (seq (-> @atom :address))
       (seq (-> @atom :insurance))
       (seq (-> @atom :birthdate))))

(defn patient-row [p]
  (let [row-state (atom {:editing? false
                         :fullname     (:fullname p)
                         :sex  (:sex p)
                         :address (:address p)
                         :insurance (:insurance p)
                         :birthdate (:birthdate p) })
        current-patient (fn []
                         (assoc p
                                :fullname (:fullname @row-state)
                                :sex (:sex @row-state)
                                :address (:address @row-state)
                                :insurance (:insurance @row-state)
                                :birthdate (:birthdate @row-state)))]
    (fn []
      [:tr
       [:td [editable-input row-state :fullname]]
       [:td [editable-input row-state :sex]]
       [:td [editable-input row-state :address]]
       [:td [editable-input row-state :insurance]]
       [:td [editable-input row-state :birthdate]]
       [:td [:button.btn.btn-primary.pull-right
             {:disabled (not (input-valid? row-state))
              :on-click (fn []
                         (when (:editing? @row-state)
                           (update-patient! (current-patient)))
                         (swap! row-state update-in [:editing?] not))}
             (if (:editing? @row-state) "Save" "Edit")]]
       [:td [:button.btn.pull-right.btn-danger
             {:on-click #(remove-patient! (current-patient))}
             "\u00D7"]]])))

(defn patient-form []
  (let [initial-form-values {:fullname     ""
                             :sex  ""
                             :address    ""
                             :insurance  ""
                             :birthdate ""
                             :editing? true}
        form-input-state (atom initial-form-values)]
    (fn []
      [:tr
       [:td [editable-input form-input-state :fullname]]
       [:td [editable-input form-input-state :sex]]
       [:td [editable-input form-input-state :address]]
       [:td [editable-input form-input-state :insurance]]
       [:td [editable-input form-input-state :birthdate]]
       [:td [:button.btn.btn-primary.pull-right
             {:disabled (not (input-valid? form-input-state))
              :on-click  (fn []
                          (add-patient! @form-input-state)
                          (reset! form-input-state initial-form-values))}
             "Add"]]])))

(defn patients []
  [:div
   [:table.table.table-striped
    [:thead
     [:tr
      [:th "Full Name"] [:th "Sex"] [:th "Address"] [:th "Insurance #"] [:th "Birthdate"]  [:th ""] [:th ""] [:th ""] [:th ""] [:th ""]]]
    [:tbody
     (map (fn [p]
            ^{:key (str "patient-row-" (:id p))}
            [patient-row p])
          (sort-by :fullname @patients-state))
     [patient-form]]]])

(defn start []
  (rdom/render [patients]
                            (js/document.getElementById "app")))
(start)