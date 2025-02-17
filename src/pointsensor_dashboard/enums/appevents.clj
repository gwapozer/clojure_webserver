(ns pointsensor-dashboard.enums.appevents
  (:require  [pointsensor_dashboard.entity.idname :refer :all])
  )

(def appevents #{(pointsensor_dashboard.entity.idname/->idname 1 "add" "add")
                 (pointsensor_dashboard.entity.idname/->idname 2 "update" "update")
                 (pointsensor_dashboard.entity.idname/->idname 3 "delete" "delete")
                 })
