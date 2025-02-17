(ns pointsensor-dashboard.definition.sensordata-definition
  (:require  [pointsensor_dashboard.definition.table_struct :refer :all])
  )

(def sensordata_definition #{(->table-struct 1 "id" "id" "INTEGER" 10 true false true)
                             (->table-struct 2 "sensorid" "sensorid" "INTEGER" 10 false false false)
                             (->table-struct 3 "dataid" "dataid" "INTEGER" 10 false false false)
                             (->table-struct 4 "name" "name" "VARCHAR" 50  false false false)
                             (->table-struct 5 "description" "description" "VARCHAR" 50  false false false)
                             (->table-struct 6 "statusid" "statusid" "INTEGER" 10 false true false)
                             (->table-struct 7 "comment" "comment" "VARCHAR" 500  false false false)
                             (->table-struct 8 "createdon" "createdon" "DATETIME" nil  false false false)
                             })