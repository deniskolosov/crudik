(ns cljs-src.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [clojure.string :as str]
            [goog.string :as gstring]
            [goog.string.format]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(enable-console-print!)

(println "This text is printed from src/cljs-src/core.cljs. Go ahead and edit it and see reloading in action.")

(def patients-atom (r/atom ()))


(defonce patients (r/atom (sorted-map)))



(defonce counter (r/atom 0))

(defn add-patient [patient]
  (let [id (:id patient)]
    (println "Adding patient" patient)
    (swap! patients assoc id {:id id
                           :fullname (:fullname patient)
                           :address (:address patient)
                           :sex (:sex patient)
                           :insurance (:insurance patient)
                           :birthdate (:birthdate patient)})))


(defn get-patients []
  (go (let [response (<! (http/get "http://localhost:3000/patients" {:with-credentials? false}))]
   (doseq [ x (:body response) ] (add-patient { :id (:id x) :fullname (:fullname x)
    :sex (:sex x) :address (:address x) :insurance (:insurance x) :birthdate (:birthdate x)}))
  )))


(defn save [id fullname]

 (swap! patients assoc-in [id :fullname] fullname))
(defn delete [id] (swap! patients dissoc id))

(defn mmap [m f a] (->> m (f a) (into (empty m))))

(defonce init (do 
                (get-patients)))

(defn patient-input [{:keys [fullname on-save on-stop]}]
  (let [val (r/atom fullname)
        stop #(do (reset! val "")
                  (if on-stop (on-stop)))
        save #(let [v (-> @val str str/trim)]
                (if-not (empty? v) (on-save v))
                (stop))]
    (fn [{:keys [id class placeholder]}]
      [:input {:type "text" :value @val
               :id id :class class :placeholder placeholder
               :on-blur save
               :on-change #(reset! val (-> % .-target .-value))
               :on-key-down #(case (.-which %)
                               13 (save)
                               27 (stop)
                               nil)}])))

(def patient-edit (with-meta patient-input
                 {:component-did-mount #(.focus (rdom/dom-node %))}))


(defn patient-item []
  (let [editing (r/atom false)]
    (fn [{:keys [id fullname]}]
      [:li {:class (str (if @editing "editing"))}
       [:div.view
        [:label {:on-double-click #(reset! editing true)} fullname]
        [:button.destroy {:on-click #(delete id)}]]
       (when @editing
         [patient-edit {:class "edit" :title fullname
                     :on-save #(save id %)
                     :on-stop #(reset! editing false)}])])))

(defn patients-app [props]
  (let [filt (r/atom :all)]
    (fn []
      (let [items (vals @patients)
            done (->> items (filter :done) count)
            active (- (count items) done)]
        [:div
         [:section#todoapp
          [:header#header
           [:h1 "patients"]
           [patient-input {:id "new-todo"
                          :placeholder "Add patient"
                          :on-save add-patient}]]
          (when (-> items count pos?)
            [:div
             [:section#main
              [:ul#patient-list
               (for [patient (filter (case @filt
                                    :all identity) items)]
                 ^{:key (:id patient)} [patient-item patient])]]
             ])]
         [:footer#info
          [:p "Double-click to edit a patient"]]]))))

(defn start []
(rdom/render [patients-app]                   
  (js/document.getElementById "app")))
(start)