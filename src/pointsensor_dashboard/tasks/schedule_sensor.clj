(ns pointsensor-dashboard.tasks.schedule-sensor
  (:require [pointsensor_dashboard.controllers.psdb_controller :as psdb_controller]
            [cljhelpers.objEval :as oe]
            [pointsensor-dashboard.entity.sensordata :refer :all]
            [cljhelpers.time :as t]
            [cljhelpers.email-utility :as eu]
            [pointsensor-dashboard.controllers.appsetting-controller :as appsetting_controller]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [cljhelpers.file :refer :all]
            [cljhelpers.app_setting :refer :all]
            [pointsensor-dashboard.utils.setting-util :refer :all]
            [cljhelpers.logger :as l]
            )
  (:import (java.time LocalDateTime)
           (java.time.format DateTimeFormatter)
           (java.util Date Calendar)
           ))

(defn- get-email-sensor-list []
  (let [EmailSensorEventList (appsetting_controller/get-appsettingByName "EmailSensorEventList")
        emails (:value (first EmailSensorEventList))
        em-arr (str/split emails #";")
        ]
    em-arr
    )
  )

(defn get-current-date
  [subtract-days]
  (let [now (LocalDateTime/now)
        curr (.minusDays now subtract-days)
        dtf (DateTimeFormatter/ofPattern "MM/dd/yyyy")
        ]
    (.format dtf curr)
    )
  )

(defn- email-event [subj body]
  (eu/simple-email {:smtp-server "smtp.test.com" :smtp-port 465 :smtp-user "test.com" :smtp-passw "test"}
                   "test@test.com"
                   (get-email-sensor-list)
                   subj ;(str subj (:name _my) " " (:evttext _my))
                   body ; (str body (:name _my) " " (:evttext _my) " " (:readingat _my))
                   )
  )

(defn process-sensor-event
  "Retrieve sensor data so it can be evaluated"
  []
  ;(println "Processing sensor event")
  (let [search-parms  {:SensorId nil :EvtIds (get-setting-val :EvtIds) :ReadingStartDate (get-current-date 7) :ReadingEndDate (get-current-date 0) :PageIndex 0 :PageSize 1000 :SortField nil :ForExport 0}
        event-list (psdb_controller/search-sensor-ByEventId search-parms)]
    ;(println "process-sensor-event")
    (loop [i 0]
      (if (< i (count event-list))
        (let [data (nth event-list i)
              _my (->_ENT_SensorData nil (:sensorid data) (:dataid data) (:userid data) (:eventtext data) 1 "" (t/getCurrentDateTime "MM/dd/YYYY HH:mm:ss.SSS"))
              ]
          (psdb_controller/add-sensor-data _my)
          (email-event (str "Sensor Event Alert: "  (:userid data) " " (clojure.string/trim (:eventtext data)))
                       (str "Event Alert for Service, Low Battery, Offline, Ch1 Low, Ch1 High, Ch2 Low, and Ch2 High."
                            "\n\nThe following item needs attention: "
                            "\nFreezer name: " (:userid data)
                            "\nTemperature: " (:temp data)
                            "\nEvent: " (clojure.string/trim (:eventtext data))
                            "\nReading on: " (:readingon data)))
          (recur (inc i))
          )
        )
      )
    )
  )

(defn process-sensor-temp
  "Retrieve sensor data so it can be evaluated"
  []
  ;(println "Processing sensor temp")
  (let [search-parms  {:FreezerIds (get-setting-val :FreezerIds) :FridgeIds (get-setting-val :FridgeIds) :ReadingStartDate (get-current-date 7) :ReadingEndDate (get-current-date 0) :PageIndex 0 :PageSize 1000 :SortField nil :ForExport 0}
        event-list (psdb_controller/search-sensor-ByTemp search-parms)]
    ;(println "process-sensor-temp")
    ;(println (str "Params: " search-parms " count: " (count event-list)))
    (loop [i 0]
      (if (< i (count event-list))
        (let [data (nth event-list i)
              _my (->_ENT_SensorData nil (:sensorid data) (:dataid data) (:userid data) (:eventtext data) 1 "" (t/getCurrentDateTime "MM/dd/YYYY HH:mm:ss.SSS"))
              ]
          (psdb_controller/add-sensor-data _my)
          (email-event (str "Sensor Temp Alert: " (:userid data) " " (clojure.string/trim (:eventtext data)))
                       (str "Freezer range -35 to -10. Fridge range 0 to 8."
                            "\n\nThe following item needs attention:"
                            "\nFreezer name: " (:userid data)
                            "\nTemperature: " (:temp data)
                            "\nEvent: " (clojure.string/trim (:eventtext data))
                            "\nReading on: " (:readingon data)))
          (recur (inc i))
          )
        )
      )
    )
  )

(defn send-reminder-event-list []
  (let [sensor-event-list (psdb_controller/get-active-sensor-event-list)]
    ;(println "send-reminder-event-list")
    (loop [i 0]
      (if (< i (count sensor-event-list))
        (let [data (nth sensor-event-list i)]
          (email-event (str "Sensor Event Reminder Alert: " (:name data) " " (clojure.string/trim (:evttext data)))
                       (str "Email Reminder for High Temp freezer (Ch1 High)."
                            "\n\nThe following item needs attention: "
                            "\nFreezer name: " (:name data)
                            "\nTemperature: " (:temp data)
                            "\nEvent: " (clojure.string/trim (:evttext data))
                            "\nReading on: " (:readingat data)))
          (recur (inc i))
          )
        )
      )
    )
  )

(defn process-email-for-sensor-event
  "Retrieve sensor data so it can be evaluated"
  []
  ;(println "Processing email sensor event")
  (let [cdt (Date.)
        cd (doto (Calendar/getInstance) (.setTime cdt))
        daysof (.get cd Calendar/DAY_OF_WEEK)
        hourof (.get cd Calendar/HOUR_OF_DAY)
        ]
    ;(println (str "Day of:"  daysof " Hour: " hourof) )
    ;Only send email during work hours but past certain hours send it every 2 hours
    (cond (and (or (= daysof 2) (= daysof 3) (= daysof 4) (= daysof 5) (= daysof 6)) (and (> hourof 9) (< hourof 19)) ) (do (send-reminder-event-list))
          (and (or (= daysof 2) (= daysof 3) (= daysof 4) (= daysof 5) (= daysof 6)) (or (< hourof 9) (> hourof 18)) (even? hourof)) (do (send-reminder-event-list))
          (and (or (= daysof 1) (= daysof 7)) (even? hourof)) (do (send-reminder-event-list))
      :else nil)
    ;End
    )
  )

(defn run-process-sensor-event
  "Hightlight the contents of the specified tab."
  []
  (reify Runnable
    (run [this]
      (try (do (process-sensor-event) (l/log-event "run-process-sensor-event")) (catch Exception e (l/log-error e)))
      )))

(defn run-process-sensor-temp
  "Hightlight the contents of the specified tab."
  []
  (reify Runnable
    (run [this]
      (try (do (process-sensor-temp) (l/log-event "run-process-sensor-temp")) (catch Exception e (l/log-error e)))
      )))


(defn run-process-email-for-sensor-event
  "Hightlight the contents of the specified tab."
  []
  (reify Runnable
    (run [this]
      (try (do (process-email-for-sensor-event) (l/log-event "run-process-email-for-sensor-event")) (catch Exception e (l/log-error e)))
      )))
