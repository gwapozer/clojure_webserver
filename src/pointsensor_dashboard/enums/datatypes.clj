(ns pointsensor_dashboard.enums.datatypes
  (:require  [pointsensor_dashboard.entity.idname :refer :all])
  )

(def datatypes #{(pointsensor_dashboard.entity.idname/->idname 1 "vec" "vector")
                 (pointsensor_dashboard.entity.idname/->idname 2 "list" "list")
                 (pointsensor_dashboard.entity.idname/->idname 3 "map" "hash map")
                 (pointsensor_dashboard.entity.idname/->idname 4 "kw" "hash map keyword")
                 (pointsensor_dashboard.entity.idname/->idname 5 "val" "value")
                 })
