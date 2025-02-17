(ns pointsensor_dashboard.enums.fxfieldtypes
  (:require  [pointsensor_dashboard.entity.idname :refer :all])
  )

(def fxfieldtypes #{(pointsensor_dashboard.entity.idname/->idname 1 "lbl" "Label")
                    (pointsensor_dashboard.entity.idname/->idname 2 "txt" "TextField")
                    (pointsensor_dashboard.entity.idname/->idname 3 "hl" "Hyperlink")})