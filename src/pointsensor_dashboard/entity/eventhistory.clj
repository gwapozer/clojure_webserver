(ns pointsensor-dashboard.entity.eventhistory
  (:require [cljhelpers.defrecord_utility :as ds]))

(defrecord _ENT_EventHistory [id appeventid tblname initdata updatedata createdon])

(defn get-eventhistory-fields []
  (seq (.getDeclaredFields _ENT_EventHistory))
  )

(defn my-event-history
  "Default record"
  []
  (let [ _t (ds/get-record-parity (get-eventhistory-fields))
        _my (ds/build-record-init _ENT_EventHistory _t)]
    _my
    )
  )