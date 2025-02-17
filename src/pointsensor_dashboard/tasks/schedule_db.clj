(ns pointsensor-dashboard.tasks.schedule-db
  (:require [pointsensor-dashboard.utils.setting-util :refer :all]
            [cljhelpers.file :as f]
            [pointsensor-dashboard.controllers.appsetting-controller :as appsetting_controller]
            [cljhelpers.logger :as l])
  (:import
    (javas ProcessUtil)
           )
  )

;(defn- build-bat-cmd [sql-binn filename]
;  (let [file-path (appsetting_controller/get-appsettingByName "ReportFilePath")
;        cmdstr (str "@echo off\n"
;                    "cd " "\"" sql-binn "\""
;                    "SQLCMD.EXE -i " "\"" (f/get-app-path) "\\db-backup.sql" "\""
;                    "copy " "\"" filename "\"" " " "\"" (:value (first file-path)) "\"" "\n")
;        bf (str (f/get-app-path) "\\run-db-back.bat")
;        _ (spit bf cmdstr)
;        ]
;    bf
;    )
;  )

(defn process-db-backup-event
  "Back up psdb db"
  []
  (let [
        cmd (get-setting-val :dbbakcmd)
        _ (ProcessUtil/ExecCmd cmd)])
  )

(defn run-process-db-backup-event
  []
  (reify Runnable
    (run [this]
      (try (do (process-db-backup-event) (l/log-event "run-weekly-sensor-schedule-report")) (catch Exception e (l/log-error e)))
      )))
