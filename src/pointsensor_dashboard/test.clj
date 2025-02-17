(ns pointsensor_dashboard.test
  ;(:gen-class)
  (:require [pointsensor_dashboard.controllers.psdb_controller :as psdb_ctler]
            [cljhelpers.objEval :as oe]
            [pointsensor_dashboard.widget.prompt :as prt]
            [cljhelpers.time :refer :all]
            [cljhelpers.file :refer :all]
            [cljhelpers.app_setting :refer :all]
            [clojure.java.io :as io]
            [cljhelpers.validator :refer :all]
            [cljhelpers.scheduler :refer :all]
            [pointsensor-dashboard.tasks.schedule-report :refer :all]
            [pointsensor-dashboard.controllers.appsetting-controller :as app-ctler]
            [pointsensor-dashboard.tasks.schedule-report :refer :all]
            [cljhelpers.email-utility :refer :all]
            [pointsensor-dashboard.tasks.schedule-sensor :refer :all]
            [pointsensor-dashboard.controllers.appsetting-controller :as appsetting_controller]
            [clojure.string :as str]
            [cljhelpers.email-utility :as eu]
            [cljhelpers.defrecord_utility :as ds]
            [pointsensor-dashboard.entity.sensor :refer :all]
            [pointsensor-dashboard.controllers.eventhistory-controller :as eh-ctler]
            [pointsensor_dashboard.enums.enumerator :refer :all]
            [pointsensor-dashboard.enums.appevents :refer :all]
            [pointsensor-dashboard.utils.setting-util :refer :all]
            [pointsensor-dashboard.tasks.schedule-sensor :as ss]
            [pointsensor-dashboard.utils.gui :as gui])
  ;(:require
  ;          [pointsensor_dashboard.core :as core]
  ;          [ring.adapter.jetty :as ring]
  ;          )
  (:import (java.util Base64 Calendar Date)
           (javax.swing.filechooser FileNameExtensionFilter FileFilter)
           (java.io File)
           (java.text SimpleDateFormat)
           (java.time LocalDateTime)
           (java.time.format DateTimeFormatter)
           (jline Terminal)
           (org.apache.commons.mail SimpleEmail DefaultAuthenticator)))

;(defn my-start [port]
;(ring/run-jetty application {:port port
;                             :join? false })
;  )
;
;(defn test-main []
;  (let [port (Integer. (or (System/getenv "PORT") "8080"))]
;    (my-start port)
;    )
;  )

;(defn get-current-date
;  [subtract-months]
;  (let [now (LocalDateTime/now)
;        curr (.minusMonths now subtract-months)
;        dtf (DateTimeFormatter/ofPattern "MM/dd/yyyy")
;        ]
;    (.format dtf curr)
;    )
;  )

(defn decode [to-decode]
  (String. (.decode (Base64/getDecoder) to-decode))
  )

; If running with lein, use "lein trampoline run"
(defn test-scheduler
  []
  (re-schedule-executor)
  (let [t (Terminal/getTerminal)]
    (loop [k (.readCharacter t System/in)]
      (if (= k 75)
        (shutdown-executor)
        (do
          (when (= k 13)
            (swap! state conj k)
            (println "@state:" @state))
          (if (and (= k 13)
                   (>= (count @state) 5))
            (do
              (println "one action")
              (shutdown-executor))
            (recur (.readCharacter t System/in))))))))

;(defn get-next-monday
;  [dt]
;  (loop [curr-date dt day nil]
;    (if (not= day 2)
;      (let [cd (doto (Calendar/getInstance) (.setTime curr-date))
;            _ (.add cd Calendar/DATE 1)
;            daysof (.get cd Calendar/DAY_OF_WEEK)
;            cdt (.getTime cd)
;            ]
;        (recur cdt daysof)
;        )
;      curr-date
;      )
;    )
;  )

(defn- get-email-sensor-list []
  (let [EmailSensorEventList (appsetting_controller/get-appsettingByName "EmailSensorEventList")
        emails (:value (first EmailSensorEventList))
        em-arr (str/split emails #";")
        ]
    em-arr
    )
  )


(defn simple-email-test
  []
  (let [my-email (SimpleEmail.)
        _ (.setHostName my-email "smtp.test.com")
        _ (.setSmtpPort my-email 465)
        _ (.setAuthenticator my-email (DefaultAuthenticator. "ent.fridge@test.com" "test"))
        _ (.setSSLOnConnect my-email true)
        _ (.setFrom my-email "test@test.com")
        _ (.setSubject my-email "TEst  email send")
        _ (.setMsg my-email "Email Body Tes data data GS")
        _ (.addTo my-email "test@test.com","test@test.com" )
        ]
    (.send my-email)
    )
  )

(defn test-db-query []
  (let [test
        ;default-path (str (System/getProperty "user.home") "\\Desktop")
        ;default-file (File. (str default-path) "\\ExportAllSensor.xlsx")
        ;filters (FileNameExtensionFilter. "Excel File" (into-array ["xlsx"]))
        ;file-path (prt/directory-file-chooser "Please choose the file location" default-file default-path filters)
        ;rslt-sr (get-sensor-reading)
        ;rslt-sr (search-sensor)
        ;lh (get-locahost)
        ;date-info (is-valid-date "8/8/8")
        ;test-schedule1 (my-schedule 1 3 5 (run-prt5-schedule-report))
        ;test-schedule2 (my-schedule 1 1 2 (run-weekly-sensor-schedule-report))
        ;rslt-data (app-ctler/get-appsettingByName "SensorReportTask")
        ;test-schedl (weekly-sensor-report)
        ;test-dld (get-next-monday (Date.))

        ;_ (send-email {:smtp-server "smtp.1and1.com" :smtp-port 465 :smtp-user "test@test.com" :smtp-passw "test"}
        ;              "ent.test@test.com"
        ;              "test.test@test.com"
        ;              "Subj Send email from test clojure"
        ;              "Message Send email from test clojure")

        ;_ (simple-email-test)
        ;_ (process-email-for-sensor-event)
        ]
    ;(test-scheduler)
    ;(oe/eval-obj 'pointsensor_dashboard.test [test-dld] "Test schedule event") (oe/load-gui-tracer)
    ;(println rslt-data)
    )
  )

(defn loadapp []
  ;(core/load-digester)
  (test-db-query)
  )

(defrecord vardata [id name])

(defn test-email []
  (let [_ (ss/send-reminder-event-list)
        _ (ss/process-sensor-temp)
        _ (ss/process-sensor-event)
        ]
    )
  )
(defn test-datastruct
  []
  (let [
        ;_d (psdb_ctler/duplicate-sensor-byid 1)
        _dt (ds/get-record-parity (get-sensor-fields))
        keys-list (mapv #(read-string (str ":" (clojure.string/lower-case %))) _dt)
        _definition (mapv #(str "(->table-struct 2 \"" (clojure.string/lower-case %) "\" \""(clojure.string/lower-case %)"\" \"VARCHAR\" 50 false false false)") _dt)
        ; _ (eh-ctler/add-event-history (-> (GetEnumByKey appevents :name "update") :id) "_ENT_SENSORINFO" "before" "after")
        _ (println (get-setting-val :FreezerIds))
        _ (println (get-setting-val :FridgeIds))
        _ (println (get-setting-val :EvtIds))
        ;_ (process-sensor-event)
        ;_ (process-sensor-temp)
        _ (weekly-sensor-report)
        ]
    (oe/eval-obj 'pointsensor_dashboard.test [(count _dt) keys-list _definition] "Test schedule event") (oe/load-gui-tracer)
    )
  )

(defn load-gui []
  (gui/init)
  )

(defn main [func]
  (println "Main function starting")

  (try (func)
       (catch Exception e (prn "caught" e)))

  (println "Main function ending")
  )

;(test-main)
(main load-gui)
