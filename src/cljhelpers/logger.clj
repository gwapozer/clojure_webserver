(ns cljhelpers.logger
  (:require [cljhelpers.file :as file]
            [cljhelpers.time :as t]
            )
  (:import (java.io File)))

(defn log-event [msg]
  (let [
        app-path (file/get-app-path)
        _fp (str app-path "\\" "Event.txt")
        f (File. _fp)
        mb (double (/ (.length f) (* 1024 1024)))
        nfp (str (.getParent f) "//" "Event.txt")
        _ (-> (File. nfp) .createNewFile)
        _ (cond (> mb 10) (file/write-to-file nfp (str (t/getCurrentLocalDateTime) ": " msg )) :else (file/update-file nfp (str (t/getCurrentLocalDateTime) ": " msg )))
        ]
    )
  )

(defn log-error [msg]
  (let [
        app-path (file/get-app-path)
        _fp (str app-path "\\" "Error.txt")
        f (File. _fp)
        mb (double (/ (.length f) (* 1024 1024)))
        nfp (str (.getParent f) "//" "Error.txt")
        _ (-> (File. nfp) .createNewFile)
        _ (cond (> mb 100) (file/write-to-file nfp (str (t/getCurrentLocalDateTime) ": " msg )) :else (file/update-file nfp (str (t/getCurrentLocalDateTime) ": " msg )))
        ]
    )
  )
