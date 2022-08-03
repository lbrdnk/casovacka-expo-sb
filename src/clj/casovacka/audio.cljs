(ns casovacka.audio
  (:require ["expo-av" :as av]))

(def confirmation-sound (js/require "../../assets/mixkit-confirmation-tone-2867.wav"))
(def countdown-sound (js/require "../../assets/mixkit-sport-start-bleeps-918.wav"))

(def sounds {:confirmation {:name "Confirmation"
                            :sound confirmation-sound}
             :countdown {:name "Countdown"
                         :sound countdown-sound}})

(def default-signal confirmation-sound)

(def test-another-signal countdown-sound)

(defn play-audio-now [audio]
  ((.. av/Audio -Sound -createAsync) audio #js {"shouldPlay" true}))


(comment
  ;; play sound
  (-> ((.. av/Audio -Sound -createAsync) countdown-sound)
      (.then #(.. % -sound playAsync))
      (.catch #(js/alert "play failed")))

  ((.. av/Audio -Sound -createAsync) countdown-sound #js {"shouldPlay" true})


  )