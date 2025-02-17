(ns pointsensor-dashboard.entity.appsetting)

(defrecord _ENT_AppSetting [id name value])

(defn get-appsetting-fields []
  (seq (.getDeclaredFields _ENT_AppSetting))
  )