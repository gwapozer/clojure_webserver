(ns pointsensor_dashboard.controllers.psdb_controller
  (:require [cljhelpers.dborm :as dborm]
            [pointsensor_dashboard.dbconn.conn :refer :all]
            [clojure.java.jdbc :as jdbc]
            [cljhelpers.plugin :as pl]
            [cljhelpers.objEval :as oe]
            [pointsensor-dashboard.entity.sensordata :refer :all]
            [pointsensor-dashboard.definition.sensordata-definition :refer :all]
            [cljhelpers.time :refer :all]
            [pointsensor-dashboard.entity.appsetting :refer :all]
            [pointsensor-dashboard.definition.sensor-definition :refer :all]
            [pointsensor-dashboard.entity.sensor :refer :all]
            [pointsensor-dashboard.entity.sensorinfo :refer :all]
            [pointsensor-dashboard.definition.sensorinfo-definition :refer :all]
            [cljhelpers.defrecord_utility :as ds]
            [pointsensor-dashboard.entity.eventhistory :refer :all]
            [pointsensor-dashboard.controllers.eventhistory-controller :as eh-ctler]
            [pointsensor-dashboard.enums.appevents :refer :all]
            [pointsensor_dashboard.enums.enumerator :refer :all]
            )
  (:import (cljhelpers.dborm Where-Cl Param-Cl)))

(defn exec-query
  [sql]
  (let [_db-spec (db-spec-mssql)
        rslt (dborm/db-query-update _db-spec sql)]
    rslt
    )
  )

(defn get-sensor-reading
  []
  (let [_db-spec (db-spec-mssql)
        rslt (dborm/db-query _db-spec "select R.SenId, S.UserId, SI.WifiMonitorName, R.EvtText, R.Field1Float, R.ReadingAt from Sensor S \nleft join _ENT_SensorInfo SI on SI.SensorId = S.id\ninner join \n(select * from data \nwhere id in (select max(id) from Data group by SenId) \n) R\non R.SenId = s.id\n\nORDER BY LEFT(SI.WifiMonitorName,PATINDEX('%[0-9]%',SI.WifiMonitorName)-1),CONVERT(INT,SUBSTRING(SI.WifiMonitorName,PATINDEX('%[0-9]%',SI.WifiMonitorName),LEN(SI.WifiMonitorName)))")]
    rslt
    )
  )

(defn get-sensor-list
  []
  (let [_db-spec (db-spec-mssql)
        ;rslt (dborm/db-query _db-spec "select id, UserId from Sensor ORDER BY LEFT(UserId,PATINDEX('%[0-9]%',UserId)-1),CONVERT(INT,SUBSTRING(UserId,PATINDEX('%[0-9]%',UserId),LEN(UserId)))")
        rslt (dborm/db-query _db-spec "select SI.SensorId as id, SI.WifiMonitorName as UserId from _ENT_SensorInfo SI inner join Sensor S on S.id = SI.SensorId ORDER BY LEFT(SI.WifiMonitorName,PATINDEX('%[0-9]%',SI.WifiMonitorName)-1),CONVERT(INT,SUBSTRING(SI.WifiMonitorName,PATINDEX('%[0-9]%',SI.WifiMonitorName),LEN(SI.WifiMonitorName)))")
        _list (reverse (into '() rslt))
        __ul (conj _list {:id -1 :userid "Please select a freezer"})]
    __ul
    )
  )

(defn get-status-list
  []
  (let [_db-spec (db-spec-mssql)
        rslt (dborm/db-query _db-spec "select id, name from _ENT_Status")
        _list (reverse (into '() rslt))
        __ul (conj _list {:id -1 :name "Please select a status"})]
    __ul
    )
  )

(defn search-sensor
  "Search sensor by stored proc"
  [params]
  (let [_db-spec (db-spec-mssql)
        rslt (dborm/db-proc _db-spec true "SearchSensor" [(Param-Cl. "SensorId" (:ddlSensorId params) nil)
                                                          (Param-Cl. "ReadingStartDate" (:txtStartDate params) nil)
                                                          (Param-Cl. "ReadingEndDate" (:txtEndDate params) nil)
                                                          (Param-Cl. "PageIndex" (:PageIndex params) nil)
                                                          (Param-Cl. "PageSize" (:PageSize params) nil)
                                                          (Param-Cl. "SortField" (:SortField params) nil)
                                                          (Param-Cl. "ForExport" (:ForExport params) nil)
                                                          ])
        ]
    rslt
    )
  )


(defn search-sensor-ByEventId
  "Search sensor by stored proc"
  [params]
  (let [_db-spec (db-spec-mssql)
        rslt (dborm/db-proc _db-spec true "SearchSensorByEventId" [
                                                                   (Param-Cl. "SensorId" (:SensorId params) nil)
                                                                   (Param-Cl. "EvtIds" (:EvtIds params) nil)
                                                                   (Param-Cl. "ReadingStartDate" (:ReadingStartDate params) nil)
                                                                   (Param-Cl. "ReadingEndDate" (:ReadingEndDate params) nil)
                                                                   (Param-Cl. "PageIndex" (:PageIndex params) nil)
                                                                   (Param-Cl. "PageSize" (:PageSize params) nil)
                                                                   (Param-Cl. "SortField" (:SortField params) nil)
                                                                   (Param-Cl. "ForExport" (:ForExport params) nil)
                                                          ])
        ]
    rslt
    )
  )

(defn search-sensor-ByTemp
  "Search sensor by stored proc"
  [params]
  (let [_db-spec (db-spec-mssql)
        rslt (dborm/db-proc _db-spec true "SearchSensorByTemp" [(Param-Cl. "FreezerIds" (:FreezerIds params) nil)
                                                                (Param-Cl. "FridgeIds" (:FridgeIds params) nil)
                                                                (Param-Cl. "ReadingStartDate" (:ReadingStartDate params) nil)
                                                                (Param-Cl. "ReadingEndDate" (:ReadingEndDate params) nil)
                                                                (Param-Cl. "PageIndex" (:PageIndex params) nil)
                                                                (Param-Cl. "PageSize" (:PageSize params) nil)
                                                                (Param-Cl. "SortField" (:SortField params) nil)
                                                                (Param-Cl. "ForExport" (:ForExport params) nil)
                                                                   ])
        ]
    rslt
    )
  )

(defn add-sensor-data
  [sd]
  (let [_db-spec (db-spec-mssql)
        rslt (dborm/db-insert _db-spec sensordata_definition sd)
        ]
    rslt
    )
  )

(defn update-sensor-data
  [entity]
  (let [_db-spec (db-spec-mssql)
        _my (->_ENT_SensorData (:id entity) (:sensorid entity) (:dataid entity) (:name entity) (:description entity) (:statusid entity) (:comment entity) (:createdon entity))
        result (dborm/db-update _db-spec sensordata_definition _my [(Where-Cl. "id" "=" (:id entity) nil nil)])]
    result
    )
  )

(defn get-sensor-data-byid
  [id]
  (let [
        _db-spec (db-spec-mssql)
        _my (->_ENT_SensorData nil nil nil nil nil nil nil nil)
        rslt (dborm/db-get _db-spec _my [(Where-Cl. "id" "=" id nil nil)] nil)
        ]
    rslt
    )
  )

(defn get-sensor-data-byids
  [ids]
  (let [_db-spec (db-spec-mssql)
        rslt (dborm/db-proc _db-spec true "GetSensorDataByDataIds" [(Param-Cl. "DataIds" ids nil)])]
    rslt
    )
  )

(defn update-sensor-data-byids
  "Search sensor by stored proc"
  [params]
  (let [_db-spec (db-spec-mssql)
        _ (dborm/db-proc _db-spec true "UpdateSensorDataByDataIds" [(Param-Cl. "DataIds" (:txtId params) nil)
                                                              (Param-Cl. "StatusId" (:ddlStatusId params) nil)
                                                              (Param-Cl. "Comment" (:txtComment params) nil)
                                                              ])
        ]
    )
  )

(defn search-sensor-data
  "Search sensor by stored proc"
  [params]
  (let [_db-spec (db-spec-mssql)
        rslt (dborm/db-proc _db-spec true "SearchSensorData" [(Param-Cl. "SensorId" (:ddlSensorId params) nil)
                                                              (Param-Cl. "StatusId" (:ddlStatusId params) nil)
                                                              (Param-Cl. "ReadingStartDate" (:txtStartDate params) nil)
                                                              (Param-Cl. "ReadingEndDate" (:txtEndDate params) nil)
                                                              (Param-Cl. "PageIndex" (:txtPageIndex params) nil)
                                                              (Param-Cl. "PageSize" (:txtPageSize params) nil)
                                                              (Param-Cl. "SortField" (:SortField params) nil)
                                                              (Param-Cl. "ForExport" (:ForExport params) nil)
                                                          ])
        ]
    rslt
    )
  )

(defn get-active-sensor-event-list
  []
  (let [_db-spec (db-spec-mssql)
        rslt (dborm/db-query _db-spec "select SD.Name, D.EvtText, D.Field1Float as Temp, D.ReadingAt from _ENT_SensorData SD left join Data D on D.id = SD.DataID where SD.StatusID = 1 AND (D.EvtId = 1002  OR D.EvtId = 2002)")]
    rslt
    )
  )

(defn get-appsetting-list
  []
  (let [_db-spec (db-spec-mssql)
        _my (->_ENT_AppSetting nil nil nil)
        rslt (dborm/db-get _db-spec _my nil nil)
        ]
    rslt
    )
  )

;(defn get-sensor-list
;  []
;  (let [_db-spec (db-spec-mssql)
;        _my (->sensor nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil
;                      nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil
;                      nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil)
;        rslt (dborm/db-get _db-spec _my nil nil)
;        ]
;    rslt
;    )
;  )

(defn get-sensor-byid
  [id]
  (let [
        _db-spec (db-spec-mssql)
        _my (->sensor nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil
                      nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil
                      nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil)
        rslt (dborm/db-get _db-spec _my [(Where-Cl. "id" "=" id nil nil)] nil)
        ]
    rslt
    )
  )

;(defn update-sensor
;  [_sensor]
;  (let [_db-spec (db-spec-mssql)
;        _my (str "UPDATE SENSOR SET UserId='" (:userid _sensor) "' WHERE id =" (:id _sensor))
;        _ (exec-query _my)]
;    )
;  )
;
;(defn update-sensor-list
;  [my-data]
;  (doseq [x my-data]
;    ;(println x)
;    (update-sensor x)
;    )
;  )
;
;(defn add-sensor-data
;  [sd]
;  (let [_db-spec (db-spec-mssql)
;        rslt (dborm/db-insert _db-spec sensordata_definition sd)]
;    rslt
;    )
;  )
;
;(defn duplicate-sensor-byid
;  [id]
;  (let [_db-spec (db-spec-mssql)
;        _t (ds/get-record-parity (get-sensor-fields))
;        _my (ds/build-record-init ->sensor _t)
;        rslt (first (dborm/db-get _db-spec _my [(Where-Cl. "id" "=" id nil nil)] nil))
;        _rslt (assoc rslt :deletedat (str (getCurrentLocalDateTime)))
;        _data (ds/build-record-data ->sensor _rslt _t)
;        _ (dborm/db-update _db-spec sensor_definition _data [(Where-Cl. "id" "=" id nil nil)])
;        ]
;    ;(oe/eval-obj 'pointsensor_dashboard.controllers.psdb_controller [_data id] "test data") (oe/load-gui-tracer)
;    )
;  )

(defn get-sensor-info-list
  []
  (let [_db-spec (db-spec-mssql)
        rslt (dborm/db-query _db-spec "select SI.ID, SI.WifiMonitorName, S.UserId from _ENT_SensorInfo SI inner join Sensor S on S.id = SI.SensorId ORDER BY LEFT(SI.WifiMonitorName,PATINDEX('%[0-9]%',SI.WifiMonitorName)-1),CONVERT(INT,SUBSTRING(SI.WifiMonitorName,PATINDEX('%[0-9]%',SI.WifiMonitorName),LEN(SI.WifiMonitorName)))")]
    rslt
    )
  )

(defn update-sensor-info
  [_sensorinfo]
  (let [_db-spec (db-spec-mssql)
        _t (ds/get-record-parity (get-sensorinfo-fields))
        _my (ds/build-record-init ->_ENT_SensorInfo _t)
        rslt (first (dborm/db-get _db-spec _my [(Where-Cl. "id" "=" (:id _sensorinfo) nil nil)] nil))
        _rslt (assoc rslt :wifimonitorname (:wifimonitorname _sensorinfo))
        _data (ds/build-record-data ->_ENT_SensorInfo _rslt _t)
        _ (dborm/db-update _db-spec sensorinfo_definition _data [(Where-Cl. "id" "=" (:id _sensorinfo) nil nil)])
        ;ADD Event History
        _ (if (not= (str rslt) (str _rslt)) (eh-ctler/add-event-history (-> (GetEnumByKey appevents :name "update") :id) "_ENT_SENSORINFO" (str rslt) (str _rslt)))
        ;END
        ]
    )
  )

(defn update-sensor-info-list
  [my-data]
  (doseq [x my-data]
    ;(println x)
    (update-sensor-info x)
    )
  )