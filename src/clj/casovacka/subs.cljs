(ns casovacka.subs
  (:require [re-frame.core :as rf]

            ;; !! temporary ??
            ;; [casovacka.view.stopwatch-tab :refer [start-button stop-button]]
            [casovacka.util :as util]
            [goog.string :refer [format]]

            [casovacka.timer :as timer]))

;; timer

(rf/reg-sub
 :timer/str
 (fn [db _]
   (let [{:keys [delta acc]} (:timer db)
         [_ m s ms] (util/ms->h-m-s-ms (+ acc delta))]
     (format "%02d:%02d.%03d" m s ms))))

;; (rf/reg-sub
;;  :view.timer/primary-button
;;  (fn [db _]
;;    (if (-> db :timer :running? not)
;;      start-button
;;      stop-button)))

(rf/reg-sub
 :view.timer/reset-disabled?
 (fn [db _]
   (let [{:keys [acc delta running?]} (:timer db)]
     (or running?
         (= 0 (+ acc delta))))))

;;
;; timers screen
;;

;; obsolete
(rf/reg-sub
 :view.timers-screen/timers
 (fn [db _]
   (or (:timers db) [])))

(rf/reg-sub
 :view.timers-screen/timers-list-data
 (fn [db _]
   ;; title and id for timer
   (or (->> (:timers db)
            (map (fn [[_key val]] (select-keys val [:id :name])))
            (map (fn [item] (assoc item :key (:id item)))))
       [])))

;;
;; activities
;;

;; (rf/reg-sub
;;  :view.new-timer/activity-ids
;;  (fn [db [_subscription timer-id]]
;;    (prn (str timer-id))
;;    (let [timers (:timers db)

;;          ;; TODO assert unique timer
;;          timer (first (filter #(= (:id %) timer-id) timers))]

;;      (map #(:id %) (:activities timer)))))

;; (rf/reg-sub
;;  :view.new-timer/activity
;;  (fn [db [_ timer-id activity-id]]
;;    (-> db
;;        :timers
;;        (get-by-id timer-id)
;;        :activities
;;        (get-by-id activity-id))))

;; ZNOVA

;;
;; edit-timer-screen
;;

(rf/reg-sub
 :view.edit-timer-screen/activity
 (fn [db [_ activity-id]]
   (get-in db [:view.edit-timer-screen/timer :activities :data activity-id])))

(rf/reg-sub
 :view.edit-timer-screen/name
 (fn [db _]
   (get-in db [:view.edit-timer-screen/timer :name])))

;; ordered, ids only
(rf/reg-sub
 :view.edit-timer-screen/activities-ids
 (fn [db _]
   (get-in db [:view.edit-timer-screen/timer :activities :order])))

(rf/reg-sub
 :view.edit-activity-screen/all
 (fn [db _]
   (-> (get db :view.edit-activity-screen/activity)
       (update :intervals #(-> % :data vals)))))

(rf/reg-sub
 :view.edit-activity-screen/modal-open
 (fn [db _]
   (or (:view.edit-activity-screen/modal-open db) false)))

(rf/reg-sub
 :view.edit-interval-modal/all
 (fn [db _]
   (get-in db [:view.edit-interval-modal/interval])))

;; select only ids and names
(rf/reg-sub
 :view.stopwatch-tab/selector-list-data
 (fn [db _]
   (->> db :timers vals (map #(select-keys % [:id :name])))))

;;
;; asdf
;;

#_(rf/reg-sub
   :timers/total
   (fn [db [_ timer-id]]
     (-> (timer/total-time-sec (get-in db [:timers timer-id])) util/s->h-m-s util/h-m-s->str)))

(rf/reg-sub
 :timers/total-h-m-s
 (fn [db [_ timer-id]]
   (-> (get-in db [:timers timer-id])
       (timer/total-time :as :h-m-s :format :string)
       util/h-m-s->str)))

(rf/reg-sub
 :timers/activities-count
 (fn [db [_ timer-id]]
   (-> (get-in db [:timers timer-id :activities :data]) count str)))

(rf/reg-sub
 :timer/name
 (fn [db [_ timer-id]]
   (get-in db [:timers timer-id :name])))

(rf/reg-sub
 :view.stopwatch-tab/timers-indices
 (fn [db _] (-> db :timers vals
                (->> (map :id)))))

(rf/reg-sub
 :view.stopwatch-tab/selected-timer-id
 (fn [db _]
   (:view.stopwatch-tab/selected-timer-id db)))

(rf/reg-sub
 :view.stopwatch-tab/selected-timer-running?
 (fn [db _]
   (let [selected-timer-id (:view.stopwatch-tab/selected-timer-id db)
         selected-timer (get-in db [:timers selected-timer-id])]
     (not (nil? (:js-tick-interval-id selected-timer))))))

(rf/reg-sub
 :view.stopwatch-tab/selected-timer-time-str
 (fn [db _]
   (let [selected-timer-id (:view.stopwatch-tab/selected-timer-id db)
         selected-timer (get-in db [:timers selected-timer-id])
         passed-time-str (-> selected-timer
                             timer/passed-time-ms
                             util/ms->str)]
     passed-time-str)))

(rf/reg-sub
 :view.stopwatch-tab/selected-timer-running?
 (fn [db _]
   (let [selected-timer-id (:view.stopwatch-tab/selected-timer-id db)
         selected-timer (get-in db [:timers selected-timer-id])]
     (timer/running? selected-timer))))

;;
;; POST STORYBOOK
;;
(rf/reg-sub
 :view.stopwatch-screen/timers
 (fn [db _]
   (->> (-> db :timers vals)

        (map #(assoc % :cleared (timer/cleared? %)))

        (map #(assoc % "timeLeftStr"
                     (apply format "%02d:%02d:%02d"
                            (util/ms->h-m-s (timer/time-left-ms %)))))

        ;; current activity name
        (map #(if (not (:cleared %))
                (assoc % "currentActivityName" (:label (timer/current-activity %)))
                %))

        ;; current activity time left

        ;; add interval name to timers that are not cleared
        (map #(if (not (:cleared %))
                (assoc % "currentIntervalName" (:label (timer/current-interval %)))
                %))

        ;; current interval time left
        (map #(if (not (:cleared %))
                (assoc % "currentIntervalTimeLeftStr" (timer/current-interval-time-left-str %))
                %))

        ;; current activity time left
        (map #(if (not (:cleared %))
                (assoc % "currentActivityTimeLeftStr" (timer/current-activity-time-left-str %))
                %))
        
        ;; ;; color
        ;; (map )

        ;; isSelected
        (map (fn [timer]
               (let [active-timer-id (:view.stopwatch-tab/selected-timer-id db)]
                 (assoc timer "isSelected" (= active-timer-id (:id timer))))
               ))
        
        (map (fn [timer]
               (assoc timer 
                      "selectTimerFn" 
                      #(rf/dispatch [:view.stopwatch-tab/timer-list-item-pressed (:id timer)]))))
        
        (map (fn [timer]
               (assoc timer
                      "currentActivityName" (timer/current-activity-name timer)
                      "nextActivityName" (timer/next-activity-name timer)
                      "currentIntervalName" (timer/current-interval-name timer)
                      "nextIntervalName" (timer/next-interval-name timer)
                      )))

        (map (fn [timer]
               (assoc timer :running (timer/running? timer))))

        )))



;;
;; color for header
;;
(rf/reg-sub
 :active-color
 (fn [db _]
   #_(:color db)
   (let [selected-timer-id (:view.stopwatch-tab/selected-timer-id db)
         selected-timer (get-in db [:timers selected-timer-id])]
     (if selected-timer
       (:color selected-timer)
       "transparent"))))


;; TODO FINISH CICINA
(rf/reg-sub
 :view.stopwatch-screen/title
 (fn [db _]
   (let [selected-timer-id (:view.stopwatch-tab/selected-timer-id db)
         selected-timer (get-in db [:timers selected-timer-id])]
     (or (:name selected-timer) "CICINA"))))