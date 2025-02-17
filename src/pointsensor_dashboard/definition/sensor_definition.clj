(ns pointsensor-dashboard.definition.sensor-definition
  (:require  [pointsensor_dashboard.definition.table_struct :refer :all])
  )

(def sensor_definition #{(->table-struct 1 "id" "id" "INTEGER" 10 true false true)})