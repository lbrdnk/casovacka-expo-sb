(ns casovacka.util
  (:require [goog.string :refer [format]]))

(defn quot&rem [num den]
  [(quot num den) (rem num den)])

(defn ms->h-m-s [ms]
  (let [[h h-rem-ms] (quot&rem ms (* 60 60 1000))
        [m m-rem-ms] (quot&rem h-rem-ms (* 60 1000))
        [s] (quot&rem m-rem-ms 1000)]
    [h m s]))

(defn ms->h-m-s-ms [ms]
  (let [[h h-rem-ms] (quot&rem ms (* 60 60 1000))
        [m m-rem-ms] (quot&rem h-rem-ms (* 60 1000))
        [s s-rem-ms] (quot&rem m-rem-ms 1000)]
    [h m s s-rem-ms]))

(defn ms->h-m-s-cs [ms]
  (let [[h h-rem-ms] (quot&rem ms (* 60 60 1000))
        [m m-rem-ms] (quot&rem h-rem-ms (* 60 1000))
        [s s-rem-ms] (quot&rem m-rem-ms 1000)
        cs (quot s-rem-ms 10)]
    [h m s cs]))

(defn h-m-s->str [[h m s]]
  (format "%02d:%02d:%02d" h m s))

;; hour
(def hour-ms 3600000)
(defn ms->str [ms]
  ;; cs = centisecond, hundredth of second
  (let [[h m s cs] (ms->h-m-s-cs ms)]
    (if (= h 0)
      (format "%02d:%02d.%02d" m s cs)
      (format "%02d:%02d:%02d" h m s))))

(defn s->h-m-s [s]
  (let [[h rem-h] (quot&rem s 3600)
        [m rem-m] (quot&rem rem-h 60)
        s rem-m]
    [h m s]))


(defn submap-ns
  ^{:doc "Extract all submap with keys qualified with keyword namespace"}
  ([m ns-kw]
   (->> m
        (filter #(= (name ns-kw) (namespace (first %))))
        (into {})))
  ([m ns-kw  & {:keys [strip] :or {strip false}}]
   (let [--after-extraction (submap-ns m ns-kw)]
     (if strip
       (->> --after-extraction
            (map (fn [[k v]] [(-> k name keyword) v]))
            (into {}))
       --after-extraction))))

(defn qualified-keys [m ns-kw]
  (->> m
       keys
       (filter (fn [k] (= (name ns-kw) (namespace k))))))

(defn vec-contains? [v pred]
  (some #(if (pred %) true false) v))

(defn swap-vec-elm [list pred new-elm]
  (->> list 
       (map #(if (pred %) new-elm %))
       (into [])))

(defn update-vec-elm [list pred update-fn]
  (->> list
       (map #(if (pred %) (update-fn %) %))
       (into [])))

(defn remove-vec-elm [list pred]
  (->> list
       (filter #(not (pred %)))
       (into [])))





(defn order-map->sorted-vec [m order]
  (reduce (fn [acc id] (conj acc (get m id))) [] order))

;;
;; misc
;;

;; write out timers to json
;; (write-all)
;; (def p (js/require "process"))
