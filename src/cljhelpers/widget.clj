(ns cljhelpers.widget
  (:require [clojure.core.async :refer [>!! timeout close! alts! chan go go-loop <! >!]]
             )
  (:import (javafx.scene.control Alert$AlertType Alert ButtonType)
           (javafx.scene Scene)
           (javafx.stage Modality))
  (:use cljhelpers.FxRun)
  )

(defn display-error
  "Displays the passed error in a dialog box"
  [title message]
  (doto (Alert. (. Alert$AlertType ERROR))
    (.setTitle title)
    (.setHeaderText nil)
    (.setContentText message)
    (.showAndWait)))

(defn build-dialog
  "Creates a confirmation dialog"
  [^Scene scene ^String title ^String text]
  (doto (Alert. Alert$AlertType/CONFIRMATION text (into-array [^:ButtonType ButtonType/NO ^:ButtonType ButtonType/YES]))
    (.setHeaderText title)
    (.setGraphic nil)
    (.initOwner (.getWindow scene))
    (.initModality Modality/WINDOW_MODAL)))

(defn async-dialog
  "Async dialog returns true/false in the passed channel"
  ([app-stage out title content warning-mins]
   (run-later
     (let [dialog (build-dialog (.getScene app-stage) title content)]
       (>!! out dialog)
       (if-let [ret (-> dialog
                        .showAndWait
                        (.orElse nil)
                        (= ButtonType/YES))]
         (>!! out true)
         (>!! out false)
         )
       ))
    )
  ([app-stage out warning-mins]
   (async-dialog app-stage out "Session Expiring" (str "Your session will expire in " warning-mins " minute(s) do you want to continue your session?") warning-mins )))