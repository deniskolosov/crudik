(ns crudik-frontend.views
  (:require
   [re-frame.core :as re-frame]
   [reagent.core :as reagent :refer [atom]]
   [crudik-frontend.subs :as subs]
   [crudik-frontend.events :as events]
   [clojure.string :as str]))

(enable-console-print!)

(defn patient-form-fields [patient-state]
  (doall (for [key [:fullname :sex :address :insurance :birthdate]]
           [:td [:input {:type     (if (= key :birthdate) "date" "text")
                         :name     (name key)
                         :value    (get @patient-state key)
                         :on-change (fn [e] (swap! patient-state
                                                   assoc key
                                                   (.. e -target -value)))}]])))

(defn patient-form []
  (let [initial-form-values {:fullname  ""
                             :sex       ""
                             :address   ""
                             :insurance ""
                             :birthdate ""}
        patient-state (reagent/atom initial-form-values)]
    (fn []
      [:tr
       (patient-form-fields patient-state)
       [:td [:button.btn.btn-primary.pull-right
             {:name "add"
              :on-click  (fn []
                           (re-frame/dispatch [::events/add-patient-http @patient-state])
                           (reset! patient-state initial-form-values))}
             "Add"]]])))

(defn patient-input [{:keys [title on-save type]}]
  (let [val  (reagent/atom title)
        save #(let [v (-> @val str str/trim)]
                (on-save v))]
    (fn [props]
      [:input (merge (dissoc props :on-save :title)
                     {:type        type
                      :value       @val
                      :auto-focus  true
                      :on-blur     save
                      :on-change   #(reset! val (-> % .-target .-value))
                      :on-key-down #(case (.-which %)
                                      13 (save)
                                      nil)})])))

(defn patient-item
  []
  (let [editing? (reagent/atom false)]
    (fn [{:keys [id fullname sex address insurance birthdate]}]
      (if @editing?
        [:tr
         [:td [patient-input {:title fullname
                        :on-save #(re-frame/dispatch
                                   [::events/update-patient-field id :fullname %])
                        :type "text"}]]
         [:td [patient-input {:title sex
                        :on-save #(re-frame/dispatch
                                   [::events/update-patient-field id :sex %])
                        :type "text"}]]
         [:td [patient-input {:title address
                        :on-save #(re-frame/dispatch
                                   [::events/update-patient-field id :address %])
                        :type "text"}]]
         [:td [patient-input {:title insurance
                        :on-save #(re-frame/dispatch
                                   [::events/update-patient-field id :insurance %])
                        :type "text"}]]
         [:td [patient-input {:title birthdate
                        :on-save #(re-frame/dispatch
                                   [::events/update-patient-field id :birthdate %])
                        :type "date"}]]
         [:td [:button.btn.btn-primary.pull-right
               {:name "save"
                :on-click #(do (reset! editing? false)
                               (re-frame/dispatch
                                [::events/send-patient-update id]))}
               "Save"]]]
        [:tr
         [:td fullname]
         [:td sex]
         [:td address]
         [:td insurance]
         [:td birthdate]
         [:td [:button.btn.btn-primary.pull-right
               {:name "edit"
                :on-click #(reset! editing? true)}
               "Edit"]]
         [:td [:button.btn.pull-right.btn-danger
               {:on-click #(re-frame/dispatch
                            [::events/delete-patient-http id])
                :name "delete"}
               "\u00D7"]]]))))

(defn patients-list
  []
  (let [patients @(re-frame/subscribe [::subs/patients])]
    [:table.table.table-striped.col-md-6
     [:thead
      [:tr
       [:th "Name"]
       [:th "Sex"]
       [:th "Address"]
       [:th "Insurance #"]
       [:th "Birthdate"]]]
     [:tbody
      (for [p patients]
        ^{:key (str "row-" (p 0))}
        [patient-item (p 1)])
      [patient-form]]]))


(defn main-panel []
  (let [patients (re-frame/subscribe [::subs/patients])]
    [:div.col-md-6
     [patients-list]]))
