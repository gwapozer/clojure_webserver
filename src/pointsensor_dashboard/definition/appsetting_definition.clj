(ns pointsensor-dashboard.definition.appsetting-definition
  (:require  [pointsensor_dashboard.definition.table_struct :refer :all])
  )

(def appsetting_definition #{(->table-struct 1 "id" "id" "INTEGER" 10 true false true)
                                                                                   (->table-struct 2 "name" "name" "VARCHAR" 50 false false false)
                                                                                   (->table-struct 3 "value" "value" "VARCHAR" 500  false false false)
                                                                                   })