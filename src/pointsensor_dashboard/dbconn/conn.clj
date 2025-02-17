(ns pointsensor_dashboard.dbconn.conn
  (:require [cljhelpers.file :refer :all]
            [cljhelpers.plugin :as pl]
            [cljhelpers.file :as file]
            [cljhelpers.app_setting :as as]
            [cljhelpers.objEval :as oe])
  )

(defn db-spec-mssql
  []
  (let [app-path (file/get-app-path)
        item (clojure.java.io/resource "config.edn")
        my-config (readEdnFile item)
        db-driver (clojure.java.io/resource "drivers/mssql-jdbc-7.0.0.jre8.jar")
        curr-driver-path (.getPath db-driver)
        hasJar? (clojure.string/index-of (str curr-driver-path) (str (:jar-name my-config)))
        driver-path (cond (nil? hasJar?) curr-driver-path
                          :else  (str (-> (java.io.File. "") .getCanonicalPath) "/drivers/mssql-jdbc-7.0.0.jre8.jar"))
        _db-spec (:db-spec my-config)
        ]
    ;(oe/eval-obj 'pointsensor_dashboard.dbconn.conn [(as/app-path) (str (-> (java.io.File. "") .getCanonicalPath) "/drivers/mssql-jdbc-7.0.0.jre8.jar")] "File path") (oe/load-gui-tracer)
    _db-spec
    )
  )

