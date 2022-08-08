(ns casovacka.db
  (:require [re-frame.core :as rf]
            [casovacka.timer :as timer]
            [casovacka.audio :as audio]))

(def stretch-timer-map
  {:id #uuid "e2838bd3-7f5c-4c17-b400-76c672a035a1"
   :name "Stretching Routine"
   :color "pink"
   :activities
   {:order
    [#uuid "db241319-f54e-4ebb-a2e3-06efbed67807"]
    :data
    {#uuid "db241319-f54e-4ebb-a2e3-06efbed67807"
     {:id  #uuid "db241319-f54e-4ebb-a2e3-06efbed67807"
      :label "Stretching"
      :repeat 40
      :intervals
      {:order
       [#uuid "48142bdb-d155-4974-92f3-55172d697a87"
        #uuid "ebd52527-9c0a-4ce0-9baa-05ac5b01f60c"]

       :data
       {#uuid "48142bdb-d155-4974-92f3-55172d697a87"
        {:id #uuid "48142bdb-d155-4974-92f3-55172d697a87"
         :label "Prep"
         :when 10}
        #uuid "ebd52527-9c0a-4ce0-9baa-05ac5b01f60c"
        {:id #uuid "ebd52527-9c0a-4ce0-9baa-05ac5b01f60c"
         :label "Stretch"
         :when 60}}}}}}})

(def simple-timer-map
  {:id #uuid "d48fe293-0fbb-43e7-aae2-febbca34b015"
   :name "Daily workout"
   :color "lightgreen"
   :activities
   {:order
    [#uuid "f6dac085-b56a-4e90-a0cc-322548cae7dd"
     #uuid "1e2818da-b988-4da5-aa83-2c75780f6bb5"
     #uuid "fa08d78e-5b8f-415a-b04c-6b6896433473"]
    :data
    {#uuid "f6dac085-b56a-4e90-a0cc-322548cae7dd"
     {:id  #uuid "f6dac085-b56a-4e90-a0cc-322548cae7dd"
      :label "Jumping Rope"
      :repeat 4
      :intervals
      {:order
       [#uuid "5f5bacbd-adb7-4270-81b7-58c96492f146"
        #uuid "3e0f1909-6cc6-41c0-9d3d-557c31ed7792"
        #uuid "7f174944-b505-44ae-8be9-82058ed71f14"]
       :data
       {#uuid "5f5bacbd-adb7-4270-81b7-58c96492f146"
       {:id #uuid "5f5bacbd-adb7-4270-81b7-58c96492f146"
        :label "Get Ready"
        :when 20}
       #uuid "3e0f1909-6cc6-41c0-9d3d-557c31ed7792"
       {:id #uuid "3e0f1909-6cc6-41c0-9d3d-557c31ed7792"
        :label "Jump"
        :when 200}
       #uuid "7f174944-b505-44ae-8be9-82058ed71f14"
       {:id #uuid "7f174944-b505-44ae-8be9-82058ed71f14"
        :label "Rest"
        :when 240}}}}

     #uuid "1e2818da-b988-4da5-aa83-2c75780f6bb5"
     {:id #uuid "1e2818da-b988-4da5-aa83-2c75780f6bb5"
      :label "Warmup"
      :intervals
      {:order
       [#uuid "4278f807-f676-4414-a9ae-025e9995f72c"]
       :data
       {#uuid "4278f807-f676-4414-a9ae-025e9995f72c"
        {:id #uuid "4278f807-f676-4414-a9ae-025e9995f72c"
         :when 600}}}}

     #uuid "fa08d78e-5b8f-415a-b04c-6b6896433473"
     {:id #uuid "fa08d78e-5b8f-415a-b04c-6b6896433473"
      :label "Handstands"
      :repeat 4
      :intervals
      {:order
       [#uuid "fa45a4a1-a3da-4ca0-b8dc-bf83fd21e37e"
        #uuid "feb72f68-0b72-4e87-b7be-1f1f42b49b34"
        #uuid "4b965c06-24f1-4ee2-ad59-0e79f45b359a"]
       :data
       {#uuid "fa45a4a1-a3da-4ca0-b8dc-bf83fd21e37e"
        {:id #uuid "fa45a4a1-a3da-4ca0-b8dc-bf83fd21e37e"
         :label "Prep"
         :when 20}
        #uuid "feb72f68-0b72-4e87-b7be-1f1f42b49b34"
        {:id #uuid "feb72f68-0b72-4e87-b7be-1f1f42b49b34"
         :label "Handstand"
         :when 40}
        #uuid "4b965c06-24f1-4ee2-ad59-0e79f45b359a"
        {:id #uuid "4b965c06-24f1-4ee2-ad59-0e79f45b359a"
         :label "Rest"
         :when 80}}}}}}})

(def test-timer-map {:id #uuid "55450ea9-f6bf-4670-a470-e1488eff4809"
                     :name "Test timer"
                     :color "lightyellow"
                     :activities
                     {:order [#uuid "a0c5e5b8-d605-4c3b-a833-6f08d096ca93"]
                      :data
                      {#uuid "a0c5e5b8-d605-4c3b-a833-6f08d096ca93"
                       {:id #uuid "a0c5e5b8-d605-4c3b-a833-6f08d096ca93"
                        :label "Activity 1"
                        :repeat 10
                        :intervals
                        {:order [#uuid "58853e08-3af3-424e-bc5d-1c8be5251a54"
                                 #uuid "58853e08-3af3-424e-bc5d-1c8be5251a66"]
                         :data
                         {#uuid "58853e08-3af3-424e-bc5d-1c8be5251a54"
                          {:id #uuid "58853e08-3af3-424e-bc5d-1c8be5251a54"
                           :when 2
                           :signal audio/test-another-signal}
                          #uuid "58853e08-3af3-424e-bc5d-1c8be5251a66"
                          {:id #uuid "58853e08-3af3-424e-bc5d-1c8be5251a66"
                           :when 6}}}}}}})

(def initial-db {:timers {(:id simple-timer-map) simple-timer-map
                          (:id stretch-timer-map) stretch-timer-map
                          (:id test-timer-map) test-timer-map}
                ;;  :timer (timer/map->Timer {:interval-ms 64
                ;;                            :action #(rf/dispatch [:timer/tick])})
                 
                 :sounds audio/sounds
                 :color "#FFFFE0"})

(comment
  (def cur-timer (get-in @re-frame.db/app-db [:timers (-> @re-frame.db/app-db :view.stopwatch-tab/selected-timer-id)]))
  cur-timer
  )

;; functions on db datastructure
(defn stop-timer [db timer-id]
  (let [timer (get-in db [:timers timer-id])
        {:keys [start-epoch
                now-epoch]} timer
        timer-run-duration (- now-epoch start-epoch)]
    (-> db
        (update-in [:timers timer-id :passed-ms]
                   + timer-run-duration)
        (update-in [:timers timer-id]
                   dissoc :js-tick-interval-id :start-epoch :now-epoch))))
