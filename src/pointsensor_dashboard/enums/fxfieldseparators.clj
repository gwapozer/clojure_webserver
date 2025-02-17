(ns pointsensor_dashboard.enums.fxfieldseparators
  (:require  [pointsensor_dashboard.entity.idname :refer :all])
  )

(def fxfieldseparators #{(pointsensor_dashboard.entity.idname/->idname 1 "->" "Pointer")})
