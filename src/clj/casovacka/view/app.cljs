(ns casovacka.view.app
  (:require ["react-native-safe-area-context" :as sa]
            [casovacka.view.navigation :refer [navigation]]
            ;; ["expo-status-bar" :refer [StatusBar]]
            ))

(defn app []
  ;; [:> sa/SafeAreaProvider
  ;;  [:> sa/SafeAreaView {:style {:flex 1}}
    [navigation]
    ;; ]]
  )