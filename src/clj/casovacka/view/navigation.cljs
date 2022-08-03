(ns casovacka.view.navigation
  (:require [reagent.core :as r]

            ["react-native" :as rn]

            ["@react-navigation/native" :refer [NavigationContainer]]
            ["@react-navigation/bottom-tabs" :refer [createBottomTabNavigator]]
            ["@expo/vector-icons/Ionicons$default" :as Ionicons]

            [casovacka.view.components :as components]))

(defn render-navigation-header [args]
  ;; (def x (reduce))
  (let [{:keys [navigation
                route
                options
                back]} (js->clj args :keywordize-keys true)]
    (r/as-element [components/navigation-header {:navigation navigation
                                                 :route route
                                                 :options options
                                                 :back back}])))

(defn navigation []
  (let [Tab (createBottomTabNavigator)
        navigator-props
        {:screenOptions
         (fn [route]
           ;; TODO use #js
           (clj->js
            {;;:headerStyle {:backgroundColor "red"}
             ;; TODO
             ;;  :header (fn [] nil)
             :header render-navigation-header
             :tabBarIcon
             (fn [args-js]
               (let [{:keys [_focused size color]} (js->clj args-js :keywordize-keys true)
                     icon-name (case (.. ^js route -route -name)
                                 "StopwatchTab" "timer"
                                 "TimersTab" "list"
                                 "MoreTab" "ellipsis-horizontal"
                                 "at")]
                 (r/as-element [:> Ionicons {:name icon-name :size size :color color}])))
             :tabBarActiveTintColor "tomato"
             :tabBarInactiveTintColor "gray"
             ;; https://reactnavigation.org/docs/bottom-tab-navigator/#headershown
             ;; :tabBarStyle #js {:backgroundColor "gainsboro"}
            ;;  :headerShown false
             }))
         :initialRouteName "StopwatchTab"}]
    [:> NavigationContainer
     [:> (.-Navigator Tab)
      navigator-props
      [:> (.-Screen Tab)
       {:name "StopwatchTab"
        ;; following yields warning
        ;; :component (r/reactify-component home-screen)
        ;; hence using children function
        :options {:tab-bar-label "Stopwatch"}}
       (fn [props] (r/as-element [components/stopwatch-screen props]))]
      [:> (.-Screen Tab)
       {:name "SbTab"
        :options {:tab-bar-label "Storybook"}}
       (fn [props] (r/as-element [:> components/Storybook]))]]]))



#_[:> (.-Screen Tab)
   {:name "TimersTab"
    :options {:tab-bar-label "Timers"}}
   (fn [props] (r/as-element [timers-tab-root props]))]
#_[:> (.-Screen Tab)
   {:name "MoreTab"
    :options {:tab-bar-label "More"}}
   (fn [props] (r/as-element [:> rn/View
                              [:> rn/Text "More"]]))]