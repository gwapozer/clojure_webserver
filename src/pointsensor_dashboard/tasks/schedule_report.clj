(ns pointsensor-dashboard.tasks.schedule-report
  (:require [pointsensor_dashboard.controllers.psdb_controller :as psdb_controller]
            [pointsensor-dashboard.controllers.appsetting-controller :as appsetting_controller]
            [cljhelpers.excel-writer :as xl]
            [cljhelpers.time :as t]
            [cljhelpers.csv-writer :as csv]
            [cljhelpers.file :as f]
            [cljhelpers.logger :as l])
  (:import (java.time LocalDateTime)
           (java.time.format DateTimeFormatter)
           (java.util Date Calendar)
           (javas ProcessUtil)))

(defn get-next-monday
  [dt]
  (loop [curr-date dt day nil]
    (if (not= day 2)
      (let [cd (doto (Calendar/getInstance) (.setTime curr-date))
            _ (.add cd Calendar/DATE 1)
            daysof (.get cd Calendar/DAY_OF_WEEK)
            cdt (.getTime cd)
            ]
        (recur cdt daysof)
        )
      curr-date
      )
    )
  )

(defn get-current-date-subdays
  [subtract-days]
  (let [now (LocalDateTime/now)
        curr (.minusDays now subtract-days)
        dtf (DateTimeFormatter/ofPattern "MM/dd/yyyy")
        ]
    (.format dtf curr)
    )
  )

(defn- build-bat-cmd [filename path]
  (let [cmdstr (str "@echo off\n" "copy " "\"" filename "\"" " " "\"" (:value (first path)) "\"" "\n" "del "  "\"" filename "\"" " /s /f /q")
        bf (str (f/get-app-path) "\\run-rpt.bat")
        _ (spit bf cmdstr)
        ]
    bf
    )
  )

(defn- excel-rpt
  [wfp]
  (let [file-path (str (f/get-app-path) "\\Weekly-report_" (clojure.string/replace (get-current-date-subdays 7) #"/" "-")  "-" (clojure.string/replace (get-current-date-subdays 0) #"/" "-") ".csv")
        params {:txtStartDate (get-current-date-subdays 7) :txtEndDate (get-current-date-subdays 0) :ddlSensorId -1 :PageIndex 0 :PageSize 100 :SortField -1 :ForExport 1}
        rslt (psdb_controller/search-sensor params)
        ;_xlrslt (mapv #(mapv val %) rslt)
        _xlrslt (mapv #(conj [(:userid %) (:lasttemp %) (:averagetemp %) (:maxtemp %) (:mintemp %) (:median %) (:stddev %) (:eventtext %)] ) rslt)
        _ (csv/write-csv file-path ["Sensor" "Last Temp (C)" "Avg Temp (C)" "Max Temp (C)" "Min Temp (C)" "Median Temp (C)" "Std Dev." "Event Text"] _xlrslt)
        _  (do
             (ProcessUtil/ExecCmd (build-bat-cmd file-path wfp))
             )
        ]
    ;(xl/write-to-excel-generic file-path ["Sensor" "Last Temp (C)" "Avg Temp (C)" "Max Temp (C)" "Min Temp (C)" "Median Temp (C)" "Std Dev." "Event Text"] _xlrslt)
    )
  )

(defn weekly-sensor-report
  []
  ;If the current date is greater than the next run date, run the task update the table for the next monday
  ;(println "Running weekly report")
  (let [report-data (appsetting_controller/get-appsettingByName "SensorReportTask")
        nrptd (:value (first report-data))
        curr-dt (Date/parse (str (t/getCurrentDateTime "MM/dd/YYYY")))
        rpt-dt (Date/parse nrptd)]
    (if (>= curr-dt rpt-dt)
      (let [next-m (get-next-monday (Date.))
            file-path (appsetting_controller/get-appsettingByName "ReportFilePath")
            ]
        (excel-rpt file-path)
        (appsetting_controller/update-appsetting (assoc (first report-data) :value (t/getDateTimeByFormat next-m "MM/dd/YYYY")))
        )
      )
    )
  ;End
  )

(defn run-weekly-sensor-schedule-report
  "Hightlight the contents of the specified tab."
  []
  (reify Runnable
    (run [this]
      (try (do (weekly-sensor-report) (l/log-event "run-weekly-sensor-schedule-report")) (catch Exception e (l/log-error e)))
      )))

