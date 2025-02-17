(ns pointsensor-dashboard.definition.eventhistory-definition
  (:require  [pointsensor_dashboard.definition.table_struct :refer :all])
  )

(def eventhistory_definition #{(->table-struct 1 "id" "id" "INTEGER" 10 true false true)
                               (->table-struct 2 "appeventid" "appeventid" "INTEGER" 10 false true false)
                               (->table-struct 3 "tblname" "tblname" "VARCHAR" 50 false false false)
                               (->table-struct 4 "initdata" "initdata" "TEXT" 10 false false false)
                               (->table-struct 5 "updatedata" "updatedata" "TEXT" 10 false false false)
                               (->table-struct 6 "createdon" "createdon" "DATETIME" 10 false false false)
                               })


