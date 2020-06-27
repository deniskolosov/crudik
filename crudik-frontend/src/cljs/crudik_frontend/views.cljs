(ns crudik-frontend.views
  (:require
   [re-frame.core :as re-frame]
   [reagent.core  :as reagent]
   [crudik-frontend.subs :as subs]
   [crudik-frontend.events :as events]
   ))


(enable-console-print!)

(defn editable-input [atom key]
  (if (:editing? @atom)
    [:input {:type     "text"
             :name     (name key)
             :value    (get @atom key)
             :on-change (fn [e] (swap! atom
                                       assoc key
                                       (.. e -target -value)))}]
    [:p (get @atom key)]))

(defn patient-form []
  (let [initial-form-values {:fullname     ""
                             :sex  ""
                             :address    ""
                             :insurance  ""
                             :birthdate ""
                             :editing? true}
        patient-state (reagent/atom initial-form-values)]
    (fn []
      [:tr
       [:td [editable-input patient-state :fullname]]
       [:td [editable-input patient-state :sex]]
       [:td [editable-input patient-state :address]]
       [:td [editable-input patient-state :insurance]]
       [:td [editable-input patient-state :birthdate]]
       [:td [:button.btn.btn-primary.pull-right
             {:name "add"
              :on-click  (fn []
                           ;; (add-patient! @patient-state)
                           (re-frame/dispatch [::events/add-patient @patient-state])
                           (reset! patient-state initial-form-values))}
             "Add"]]])))

(defn main-panel []
  ;; todo: display list of patients from db
  (let [patients (re-frame/subscribe [::subs/patients])
        p (get @patients 1)
        ]
    (println "patients" patients)
    [:div.col-md-6
     [:table.table.table-striped.col-md-6
      [:thead
       [:tr
        [:th "Name"]
        [:th "Sex"]
        [:th "Address"]
        [:th "Insurance #"]
        [:th "Birthdate"]]]
      [:tbody
       (for [patient @patients
             :let [p (patient 1)
                   id (patient 0)]]
         ^{:key (str "patient-row-" id)}
         [:tr
          [:td (p :fullname)]
          [:td (p :sex)]
          [:td (p :address)]
          [:td (p :insurance)]
          [:td (p :birthdate)]
          [:td [:button.btn.btn-primary.pull-right
                {:name "edit"
                 :on-click (fn [] (re-frame/dispatch [::events/edit-patient ]))}
                "Edit"]]])
          [patient-form] 
        ]]]
    ))
