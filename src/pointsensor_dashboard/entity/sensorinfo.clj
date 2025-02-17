(ns pointsensor-dashboard.entity.sensorinfo)

(defrecord _ENT_SensorInfo [id sensorid wifimonitorname])

(defn get-sensorinfo-fields []
  (seq (.getDeclaredFields _ENT_SensorInfo))
  )