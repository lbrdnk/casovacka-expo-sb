(ns casovacka.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [expo.root :as expo-root]
            [casovacka.subs]
            [casovacka.events]
            ;; [casovacka.view.root :refer [root]]
            [casovacka.view.app :refer [app]]
            ;; [casovacka.view.components]
            ))

(def functional-compiler (r/create-compiler {:function-components true}))
(r/set-default-compiler! functional-compiler)

(defn start
  {:dev/after-load true}
  []
  (expo-root/render-root (r/as-element [app])))

(defn init []
  (rf/dispatch-sync [:init])
  (start))

(defn ^:dev/before-load stop []
  (js/console.log "stop")
  (rf/clear-subscription-cache!))