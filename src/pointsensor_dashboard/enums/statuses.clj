(ns pointsensor-dashboard.enums.statuses
  (:require  [pointsensor_dashboard.entity.idname :refer :all])
  )

(def statuses #{(pointsensor_dashboard.entity.idname/->idname 1 "Active" "Active")
                 (pointsensor_dashboard.entity.idname/->idname 2 "Completed" "Completed")
                 (pointsensor_dashboard.entity.idname/->idname 3 "Cancelled" "Cancelled")
                 })

