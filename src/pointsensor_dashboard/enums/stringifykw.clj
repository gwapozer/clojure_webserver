(ns pointsensor_dashboard.enums.stringifykw
  (:require  [pointsensor_dashboard.entity.idname :refer :all])
  )

(def stringifykw #{(pointsensor_dashboard.entity.idname/->idname 1 "name" "name")})