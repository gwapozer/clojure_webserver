(ns cljhelpers.app_setting
  (:import (java.io File)))

(defn app-path []
  (let [f (File. "")
        p (.getAbsolutePath f)]
    (str p "\\")
    )
  )
