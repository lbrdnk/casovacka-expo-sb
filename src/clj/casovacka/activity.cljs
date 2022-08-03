(ns casovacka.activity
  (:require [casovacka.util :as util]))

(defn duration [activity]
  (let [last-interval-id (-> activity :intervals :order last)
        last-interval-when-sec (get-in activity [:intervals :data last-interval-id :when])]
    last-interval-when-sec))

(defn total-duration [activity]
  (let [duration-no-repeat (duration activity)]
    (* (or (:repeat activity) 1) duration-no-repeat)))

(defn total-duration-ms [activity]
  (* 1000 (total-duration activity)))

(defn intervals-count [activity]
  (-> activity :intervals :data count))

(comment
  (def some-activity (-> @re-frame.db/app-db :timers vals first :activities :data vals first))

  #_(defn precompute-intervals [activity]
      (let [order (get-in activity [:intervals :order])
            intervals-with-duration (-> (reduce (fn [acc interval])
                                                {}
                                                (get-in activity [:intervals :data])))]
        (reduce (fn [acc-precomputed interval-id]
                  (let [interval (get-in activity [:intervals :data interval-id])
                        interval-duration
                        last-acc-time (-> acc-precomputed last first)]))
                (sorted-map)
                order)))

  #_(defn precompute-intervals [activity]

      (reduce (fn [a b] nil) (sorted-map) (range 4))
      (reduce)

      (let [order (get-in activity [:intervals :order])
            duration-no-rep (duration activity)
            acc1 (map (fn [interval-id]
                        (let [interval (get-in activity [:intervals :data interval-id])]
                          [(:when interval) interval]))
                      order)
            acc2 (reduce (fn [acc repeat-round]
                           (into acc))
                         (sorted-map)
                         (range (:repeat activity)))])




    ;; mapped [ [when interval] [when interval] ]
      (def a (-> @re-frame.db/app-db :timers vals first :activities :data vals first))
      (def x (map #(vector (:when %) %) (-> @re-frame.db/app-db :timers vals first :activities :data vals first :intervals :data vals)))
    ;; duplicate
      (reduce [] (range 4))

      (for [coef (range 4)
            item x]
        [(+ (* (duration a) coef) (first item)) (second item)])

      (total-duration a))

  (def a (-> @re-frame.db/app-db :timers vals first :activities :data vals first))

  (defn precompute-intervals [activity]
    (for [dur*round (map #(* (duration activity) %) (range (:repeat activity)))
          interval (-> activity :intervals :data vals)]
      [(+ dur*round (:when interval)) interval]))

  (def s (-> (precompute-intervals a) flatten (->> (apply sorted-map))))
  (-> (precompute-intervals a) flatten (->> (apply sorted-map)) sorted?)

  (second s)

  (last s)

  )



(defn sorted-intervals [activity]
  (let [order (-> activity :intervals :order)
        intervals (-> activity :intervals :data)]
    (util/order-map->sorted-vec intervals order)))

(comment
  (def some-activity (-> @re-frame.db/app-db :timers vals first :activities :data vals first))
  (sorted-intervals some-activity)
  )

;; for activities intervals, 
;; creates sorted map of 
;; {<ending interval in miliseconds>
;;  {:activity-id
;;   :interval-id
;;   :signal
;;  }}
(defn sorted-signal-map [activity]
  (let [activity-id (:id activity)]
    (-> activity


        sorted-intervals

      ;; ! multiply list by repeat
      ;; ((fn [interval-list]
      ;;    (reduce ())))

      ;; update when to millis
      ;;((fn [intervals-list] (map #(update % :when * 1000) intervals-list)))

      ;; add activity id to interval structure
        ((fn [intervals-list]
           (into [] (map (fn [interval] (assoc interval :activity-id activity-id)) 
                         intervals-list))))

      ;; add key to list
        ((fn [intervals-list]
           (mapcat (fn [interval] [(:when interval) interval]) intervals-list)))

      ;; update keys to millis
        ((fn [key-interval-list]
           (map-indexed (fn [index item]
                          (if (even? index)
                            (* 1000 item)
                            item))
                        key-interval-list)))

      ;; create sorted-map
        ((fn [key-interval-list] (apply sorted-map key-interval-list)))

      ;; "repeat map"
        ((fn [interval-map]
           (for [_ (range (:repeat activity))]
             interval-map)))


      ;; TODO move to utils
      ;; COPIED FROM TIMER -- reduction into single sorted map
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

  )

(comment
  (def some-activity (-> @re-frame.db/app-db :timers vals first :activities :data vals first))
  (-> (sorted-signal-map some-activity) sorted?)
  )

(defn precompute-intervals [activity]
  (for [dur*round (map #(* (duration activity) %) (range (or (:repeat activity) 1)))
        interval (-> activity :intervals :data vals)]
    [(* 1000 (+ dur*round (:when interval))) interval]))