(ns crudik-frontend.views
  (:require
   [re-frame.core :as re-frame]
   [reagent.core  :as reagent]
   [crudik-frontend.subs :as subs]
   [crudik-frontend.events :as events]
   ))


(enable-console-print!)

(defn patient-input [atom key]
  [:input {:type     (if (= key :birthdate) "date" "text")
           :name     (name key)
           :value    (get @atom key) :on-change (fn [e] (swap! atom
                                     assoc key
                                     (.. e -target -value)))}])

(defn patient-form []
  (let [initial-form-values {:fullname     ""
                             :sex  ""
                             :address    ""
                             :insurance  ""
                             :birthdate ""}
        patient-state (reagent/atom initial-form-values)]
    (fn []
      [:tr
       [:td [patient-input  patient-state :fullname]]
       [:td [patient-input  patient-state :sex]]
       [:td [patient-input  patient-state :address]]
       [:td [patient-input  patient-state :insurance]]
       [:td [patient-input  patient-state :birthdate]]
       [:td [:button.btn.btn-primary.pull-right
             {:name "add"
              :on-click  (fn []
                           (re-frame/dispatch [::events/add-patient-http @patient-state])
                           (reset! patient-state initial-form-values))}
             "Add"]]])))


(defn patient-row [p]
  (let [editing? (reagent/atom false)
        patient (reagent/atom @(re-frame/subscribe [::subs/patient (:id p)]))]
    (fn [{:keys [id fullname sex address insurance birthdate]}]
      (if @editing?
        [:tr
         [:td [patient-input patient :fullname ]]
         [:td [patient-input patient :sex ]]
         [:td [patient-input patient :address ]]
         [:td [patient-input patient :insurance ]]
         [:td [patient-input patient :birthdate ]]
         [:td [:button.btn.btn-primary.pull-right
               {:name "save"
                :on-click  (fn []
                             (re-frame/dispatch [::events/edit-patient-http @patient])
                             (reset! editing? false))}
               "Save"]]]
        [:tr
         [:td (@patient :fullname)]
         [:td (@patient :sex)]
         [:td (@patient :address)]
         [:td (@patient :insurance)]
         [:td (@patient :birthdate)]
         [:td [:button.btn.btn-primary.pull-right
               {:name "edit"
                :on-click #(reset! editing? true)}
               "Edit"]]
         [:td [:button.btn.pull-right.btn-danger
               {:on-click #(re-frame/dispatch
                            [::events/delete-patient-http id])
                :name "delete"}
               "\u00D7"]]]
        ))))



(defn main-panel []
  ;; todo: display list of patients from db
  (let [patients (re-frame/subscribe [::subs/patients])]
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
       (for [patient @patients]
         ^{:key (str "patient-row-" (patient 0))}
         [patient-row (patient 1)])
       [patient-form]
       ]]]))
