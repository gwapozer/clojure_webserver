(ns pointsensor_dashboard.views.sensor
  (:require
    [pointsensor_dashboard.views.layout :as layout]
    [hiccup.core :refer [h html]]
    [hiccup.form :as form]
    [ring.util.anti-forgery :as anti-forgery]
    [cljhelpers.time :refer :all]
    [cljhelpers.objEval :as oe]
    [pointsensor_dashboard.controllers.psdb_controller :as psdb_controller])
  )

(defn sensor-form
  []
  [:div {:id "sensor-form"}
   (form/form-to
     [:post "/"]
     (anti-forgery/anti-forgery-field)
     (form/label "Sensor" "")
     ;(form/submit-button "Load Sensor Reading")
     )
   ]
  )

(defn get-sensor-data
  []
  (let [rslt (psdb_controller/get-sensor-reading)]
    rslt
    )
  )

(defn sensor-data [results]
  (do
    []
    [:table
     [:tr
      [:th {:align "left"} "Sensor"]
      [:th {:align "left"} "Wifi Monitor Name"]
      [:th {:align "left"} "Status"]
      [:th {:align "left"} "Temp (C)"]
      [:th {:align "left"} "Reading On"]
      ]
     (map (fn [result] [:tr
                        [:td {:width "100"} (:userid result)]
                        [:td {:width "160"} (:wifimonitorname result)]
                        [:td {:width "70"} (cond (nil? (:evttext result)) "ALIVE" :else (:evttext result)) ]
                        [:td {:width "70"} (cond (nil? (:field1float result)) "" :else (format "%.1f" (:field1float result)))]
                        [:td {:width "200"} (:readingat result) ]
                        ]
            ) results)
     ]
    )
  )

(defn display-sensor-reading [results]
  (do
    []
    [:center
     [:script "
    function FreezerInfo() {$.ajax('/data-index/',{success: function (data, status, xhr) {$('#my-reading').html(data);}});}
    function GetFreezerInfo() {setInterval(function(){ $.ajax('/data-index/',{success: function (data, status, xhr) {$('#my-reading').html(data);}}); }, 300000);}
    window.onload = GetFreezerInfo;
    $(document).ready(function (){$('#ajaxBtn').click(function(){$.ajax('/data-index/',{success: function (data, status, xhr) {$('#my-reading').html(data);}, beforeSend: function(){ $('.loader').show()},complete: function(){ $('.loader').hide();}});});});
    "]
     [:div {:id "my-btn"}
      [:table [:tr
               [:td [:button {:id "ajaxBtn" :value "Get data"} "Refresh reading"]]
               ]
       ]
      ]
     [:div {:id "my-reading"}
      (sensor-data results)
      ]
     ]
    )
  )

(defn sensor
  ([results]
   (layout/common-layout "Sensor Dashboard"
                  (sensor-form)
                  [:div {:class "clear"}]
                  (display-sensor-reading results)))
  )

(defn data-index
  [results]
  (layout/html-value (sensor-data results))
  )