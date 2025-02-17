(ns pointsensor-dashboard.utils.setting-util
  (:require [clojure.java.io :as io]
            [cljhelpers.app_setting :refer :all]
            [cljhelpers.file :refer :all]
            ))

(defn get-setting-val
  [kw]
  (try
    (let [lh (read-edn-file (io/file (str (app-path) "/settings.edn")))] (kw lh))
    (catch Exception e nil))
  )