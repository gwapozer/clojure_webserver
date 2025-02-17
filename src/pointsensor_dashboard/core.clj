(ns pointsensor_dashboard.core
  (:require [compojure.core :refer [defroutes]]
            [ring.adapter.jetty :as ring]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [pointsensor_dashboard.views.index :as index-model]
            [pointsensor_dashboard.views.layout :as layout]
            [pointsensor-dashboard.tasks.schedule-report :refer :all]
            [cljhelpers.email-utility :refer :all]
            [pointsensor-dashboard.tasks.schedule-sensor :refer :all]
            [cljhelpers.scheduler :refer :all]
            [pointsensor-dashboard.tasks.schedule-sensor :refer :all]
            [pointsensor-dashboard.utils.gui :as gui]
            [cljhelpers.objEval :as oe])
  (:gen-class)
  (:import (javafx.application Platform)))

(gen-class
  :name myapp.Application
  :extends javafx.application.Application
  :prefix "myapp-")

(defn load-gui []
  (gui/init)
  )

(defn -main
  "Program launcher."
  [& args]
  ;(javafx.application.Application/launch myapp.Application (into-array String args))
  (load-gui)
  )

;(defn -main []
;  (let [port (Integer. (or (System/getenv "PORT") "80"))
;        ;_ (load-gui)
;        ;_ (oe/eval-obj 'pointsensor_dashboard.core [(println "Point Sensor started")] "Point sensor start") (oe/load-gui-tracer)
;        ;_ (my-schedule 1 1 1440 (run-weekly-sensor-schedule-report))
;        ;_ (my-schedule 1 1 3 (run-process-sensor-event))
;        ;_ (my-schedule 1 1 5 (run-process-sensor-temp))
;        ;_ (my-schedule 1 2 10 (run-process-email-for-sensor-event))
;        ]
;    ;(start port)
;    )
;  )

(when *compile-files*
  (future (println "Process" (-> (java.lang.management.ManagementFactory/getRuntimeMXBean) .getName))
          (println "Waiting 10 secs before exiting JavaFX platform")
          (Thread/sleep 2000)
          (println "Exiting JavaFX platform")
          (Platform/setImplicitExit true)
          (Platform/exit))
  )
