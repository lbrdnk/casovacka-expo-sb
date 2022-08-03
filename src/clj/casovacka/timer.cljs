(ns casovacka.timer
  (:require [casovacka.util :as util]
            [casovacka.activity :as activity]
            [casovacka.interval :as interval]
            [goog.string :refer [format]]))

;; (defprotocol ITimer
;;   (start! [this])
;;   (stop! [this])
;;   (tick [this])
;;   (reset [this]))

;; (defrecord Timer [interval-ms action])

;; (extend-protocol ITimer

;;   Timer

;;   (start!
;;     [this]
;;     {:pre (-> this :action nil? not)}
;;     (prn "timer start")
;;     (let [start-time (.now js/Date)
;;           interval-id (js/setInterval (:action this) (:interval-ms this))]
;;       (assoc this
;;              :interval-id interval-id
;;              :start-time start-time
;;              :running? true)))

;;   ;; tick!
;;   (tick
;;     [this]
;;     (assoc this :delta (- (.now js/Date) (:start-time this))))

;;   (stop!
;;     [this]
;;     {:pre (-> this :interval-id nil? not)}
;;     (prn "timer stop")
;;     (js/clearInterval (:interval-id this))
;;     (-> this
;;         (update :acc + (:delta this))
;;         (dissoc :interval-id :running? :delta)))

;;   (reset
;;     [this]
;;     (-> this
;;         (dissoc :acc))))




;; how to add side effect


;;
;; functions on timer structure
;;

(defn total-time [timer & {:keys [format as] :or {format :int as :sec}}]
  (let [total-time-sec (reduce #(+ %1 (activity/total-duration %2))
                               0
                               (-> timer :activities :data vals))
        total-time-converted (case as
                               :h-m-s (util/s->h-m-s total-time-sec)
                               :sec total-time-sec)
        total-time-formatted (case format
                               :string (cond (seqable? total-time-converted)
                                             (->> total-time-converted (map str) (into [])))
                               :int total-time-converted)]
    total-time-formatted))

(defn total-time-ms [timer]
  (reduce #(+ %1 (activity/total-duration-ms %2))
          0
          (-> timer :activities :data vals)))

(defn passed-time-ms [timer]
  (+ (:passed-ms timer)
     (- (:now-epoch timer)
        (:start-epoch timer))))

(defn running? [timer]
  (-> timer :js-tick-interval-id boolean))


(declare sorted-signal-map)
;; THIS IS USED !!!!!!!!!! instaed of sorted-signal map
(defn precompute-intervals [timer]
  (sorted-signal-map timer)
  #_(let [precomputed-activities
          (for [activity-id (-> timer :activities :order)]
            (activity/precompute-intervals
             (get-in timer [:activities :data activity-id])))
          to-add (map #(-> % last first) precomputed-activities)
          to-add-normalized (apply vector 0 (butlast to-add))
          to-add-normalized-boha-jeho (reduce (fn [acc val]
                                                (conj (into [] acc)
                                                      (+ val (last acc))))
                                              [0]
                                              (drop 1 to-add-normalized))
          finale-1 (map (fn [to-add dur-act-tuples]
                          (map #(vector (+ (first %) to-add) (second %)) dur-act-tuples))
                        #_(doto to-add-normalized prn)
                        to-add-normalized-boha-jeho
                        precomputed-activities)]
      (-> finale-1
          flatten
          #_(->> (into (sorted-map)))
          (->> (apply sorted-map)))))

(comment
  (def some-timer (-> @re-frame.db/app-db :timers vals first))
  (def tmp (precompute-intervals some-timer))
  tmp
  (-> tmp keys)
  (apply sorted-map tmp)
  ;; (->>  vector 0 (butlast tmp))
  )



;; !!! EXPECTING ms->interval set
(defn current-interval-id [timer]
  (-> timer :ms->interval-end (subseq >= (passed-time-ms timer)) first second :id))

(defn should-signal? [timer]
  ;; 1 check interval timer is in
  ;; check if already signalled
  ;; when not, check if it is right time to signal
  ;;             - meaning total + 
  #_false
  (boolean (not (= (:last-tick-interval-id timer) (current-interval-id timer)))))

(defn sorted-activities [timer]
  (util/order-map->sorted-vec (-> timer :activities :data) (-> timer :activities :order)))



(defn sorted-signal-map [timer]
  (-> timer

      sorted-activities

   ;; key-interval
      ((fn [activities]
         (map activity/sorted-signal-map activities)))


      ;; update keys -- nested reduce?
      ((fn [interval-maps]

         (reduce

          (fn [acc-interval-map interval-map]

            (let [accumulated-ms-shift (-> acc-interval-map last first)]

              (reduce-kv (fn [acc k v]
                           (assoc acc (+ accumulated-ms-shift k) v))
                         acc-interval-map
                         interval-map)))

          (first interval-maps)
          (rest interval-maps))))))

(defn current-interval [timer]
  (-> timer :ms->interval-end (subseq >= (passed-time-ms timer)) first second))


(defn current-interval-signal [timer]
  (-> timer current-interval interval/signal))

(defn prev-interval [timer]
  (-> timer :ms->interval-end (subseq < (passed-time-ms timer)) last second))

(comment

  (def selected-timer-id (-> @re-frame.db/app-db :view.stopwatch-tab/selected-timer-id))
  (def selected-timer (get-in @re-frame.db/app-db [:timers selected-timer-id]))

  selected-timer

  (prev-interval selected-timer)

  (passed-time-ms selected-timer)
  (-> selected-timer :ms->interval-end (subseq < (passed-time-ms selected-timer)) last second))

(defn prev-interval-signal [timer]
  (-> timer prev-interval interval/signal))

(comment

  (def some-timer (-> @re-frame.db/app-db :timers vals first))

  (sorted-activities some-timer)

  (sorted? (sorted-signal-map some-timer))

  (current-interval-signal some-timer))


;; asi ok
;; TODO daj prec running?
(defn cleared? [timer]
  (and (not (running? timer))
       (= 0 (passed-time-ms timer))))

(defn time-left-ms [timer]
  (- (total-time-ms timer)
     (passed-time-ms timer)))

(defn current-activity [timer]
  (let [{:keys [activity-id]} (current-interval timer)]
    (get-in timer [:activities :data activity-id])))

(defn current-activity-time-left-ms [timer]
  (if (contains? timer :ms->interval-end)
    (let [current-activity-end-ms (-> (partition-by
                                       (fn [[k v]] (:activity-id v))
                                       (-> timer :ms->interval-end (subseq >= (passed-time-ms timer))))
                                     ;; first partition -> last element -> end interval time ms
                                      first last first)]
      current-activity-end-ms
      (- current-activity-end-ms
         (passed-time-ms timer)))
    nil))

(defn current-activity-time-left-str [timer]
  (-> timer
      current-activity-time-left-ms
      util/ms->h-m-s
      ;; tmp correction
      ;; ((fn [[h m s]]
      ;;    [h m (+ 1 s)]))
      util/h-m-s->str))

(comment
  (def timer (-> @re-frame.db/app-db :timers vals first))
  (current-activity-time-left-str timer)
  (passed-time-ms timer)
  (def signal-map (-> @re-frame.db/app-db :timers vals first :ms->interval-end))
  signal-map
  (def cur-act (current-activity timer))
  cur-act
  (def cur-act-id (:id cur-act))
  cur-act-id
  (filter (fn [[k v]] (prn k)
            (prn v)
            (= cur-act-id (:activity-id v))) signal-map)


  ;; cur interval key to sorted map
  (-> timer :ms->interval-end (subseq >= (passed-time-ms timer)) first)


  (reduce
   (fn [acc [k v]])
   ;; activity-id acc
   (-> timer :ms->interval-end (subseq >= (passed-time-ms timer)) first second :activity-id)
   ;; rest of intervals
   (-> timer :ms->interval-end (subseq >= (passed-time-ms timer))))



  (-> (partition-by (fn [[k v]] (:activity-id v)) (-> timer :ms->interval-end (subseq >= (passed-time-ms timer))))
      last first)

  )

(defn current-interval-end-ms [timer]
  
  (-> timer :ms->interval-end (subseq >= (passed-time-ms timer)
                                      ;; first [end-time interval] tuple -> end-time
                                      )
      first first))

(defn current-interval-time-left-str [timer]
  (if (contains? timer :ms->interval-end)
   (-> (- (current-interval-end-ms timer)
          (passed-time-ms timer))
       util/ms->h-m-s
       util/h-m-s->str)
    nil))