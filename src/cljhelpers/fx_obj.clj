(ns cljhelpers.fx_obj
  (:import (javafx.scene SceneBuilder)
           (javafx.scene.control ButtonBuilder)
           (javafx.scene.layout VBoxBuilder)
           (javafx.stage StageBuilder)
           (javafx.fxml FXMLLoader)
           (javas fxhelpers))
  (:import
           (javafx.scene.control Alert Alert$AlertType ButtonType TextInputDialog)
           (javafx.stage Modality FileChooser FileChooser$ExtensionFilter StageStyle)
           (javafx.scene Scene)
           (javafx.collections FXCollections)
           (java.util Collection Date)
           (java.text SimpleDateFormat)
           (java.io File)
           )
  (:use cljhelpers.FxRun)
  )

(defn observable-list
  "creates a new instance of observable array list from given collection"
  ([]
   (FXCollections/observableArrayList))
  ;(println "1. in observable list")
  ([^Collection col]
   (if (nil? col)
     ;(println "2. in observable list")
     (FXCollections/observableArrayList)
     (FXCollections/observableArrayList col))))

(defn to-id [kw]
  (str(read-string (reduce str (rest (prn-str kw)))) )
  )

(defn fxnode [stage kw]
  (let [root (.getRoot (.getScene stage))
        nodes (fxhelpers/getAllNodes root)
        _kw (to-id kw)
        node (filter #(= (.getId %) (to-id kw)) nodes)]
    (if (empty? node) nil (first node)))
  )

(defn _to-id
  "Convert keyword to JavaFx id format"
  [kw]
  (->> kw
       name
       (str "#")))

(defn nodify
  "Gets the node whose id matches the passed keyword"
  ([kw stage]
   (.lookup (.getScene stage) (to-id kw))))
