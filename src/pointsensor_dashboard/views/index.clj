(ns pointsensor_dashboard.views.index
  (:require [compojure.core :refer [defroutes GET POST]]
            [clojure.string :as str]
            [ring.util.response :as ring]
            [ring.util.codec :as cd]
            [pointsensor_dashboard.views.sensor :as sensor]
            [pointsensor_dashboard.views.main :as main]
            [pointsensor_dashboard.views.search :as search]
            [pointsensor_dashboard.controllers.psdb_controller :as psdb_controller]
            [pointsensor-dashboard.controllers.appsetting-controller :as as_controller]
            [pointsensor_dashboard.entity.pagination :refer :all]
            [clojure.core.async :refer [>!! timeout close! alts! chan go go-loop <! >!]]
            [cljhelpers.excel-writer :as xl]
            [pointsensor_dashboard.widget.prompt :as prt]
            [cljhelpers.objEval :as oe]
            [pointsensor-dashboard.config :refer [env]]
            [pointsensor-dashboard.validator.search-validator :as srch-validator]
            [hiccup.page :as h]
            [cljhelpers.app_setting :refer :all]
            [pointsensor-dashboard.entity.httpdatavar :refer :all]
            [pointsensor-dashboard.views.sensordata-search :as sd-search]
            [pointsensor-dashboard.views.sensordata-edit :as sd-edit]
            [pointsensor-dashboard.views.sensordata-multi-edit :as sd-multi-edit]
            [pointsensor-dashboard.views.appsetting-edit :as as-edit]
            [pointsensor-dashboard.views.sensorinfo-edit :as s-edit]
            [cljhelpers.datastruct :as ds]
            [cljhelpers.csv-writer :as csv]
            )
  )

(defn result
  [ result-str]
  (sensor/sensor result-str))

(defn main []
  ;(println "Index Start")
  (main/__main)
  )

(defn search-sensor []
  ;(println "Search Start")
  (search/search)
  )

(defn search-display-msg
  [msg]
  (h/html5 [:label msg])
  )

(defn search-sensor-result
  [data]
  ;(println (str "Search Result Start:" (read-string (cd/url-decode data))))
  (def msg (srch-validator/is-valid (read-string (cd/url-decode data))))
  ;(println msg)
  (if (clojure.string/blank? msg)
    (let [params (assoc (read-string (cd/url-decode data)) :PageIndex 0 :PageSize 1000 :SortField -1 :ForExport 0)
          rslt (psdb_controller/search-sensor params)
          ]
      (search/sensor-search-data rslt)
      )
    msg
    )
  )

(defn export-search-sensor-result [data]
  ;(println (str "Export Result Start:" (read-string (cd/url-decode data))))
  (def msg (srch-validator/is-valid (read-string (cd/url-decode data))))
  (if (clojure.string/blank? msg)
    (let [params (assoc (read-string (cd/url-decode data)) :PageIndex 0 :PageSize 1000 :SortField -1 :ForExport 1)
          rslt (psdb_controller/search-sensor params)
          ;_xlrslt (mapv #(mapv val %) rslt)
          ;__xlrslt (mapv #(conj [(:userid %) (:lasttemp %) (:averagetemp %) (:maxtemp %) (:mintemp %) (:median %) (:stddev %) (:eventtext %)]) rslt)
          ;_ (do (oe/eval-obj 'pointsensor_dashboard.views.index [__xlrslt _xlrslt] "search data") (oe/load-gui-tracer))
          ;_ (do (oe/eval-obj 'pointsensor_dashboard.views.index [_xlrslt] "xls data") (oe/load-gui-tracer))
          ;bytes-arr (xl/get-excel-generic-byte ["Sensor" "Last Temp (C)" "Avg Temp (C)" "Max Temp (C)" "Min Temp (C)" "Median Temp (C)" "Std Dev." "Event Text"] _xlrslt)
          _xlrslt (mapv #(conj [(:userid %) (:lasttemp %) (:averagetemp %) (:maxtemp %) (:mintemp %) (:median %) (:stddev %) (:eventtext %)] ) rslt)
          bytes-arr (csv/get-csv ["Sensor" "Last Temp (C)" "Avg Temp (C)" "Max Temp (C)" "Min Temp (C)" "Median Temp (C)" "Std Dev." "Event Text"] _xlrslt)
          ]
      bytes-arr
      )
    msg
    )
  )

(defn export-all-search-sensor-result [data]
  ;(println (str "Export All Result Start:" (read-string (cd/url-decode data))))
  (def msg (srch-validator/is-valid (read-string (cd/url-decode data))))
  (if (clojure.string/blank? msg)
    (let [params (assoc (read-string (cd/url-decode data)) :PageIndex 0 :PageSize 1000 :SortField -1 :ForExport 2)
          rslt (psdb_controller/search-sensor params)
          _xlrslt (mapv #(mapv val %) rslt)
          bytes-arr (xl/get-excel-generic-byte ["Sensor" "Temp (C)" "Event Text" "Reading On"] _xlrslt)
          ]
      ;(println (str "data count: " (count _xlrslt)))
      (cd/base64-encode (.toByteArray bytes-arr))
      ;(do
      ;  (loop [i 0 my-str ""]
      ;    (if (< i (count _xlrslt))
      ;      (let [_data (nth _xlrslt i)
      ;            _str (apply str (butlast (apply str (map #(str % ",") _data))))
      ;            ]
      ;        (recur (inc i) (str my-str _str "\n"))
      ;        )
      ;      (do
      ;        (cd/base64-encode (.getBytes my-str))
      ;        )
      ;      )
      ;    )
      ;  )
      )
    msg
    )
  )

(defn search-sensor-data-result
  ([data]
    ;(println (str "Search Sensor data Result Start:" (read-string (cd/url-decode data))))
   (def msg (srch-validator/is-valid (read-string (cd/url-decode data))))
    ;(println msg)
   (if (clojure.string/blank? msg)
     (let [parms (read-string (cd/url-decode data))
           fld (:chkSummary parms)
           _parms (assoc parms :SortField -1 :ForExport (cond (nil? fld) 0 :else 2))
           rslt (psdb_controller/search-sensor-data _parms)
           json (str "{count:\"" (:totalrows (first rslt) ) "\",data:\"" (cd/url-encode (str (cond (nil? fld) (sd-search/sensor-search-data rslt) :else (sd-search/sensor-search-data-summary rslt)))) "\"}")
           ]
       json
       )
     msg
     )
    )
  ([]
    ;(println "Sensor Data Search Start")
   (sd-search/search)
    )
  )

(defn search-sensor-data-edit-result
  [data]
  ;(println (str "Search Sensor data edit Result Start:" (read-string (cd/url-decode data))))
  (let [rslt (psdb_controller/get-sensor-data-byid data)
        ]
    ;(println rslt)
    (sd-edit/edit (first rslt))
    )
  )

(defn sensor-data-save
  [data]
  ;(println (str "Sensor data save edit Result Start:" (read-string (cd/url-decode data))))
  (let [parms (read-string (cd/url-decode data))
        rslt (psdb_controller/get-sensor-data-byid (:txtId parms))
        update-entity (assoc (first rslt) :comment (:txtComment parms) :statusid (:ddlStatusId parms))
        __updated-entity (psdb_controller/update-sensor-data update-entity)
        ]
    (sd-edit/edit __updated-entity)
    )
  )

(defn search-sensor-data-multi-edit-result
  [data]
  ;(println (str "Search Sensor data edit Result Start:" (cd/url-decode data)))
  (let [rslt (psdb_controller/get-sensor-data-byids (cd/url-decode data))]
    ;(println rslt)
    (sd-multi-edit/edit data rslt)
    )
  )

(defn sensor-data-multi-save
  [data]
  ;(println (str "Sensor data save multi edit Result Start:" (read-string (cd/url-decode data))))
  (let [parms (read-string (cd/url-decode data))
        _ (psdb_controller/update-sensor-data-byids parms)
        ]
    )
  )

(defn appsetting-edit-result
  []
  ;(println (str "Search Sensor data edit Result Start:" (read-string (cd/url-decode data))))
  (let [rslt (psdb_controller/get-appsetting-list)]
    ;(println rslt)
    (as-edit/edit rslt)
    )
  )

(defn appsetting-multi-save
  [data]
  ;(println (str "App setting save multi edit Result Start:" (cd/url-decode data)))
  (let [
        parms (read-string (cd/url-decode data))
        grp-keys (ds/group-keys parms [])
        _ (as_controller/update-appsetting-list grp-keys)
        rslt (psdb_controller/get-appsetting-list)]
    (as-edit/edit rslt)
    )
  )

(defn sensor-info-edit-result
  []
  ;(println (str "Search Sensor data edit Result Start:" (read-string (cd/url-decode data))))
  (let [rslt (psdb_controller/get-sensor-info-list)]
    ;(println rslt)
    (s-edit/edit rslt)
    )
  )

(defn sensor-info-multi-save
  [data]
  ;(println (str "Sensor save multi edit Result Start:" (cd/url-decode data)))
  (let [
        parms (read-string (cd/url-decode data))
        grp-keys (ds/group-keys parms [])
        _ (psdb_controller/update-sensor-info-list grp-keys)
        rslt (psdb_controller/get-sensor-info-list)
        ]
    ;(oe/eval-obj 'pointsensor_dashboard.views.index [grp-keys] "Test data") (oe/load-gui-tracer)
    (s-edit/edit rslt)
    )
  )

(defn rslt-data-index [result]
  ;(println "Data Index Start")
  (sensor/data-index result)
  )

(defn data-index []
  ;(println "Data Index Start")
  (sensor/data-index result)
  )

(defn sensor
  []
  (let [rslt (psdb_controller/get-sensor-reading)]
    (result rslt)
    )
  )

(defn ret-data
  []
  (let [rslt (psdb_controller/get-sensor-reading)]
    (rslt-data-index rslt)
    )
  )

(defroutes routes
           (GET "/" [] (main))
           (GET "/search/" [] (search-sensor))
           (GET "/search-sensor/:data" [data] (search-sensor-result data))
           (GET "/export-search-sensor/:data" [data] (export-search-sensor-result data))
           (GET "/export-all-search-sensor/:data" [data] (export-all-search-sensor-result data))
           (GET "/sensor/" [] (sensor))
           (GET "/data-index/" [] (ret-data))
           (POST "/" [] (sensor))

           (GET "/search-sensor-data/" [] (search-sensor-data-result))
           (GET "/search-sensor-data/:data" [data] (search-sensor-data-result data))
           (GET "/search-sensor-data-edit/:id" [id] (search-sensor-data-edit-result id))
           (GET "/sensor-data-save/:data" [data] (sensor-data-save data))
           (GET "/search-sensor-data-multi-edit/:ids" [ids] (search-sensor-data-multi-edit-result ids))
           (GET "/sensor-data-multi-save/:data" [data] (sensor-data-multi-save data))

           (GET "/appsetting-edit/" [] (appsetting-edit-result))
           (GET "/app-setting-multi-save/:data" [data] (appsetting-multi-save data))

           (GET "/sensorinfo-edit/" [] (sensor-info-edit-result))
           (GET "/sensorinfo-multi-save/:data" [data] (sensor-info-multi-save data))
           )