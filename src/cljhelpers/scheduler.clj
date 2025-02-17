(ns cljhelpers.scheduler
  (:import (java.util.concurrent Executors TimeUnit)))

(def executor (Executors/newSingleThreadScheduledExecutor))
(def fyutchur (atom nil))
(def state (atom []))

(defn run-every-five-seconds
  "Hightlight the contents of the specified tab."
  []
  (reify Runnable
    (run [this]
      (println "Five seconds have passed."))))

(defn cancel-future-if-needed
  "Cancel a future, if it exists. Does not interrupt
   the task if it has already started."
  []
  (when (and (not (nil? @fyutchur))
             (not (.isDone @fyutchur)))
    (.cancel @fyutchur false)))

(defn re-schedule-executor
  "Cancel any existing futures if needed and reset to
  a new delay period."
  []
  (cancel-future-if-needed)
  (reset! fyutchur (.scheduleAtFixedRate executor (run-every-five-seconds)
                                         5 15 TimeUnit/SECONDS)))

(defn shutdown-executor
  "Cancel any futures from running and shutdown
   the exector that schedules things to run."
  []
  (cancel-future-if-needed)
  (.shutdownNow executor))


(defn my-schedule
  [thread-pool-size delay interval job]
  (let [executor (Executors/newScheduledThreadPool thread-pool-size)]
    (.scheduleAtFixedRate executor job delay interval TimeUnit/MINUTES)
    executor
    )
  )
