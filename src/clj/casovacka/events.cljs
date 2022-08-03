(ns casovacka.events
  (:require [re-frame.core :as rf]
            ;; [casovacka.timer :as timer]
            [casovacka.db :as db]
            [casovacka.util :as util]
            [casovacka.timer :as timer]
            [casovacka.audio :as audio]))

;;
;; init
;;

(rf/reg-event-db
 :init
 (fn [_ _]
   db/initial-db))

;;
;; timer
;;

;; (rf/reg-event-db
;;  :timer/started
;;  (fn [db [_ started-timer]]
;;    (assoc db :timer started-timer)))

;; (rf/reg-event-db
;;  :timer/stopped
;;  (fn [db [_ stopped-timer]]
;;    (assoc db :timer stopped-timer)))

;; (rf/reg-event-db
;;  :timer/tick
;;  (fn [db _]
;;    (update db :timer timer/tick)))

;; (rf/reg-fx
;;  :timer/start
;;  (fn [timer-to-start]
;;    ;; TODO add option to set action on start
;;    (rf/dispatch [:timer/started (timer/start! timer-to-start)])))

;; (rf/reg-fx
;;  :timer/stop
;;  (fn [timer-to-stop]
;;    (rf/dispatch [:timer/stopped (timer/stop! timer-to-stop)])))

;;
;; view.timer
;;

;; (rf/reg-event-fx
;;  :view.timer/start-pressed
;;  (fn [{:keys [db]} _]
;;    {:fx [[:timer/start (:timer db)]]}))

;; (rf/reg-event-fx
;;  :view.timer/stop-pressed
;;  (fn [{:keys [db]} _]
;;    {:fx [[:timer/stop (:timer db)]]}))

;; (rf/reg-event-db
;;  :view.timer/reset-pressed
;;  (fn [db _]
;;    (update db :timer timer/reset)))










(rf/reg-cofx
 :now-epoch
 (fn [cofx _]
   (assoc cofx :now-epoch (. js/Date now))))

;; (rf/reg-event-fx
;;  :timer.start/)

;; point is to have db updated before first tick
(rf/reg-fx
 :timer/start
 (fn [timer]
   (let [timer-id (:id timer)
         tick-interval 64
         start-epoch (. js/Date now)
         tick-interval-id (js/setInterval #(rf/dispatch [:timer/tick timer-id]) tick-interval)]
     ;; using dispatch, not dispatch-sync hence first tick will be fired
     ;; after 64ms and I assume started dispatch and handling will happen
     ;; earlier.
     (rf/dispatch [:timer/started timer-id start-epoch tick-interval-id]))))

(rf/reg-event-fx
 :timer/started
 (fn [{:keys [db]} [_ timer-id start-epoch tick-interval-id]]
   (let [timer-to-start (get-in db [:timers timer-id])
         precomp-intervals (timer/precompute-intervals timer-to-start)
         ---tmp-timer-precomp-intervals (assoc timer-to-start :ms->interval-end precomp-intervals)
         first-interval-id (-> precomp-intervals first second)]
     {:db (update-in db [:timers timer-id] assoc
                     :start-epoch start-epoch
                     :now-epoch start-epoch ;; so read before first tick produces 0, not negative
                     :js-tick-interval-id tick-interval-id
                   ;; signalling support
                     :last-tick-interval-id (timer/current-interval-id ---tmp-timer-precomp-intervals) 
                     #_first-interval-id ;; WHEN
                     :ms->interval-end precomp-intervals
                     :cleared false)})))

(comment
  (def selected-timer-id (-> @re-frame.db/app-db :view.stopwatch-tab/selected-timer-id))
  (-> (get-in @re-frame.db/app-db [:timers selected-timer-id]) (select-keys [:ms->interval-end :last-tick-interval-id]))
  (def sel-timer (get-in @re-frame.db/app-db [:timers selected-timer-id]))
  (timer/current-interval-id sel-timer)
  (-> sel-timer :ms->interval-end (subseq >= (timer/passed-time-ms sel-timer)) first second :id)
  )

(rf/reg-fx
 :play-audio
 (fn [audio-id]
   (audio/play-audio-now audio-id)
   (prn "PLAY")
   
   ))

(rf/reg-event-fx
 :timer/tick
 [(rf/inject-cofx :now-epoch)]
 (fn [{:keys [db now-epoch]} [_ timer-id]]
   (let [examined-timer (get-in db [:timers timer-id])]
     (doto (cond->

            {:db (update-in db [:timers timer-id] assoc
                            :now-epoch now-epoch
                            :last-tick-interval-id (timer/current-interval-id examined-timer))}

     ;; currently "default sound"
             (timer/should-signal? examined-timer)
             (merge {:play-audio (timer/prev-interval-signal examined-timer) #_(-> db :sounds :confirmation :sound)}))
       ((fn [efe] (when (contains?  efe :play-audio) (:play-audio efe))))))))

(comment

  (def test-timer-id (-> @re-frame.db/app-db :view.stopwatch-tab/selected-timer-id))
  (def test-timer (get-in @re-frame.db/app-db [:timers test-timer-id]))
  
  (audio/play-audio-now (timer/current-interval-signal test-timer))

  (timer/prev-interval-signal test-timer)

  (timer/prev-interval test-timer)

  (test-timer)
  )


(rf/reg-fx
 :clear-interval
 (fn [interval-id]
   (js/clearInterval interval-id)))

(rf/reg-event-fx
 :view.stopwatch-tab/start-timer-pressed
 (fn [{:keys [db]} _]
   (let [selected-timer-id (:view.stopwatch-tab/selected-timer-id db)
         selected-timer (get-in db [:timers selected-timer-id])]
     {:timer/start selected-timer})))

(rf/reg-event-fx
 :view.stopwatch-tab/stop-timer-pressed
 (fn [{:keys [db]} _]
   (let [selected-timer-id (:view.stopwatch-tab/selected-timer-id db)
         selected-timer (get-in db [:timers selected-timer-id])
         tick-interval-id (:js-tick-interval-id selected-timer)]
     {:db (db/stop-timer db selected-timer-id)
      :clear-interval tick-interval-id})))


(rf/reg-event-fx
 :view.stopwatch-tab/timer-list-item-pressed
 (fn [{:keys [db]} [_ timer-id]]
   (let [selected-timer-id (:view.stopwatch-tab/selected-timer-id db)
         selected-timer (get-in db [:timers selected-timer-id])
         selected-timer-running? (timer/running? selected-timer)
         selected-timer-pressed? (= (:view.stopwatch-tab/selected-timer-id db)
                                    timer-id)]
     (-> {:fx []}
         ;; change selected timer id
         (update :fx conj
                 (if selected-timer-pressed?
                   [:db (dissoc db :view.stopwatch-tab/selected-timer-id)]
                   [:db (assoc db :view.stopwatch-tab/selected-timer-id timer-id)]))
         ;; stop timer, 1. update db, 2. clear interval
         (cond->
          selected-timer-running?
           (-> (update-in [:fx 0 1] db/stop-timer selected-timer-id) ;; stop 
               (update :fx conj
                       [:clear-interval (:js-tick-interval-id selected-timer)])))))))

;; disabled when running?
;; reset passed-ms
;;
;; currently expecting timer to be stopped
(rf/reg-event-db
 :view.stopwatch-tab/reset-timer-pressed
 (fn [db _]
   (let [selected-timer-id (:view.stopwatch-tab/selected-timer-id db)]
     (update-in db [:timers selected-timer-id] dissoc :passed-ms))))








;;
;; view.timers-screen
;;

(rf/reg-fx
 :navigate
 (fn [[js-navigation route-name]]
   (cond
     (string? route-name)
     (.navigate js-navigation route-name)
     
     (= :back route-name)
     (.goBack js-navigation))))

(rf/reg-cofx
 :random-uuid
 (fn [cofx _]
   (assoc cofx :random-uuid (random-uuid))))

(rf/reg-event-fx
 :view.timers-screen.new-timer-button/pressed
 [(rf/inject-cofx :random-uuid)]
 (fn [{db :db
       random-uuid :random-uuid}
      [_ js-navigation]]
   {:db (assoc db :view.edit-timer-screen/timer {:id random-uuid})
    :fx [[:navigate [js-navigation "NewTimerScreen"]]]}))

;; WIP 2
(rf/reg-event-fx
 :view.timers-screen.timers-list-element/pressed
 (fn [{db :db} [_ js-navigation timer-id]]
   {:db (assoc db :view.edit-timer-screen/timer (get-in db [:timers timer-id]))
    :fx [[:navigate [js-navigation "EditTimerScreen"]]]}))

;; OK ?
(rf/reg-event-db
 :view.edit-timer-screen/name-changed
 (fn [db [_ new-name]]
   (assoc-in db [:view.edit-timer-screen/timer :name] new-name)))

;;
;; view.edit-timer-screen/...
;;

(rf/reg-event-fx
 :view.edit-timer-screen/create-pressed
 (fn [{:keys [db]} [_ js-navigation]]
   (let [timer--new (:view.edit-timer-screen/timer db)
         timer-id (:id timer--new)]
     {:db (assoc-in db [:timers timer-id] timer--new)
      :fx [[:navigate [js-navigation :back]]]})))

;; DUPLICIT to :view.edit-timer-screen/create-pressed
(rf/reg-event-fx
 :view.edit-timer-screen/edit-pressed
 (fn [{:keys [db]} [_ js-navigation]]
   (let [timer--updated (-> db :view.edit-timer-screen/timer)
         timer-id (:id timer--updated)]
     {:db (assoc-in db [:timers timer-id] timer--updated)
      :fx [[:navigate [js-navigation :back]]]})))

(rf/reg-event-fx
 :view.edit-timer-screen/delete-pressed
 (fn [{:keys [db]} [_ js-navigation]]
   {:db (update db :timers dissoc (-> db :view.edit-timer-screen/timer :id))
    :fx [[:navigate [js-navigation "TimersRoot"]]]}))

(rf/reg-event-fx
 :view.edit-timer-screen/add-activity-pressed
 [(rf/inject-cofx :random-uuid)]
 (fn [{:keys [db random-uuid]} [_ js-navigation]]
   {:db (assoc db :view.edit-activity-screen/activity {:id random-uuid})
    :fx [[:navigate [js-navigation "NewActivityScreen"]]]}))

;; WIP 10 
(rf/reg-event-fx
 :view.edit-timer-screen/activity-pressed
 (fn [{:keys [db]} [_ js-navigation activity-id]]
   {:db (assoc db
               :view.edit-activity-screen/activity
               (get-in db [:view.edit-timer-screen/timer :activities :data activity-id]))
    :fx [[:navigate [js-navigation "EditActivityScreen"]]]}))

;;
;; view.edit-activity-screen/...
;;

(rf/reg-event-db
 :view.edit-activity-screen/label-changed
 (fn [db [_ new-label]]
   (assoc-in db [:view.edit-activity-screen/activity :label] new-label)))

(rf/reg-event-fx
 :view.edit-activity-screen/edit-pressed
 (fn [{:keys [db]} [_ js-navigation]]
   (let [activity--edited (-> db :view.edit-activity-screen/activity)
         activity-id (:id activity--edited)]
     {:db (assoc-in db [:view.edit-timer-screen/timer :activities :data activity-id] activity--edited)
      :fx [[:navigate [js-navigation :back]]]})))

(rf/reg-event-fx
 :view.edit-activity-screen/create-pressed
 (fn [{:keys [db]} [_ js-navigation]]
   (let [activity--edited (:view.edit-activity-screen/activity db)
         activity-id (:id activity--edited)]
     {:db (-> db
              (assoc-in [:view.edit-timer-screen/timer :activities :data activity-id] activity--edited)
              (update-in [:view.edit-timer-screen/timer :activities :order] concat [activity-id]))
      :fx [[:navigate [js-navigation :back]]]})))

(rf/reg-event-fx
 :view.edit-activity-screen/delete-pressed
 (fn [{:keys [db]} [_ js-navigation]]
   (let [activity-id (-> db :view.edit-activity-screen/activity :id)
         timer-id (-> db :view.edit-timer-screen/timer :id)]
     {:db (-> db
              ;; delete from (:view.edit-timer-screen/timer db)
              (update-in [:view.edit-timer-screen/timer :activities :data] dissoc activity-id)
              (update-in [:view.edit-timer-screen/timer :activities :order] remove #(= activity-id %))
              ;; delete from (:timers db)
              ;;  (update-in [:timers timer-id :activities :data] dissoc activity-id)
              ;;  (update-in [:timers timer-id :activities :order] remove #(= activity-id %))
              )
      :fx [[:navigate [js-navigation :back]]]})))


;; !!!!!!!!!!!!! TU
;; OK ?
;; contains effect -- uuid generation
(defn init-interval-modal [db]
  (-> db
      (assoc :view.edit-interval-modal/id (random-uuid))
      (dissoc :view.edit-interval-modal/label
              :view.edit-interval-modal/when
              :view.edit-interval-modal/repeat)))

;; WIP 14
(rf/reg-event-fx
 :view.edit-activity-screen/add-interval-pressed
 [(rf/inject-cofx :random-uuid)]
 (fn [{:keys [db random-uuid]} _]
   {:db (-> db
            (assoc :view.edit-interval-modal/interval {:id random-uuid})
            (assoc :view.edit-activity-screen/modal-open true))}))

;; OK 15 ?
(rf/reg-event-db
 :view.edit-activity-screen/modal-cancel-pressed
 (fn [db _]
   (assoc db :view.edit-activity-screen/modal-open false)))

;; fuj ? OK ?
(defn append-when-not-present [seq1 elm]
  (if (contains? (set seq1) elm)
    (seq seq1)
    (seq (conj (into [] seq1) elm))))


;; WIP 16
(rf/reg-event-db
 :view.edit-activity-screen/modal-save-pressed
 (fn [db _]
   (let [interval--after-edit (util/submap-ns db :view.edit-interval-modal :strip true)
         interval-id (:id interval--after-edit)]
     (-> db

         ;; 1 close modal
         (assoc :view.edit-activity-screen/modal-open false)

         ;; 2.1 copy interval data into activity -- order and 
         (assoc-in [:view.edit-activity-screen/intervals interval-id] interval--after-edit)
         ;; 2.2 update order
         (update-in [:view.edit-activity-screen/intervals :order]
                    append-when-not-present
                    interval-id)))))


(rf/reg-event-db
 :view.edit-activity-screen/interval-list-element-pressed
 (fn [db [_ interval-id]]
   (let [interval--to-edit
         (get-in db [:view.edit-activity-screen/activity :intervals :data interval-id])

        ;;  interval--updated-keys (into {} (for [[k v] interval--to-edit]
        ;;                                    [(keyword :view.edit-interval-modal k) v]))
         ]
     (-> db
        ;;  (merge interval--updated-keys)
         (assoc :view.edit-interval-modal/interval interval--to-edit)
         (assoc :view.edit-activity-screen/modal-open true)))))

(rf/reg-event-db
 :view.edit-interval-modal/label-text-changed
 (fn [db [_ new-label]]
   (assoc db :view.edit-interval-modal/label new-label)))

(rf/reg-event-db
 :view.edit-interval-modal/when-changed
 (fn [db [_ when--]]
   (assoc db :view.edit-interval-modal/when when--)))

(rf/reg-event-db
 :set-active-color
 (fn [db [_ color]]
   (assoc db :color color)))