(ns pointsensor-dashboard.config
  "This namespace exposes the env variable which is the results of merging the system properties, the environment properties and
   the configuration from the config.edn file which is included in the uberjar"
  (:require [cprop.core :refer [load-config]]
            [cprop.source :as source]
            [mount.core :refer [args defstate]]
            ))

;TODO map config files based on running application path
(defstate env :start
          (load-config
            :merge
            [                                    ;(args)
             (source/from-system-props)
             (source/from-env)
             ]))
