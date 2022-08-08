(ns casovacka.view.components
  "js REQUIRES CAN NOT BE DYNAMIC according to https://github.com/facebook/react-native/issues/6391"
  (:require [goog.object]
            [re-frame.core :as rf]))

#_(defn require-component-2 [Name]
  (-> (js/require (str "../components/" Name ".tsx"))
      (goog.object.get "default")))

#_(defn require-component [Name]
  (js/require (str "../components/" Name ".tsx")))

(def Storybook (-> (js/require "../../.ondevice/Storybook.tsx")
                    .-default))


;; requires
#_(def Button (require-component "Button"))
;; (def Button (-> (str ui-dir "/components/Button/Button.tsx") js/require .-default))
#_(def Stopwatch (require-component "Stopwatch"))
;; (def Stopwatch (-> (str ui-dir "/components/Stopwatch/Stopwatch.tsx") js/require .-default))

(def StopwatchScreen (-> (js/require "../../src/ts/ui/components/StopwatchScreen/StopwatchScreen.tsx")
                         .-default))

(defn stopwatch-screen
  ([] (stopwatch-screen {}))
  ([props]
   (let [timers @(rf/subscribe [:view.stopwatch-screen/timers])
         stopwatch-time-str @(rf/subscribe [:view.stopwatch-tab/selected-timer-time-str])
         is-timer-running @(rf/subscribe [:view.stopwatch-tab/selected-timer-running?])
        ;; is-timer-cleared @(rf/subscribe [:view.stopwatch-screen/timer-cleared? timer-id])
         ]
    ;; (prn "clj rerender")
     [:> StopwatchScreen
      {:timers timers
       :stopwatch-time-str stopwatch-time-str
       :select-timer-fn (fn [timer-id] (rf/dispatch [:view.stopwatch-tab/timer-list-item-pressed timer-id])
                          (prn timer-id)
                          (prn "xiixxi"))
       :reset-timer-fn (fn [] (rf/dispatch [:view.stopwatch-tab/reset-timer-pressed]))
       :start-timer-fn (fn [] (rf/dispatch [:view.stopwatch-tab/start-timer-pressed]))
       :stop-timer-fn (fn [] (rf/dispatch [:view.stopwatch-tab/stop-timer-pressed]))
       :is-timer-running is-timer-running
       :active-color @(rf/subscribe [:active-color])
      ;; :is-timer-cleared is-timer-cleared
       }])))

(def NavigationHeader (-> (js/require "../../src/ts/ui/components/NavigationHeader/NavigationHeader.tsx")
                          .-default))
(defn navigation-header [props]
  #_nil
  (let [render-header-args (clj->js (select-keys props [:navigation :route :options :back]))
        active-color @(rf/subscribe [:active-color])
        title @(rf/subscribe [:view.stopwatch-screen/title])
        child-props (merge props {:active-color active-color
                                  :title title})
        ]
    (prn title)
  ;; (prn (:js-props props))
    [:> NavigationHeader child-props]))