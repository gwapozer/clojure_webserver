(ns pointsensor-dashboard.controllers.appsetting-controller
  (:require [cljhelpers.dborm :as dborm]
            [pointsensor_dashboard.dbconn.conn :refer :all]
            [pointsensor-dashboard.entity.appsetting :refer :all]
            [pointsensor-dashboard.definition.appsetting-definition :refer :all]
            [cljhelpers.objEval :as oe]
            [pointsensor-dashboard.controllers.eventhistory-controller :as eh-ctler]
            [cljhelpers.defrecord_utility :as ds]
            [pointsensor-dashboard.enums.appevents :refer :all]
            [pointsensor_dashboard.enums.enumerator :refer :all]
            )
  (:import (cljhelpers.dborm Where-Cl)))

(defn get-appsettingById
  [id]
  (let [_db-spec (db-spec-mssql)
        _as (->_ENT_AppSetting nil nil nil )
        rslt (dborm/db-get _db-spec _as [(Where-Cl. "id" "=" id nil nil)] nil)
        ]
    rslt
    )
  )

(defn get-appsettingByName
  [name]
  (let [_db-spec (db-spec-mssql)
        _as (->_ENT_AppSetting nil nil nil )
        rslt (dborm/db-get _db-spec _as [(Where-Cl. "name" "=" name nil nil)] nil)
        ]
    rslt
    )
  )

(defn update-appsetting
  [_appsetting]
  (let [_db-spec (db-spec-mssql)
        _t (ds/get-record-parity (get-appsetting-fields))
        rslt (first (get-appsettingById (:id _appsetting)))
        _rslt (assoc rslt :name (:name _appsetting) :value (:value _appsetting))
        _data (ds/build-record-data ->_ENT_AppSetting _rslt _t)
        result (dborm/db-update _db-spec appsetting_definition _data [(Where-Cl. "id" "=" (:id _appsetting) nil nil)])
        _ (if (not= (str rslt) (str _rslt)) (eh-ctler/add-event-history (-> (GetEnumByKey appevents :name "update") :id) "_ENT_APPSETTING" (str rslt) (str _rslt)))
        ]
    result
    )
  )

(defn update-appsetting-list
  [my-data]
  (doseq [x my-data]
    (update-appsetting x)
    )
  )