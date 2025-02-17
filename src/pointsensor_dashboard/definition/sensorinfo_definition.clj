(ns pointsensor-dashboard.definition.sensorinfo-definition
  (:require  [pointsensor_dashboard.definition.table_struct :refer :all])
  )

(def sensorinfo_definition #{(->table-struct 1 "id" "id" "INTEGER" 10 true false true)
                             (->table-struct 2 "sensorid" "sensorid" "INTEGER" 10 false true false)
                             (->table-struct 3 "wifimonitorname" "wifimonitorname" "VARCHAR" 50  false false false)
                             })