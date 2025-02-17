(ns pointsensor-dashboard.router.ip-route
  (:require [clojure.java.io :as io]
            [cljhelpers.file :refer :all]
            [cljhelpers.app_setting :refer :all]
            )
  )

(defn get-locahost
  []
  (try
    (let [lh (read-edn-file (io/file (str (app-path) "/settings.edn")))] (:local-ip lh))
    (catch Exception e "localhost"))
  )