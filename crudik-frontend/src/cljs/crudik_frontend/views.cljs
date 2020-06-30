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

(defn patient-input-fields [patient-state]
  (doall (for [key [:fullname :sex :address :insurance :birthdate] :let [id (:id @patient-state)]]
           ^{:key (str "patient-" id "-input-" key)}
           [:td [patient-input patient-state key]])))

(defn patient-form []
  (let [initial-form-values {:fullname  ""
                             :sex       ""
                             :address   ""
                             :insurance ""
                             :birthdate ""}
        patient-state (reagent/atom initial-form-values)]
    (fn []
      [:tr
       (patient-input-fields patient-state)
       [:td [:button.btn.btn-primary.pull-right
             {:name "add"
              :on-click  (fn []
                           (re-frame/dispatch [::events/add-patient-http @patient-state])
                           (reset! patient-state initial-form-values))}
             "Add"]]])))


(defn patient-row [patient-id]
  (let [editing? (reagent/atom false)
        patient (reagent/atom @(re-frame/subscribe [::subs/patient patient-id]))]
    (fn []
      (if @editing?
        ;; Patient edit form.
        [:tr
         (patient-input-fields patient)
         [:td [:button.btn.btn-primary.pull-right
               {:name "save"
                :on-click  (fn []
                             (re-frame/dispatch [::events/edit-patient-http @patient])
                             (reset! editing? false))}
               "Save"]]]
        ;; Patient info fields.
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
                            [::events/delete-patient-http patient-id])
                :name "delete"}
               "\u00D7"]]]
        ))))



(defn main-panel []
  ;; todo: display list of patients from db
  (let [patients (re-frame/subscribe [::subs/patients])]
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
         (let [id (patient 0)]
           ^{:key (str "patient-row-" id)}
           [patient-row id]))
       [patient-form]
       ]]]))
