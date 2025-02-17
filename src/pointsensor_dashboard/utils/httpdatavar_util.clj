(ns pointsensor-dashboard.utils.httpdatavar-util
  (:require [obj-eval.core :as oe]))

(defn get-val-byfldname
  [fldName entity]
  Let [val (eval (read-string (str  "(:" fldName  " " entity ")"))) ]
  (oe/eval-obj 'pointsensor-dashboard.utils.httpdatavar-util [val] "Data extract") (oe/load-gui-tracer)
  val
  )