(ns pointsensor-dashboard.controllers.eventhistory-controller
  (:require [cljhelpers.dborm :as dborm]
            [pointsensor_dashboard.dbconn.conn :refer :all]
            [pointsensor-dashboard.definition.eventhistory-definition :refer :all]
            [pointsensor-dashboard.entity.eventhistory :refer :all]
            [cljhelpers.time :refer :all]
            ))

(defn add-event-history
  [appeventid tblname initdata updatedata]
  (let [_db-spec (db-spec-mssql)
        eh (->_ENT_EventHistory nil appeventid tblname initdata updatedata (str (getCurrentLocalDateTime)))
        rslt (dborm/db-insert _db-spec eventhistory_definition eh)]
    rslt
    )
  )
