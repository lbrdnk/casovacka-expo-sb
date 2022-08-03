(ns casovacka.interval
  (:require [casovacka.util :as util]
            [casovacka.audio :as audio]))


(defn til [interval & {:keys [as format] :or {as :sec format :int}}]
  (case as
    :h-m-s (cond-> (util/s->h-m-s (:when interval))
           (= format :string) (->> (map str) (into [])))
    (cond-> (:when interval)
      (= format :string) str)))

(comment
  (def i1 (-> @re-frame.db/app-db :timers vals first :activities :data vals first :intervals :data first second))
  i1
  (til i1 :as :h-m-s)
  (til i1 :as :h-m-s :format :string)

  )

(defn signal [interval]
  (if (:signal interval)
    (:signal interval)
    audio/default-signal
    ))