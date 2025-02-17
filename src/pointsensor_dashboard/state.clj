(ns pointsensor-dashboard.state
  (:require [clojure.core.async :refer [go >!]])
  )

(def app-states "Preserves successive app-state for debugging" (atom []))

(def init-app-state "Initial application state"
  {
   :user nil
   :logged false
   :operations-channel nil
   }
  )

(def app-state "Application state" (atom init-app-state))

(defn set-get-atom-app-state
  ([] @app-state)
  ([x] (reset! app-state x))
  )

(defmulti handle-event
          "This multimethod dispatches on the event's :event key value each multimethod will receive the current app state and an event and should return an updated state"
          (fn [_ event]
            (:event event)))

(defn dispatch
  "This updates the application state with the results of the dispatch of the passed event to the handle-event multimethod, and sends it to the :operations-channel channel
  to notify the timeout process to reset its timer"
  [event]
  (try
    ;(prn-level :trace "dispatch " event)
    (if-let [ch (:operations-channel @app-state)]
      (go
        (>! ch (:event event))
        )
      )
    (if (nil? (last (last event)))
      :do-nothing
      (swap! app-state handle-event event))
    (catch Throwable ex
      ;(prn-level :error ex)
      )))
