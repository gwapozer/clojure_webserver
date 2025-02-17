(ns pointsensor_dashboard.views.search
  (:require
    [pointsensor_dashboard.views.layout :as layout]
    [hiccup.core :refer [h html]]
    [hiccup.form :as form]
    [ring.util.anti-forgery :as anti-forgery]
    [cljhelpers.time :refer :all]
    [cljhelpers.objEval :as oe]
    [pointsensor_dashboard.controllers.psdb_controller :as psdb_controller]
    [hiccup.page :as h]
    )
  (:import (java.text SimpleDateFormat)
           (java.time.format DateTimeFormatter)
           (java.time LocalDateTime)))

(defn get-current-date
  [subtract-months]
  (let [now (LocalDateTime/now)
        curr (.minusMonths now subtract-months)
        dtf (DateTimeFormatter/ofPattern "MM/dd/yyyy")
        ]
    (.format dtf curr)
    )
  )

(def search-script (str "function ResetSearch() {$('#txtStartDate').val('" (get-current-date 1) "'); $('#txtEndDate').val('" (get-current-date 0) "'); $('#ddlSensorId').val('-1');}
function JquerySerialToHashMap(serializedData) {\n    var ar1 = serializedData.split(\"&\");\n    var json = \"{\";\n    for (var i = 0; i<ar1.length; i++) {\n        var ar2 = ar1[i].split(\"=\");\n        json += i > 0 ? \", \" : \"\";\n        json += \":\" + ar2[0] + \" \";\n        json += \"\\\"\" + (ar2.length < 2 ? \"\" : ar2[1]) + \"\\\"\";\n    }\n    json += \"}\";\n    return json;\n}\n
function SearchSensor() {var parms = JquerySerialToHashMap($('form').serialize().replace(/\\+/g,'%20'));$.ajax({type: 'GET', url: '/search-sensor/' + encodeURIComponent(parms), success: function(data) {$('#search-result').html(data)}, beforeSend: function(){ $('.loader').show()},complete: function(){ $('.loader').hide();}}); }\n
$(document).ready(function (){$('#btnSearch').click(function(){SearchSensor();});});\n
$(document).ready(function (){$('#btnClear').click(function(){ResetSearch(); $('#search-result').html('');});});
"))

(def export-script (str "
function s2ab(s) {\n  var buf = new ArrayBuffer(s.length);\n  var view = new Uint8Array(buf);\n  for (var i=0; i!=s.length; ++i) view[i] = s.charCodeAt(i) & 0xFF;\n  return buf;\n}
function CreateFileFromBinary(fileName, fileContent){var bin = s2ab(atob(fileContent)); var blob = new Blob([bin], {type: 'application/octet-stream'});const a = document.createElement('a');a.setAttribute('download', fileName);a.setAttribute('href', window.URL.createObjectURL(blob));document.body.appendChild(a);a.click();}
function ExportSearchSensor() {var parms = JquerySerialToHashMap($('form').serialize()); $.ajax({type: 'GET', url: '/export-search-sensor/' + encodeURIComponent(parms), success: function(data) {CreateFileFromBinary('ExportSensor.csv', data)}, beforeSend: function(){ $('.loader').show()},complete: function(){ $('.loader').hide();}});}
function ExportAllSearchSensor() {var parms = JquerySerialToHashMap($('form').serialize()); $.ajax({type: 'GET', url: '/export-all-search-sensor/' + encodeURIComponent(parms), success: function(data) {if (data != ''){CreateFileFromBinary('ExportAllSensor.xlsx', data)}else{$('#search-result').html(data)}}, beforeSend: function(){ $('.loader').show()},complete: function(){ $('.loader').hide();}});}
$('#export').click(function(e) {ExportSearchSensor();return false;});
$('#exportAll').click(function(e) {ExportAllSearchSensor();return false;});
"))

(defn sensor-list []
  (let
    [results (psdb_controller/get-sensor-list)]
    [:select {:id "ddlSensorId" :name "ddlSensorId"}
     (map (fn [result] [:option {:value (:id result)} (:userid result)]
            ) results)
     ]
    )
  )

(defn sensor-search-data [results]
  (do
    []
    (cond (= (count results) 0) (h/html5 "No results found")
          :else
          (h/html5
            [:table
             [:tr
              [:td [:a {:id "export" :href "#"} "[Export]"]] [:td [:a {:id "exportAll" :href "#"} "[Export All]"]]
              ]
             ]
            [:table {:border "1"}
             [:tr
              [:th {:align "left"} "Sensor"]
              [:th {:align "left"} "Status"]
              [:th {:align "left"} "Last Temp (C)"]
              [:th {:align "left"} "Avg Temp (C)"]
              [:th {:align "left"} "Max Temp (C)"]
              [:th {:align "left"} "Min Temp (C)"]
              [:th {:align "left"} "Median (C)"]
              [:th {:align "left"} "Std Dev."]
              ]
             (map (fn [result] [:tr
                                [:td {:width "100"} (:userid result)]
                                [:td {:width "80"} (cond (nil? (:eventtext result)) "ALIVE" :else (:eventtext result)) ]
                                [:td {:width "100"} (cond (nil? (:lasttemp result)) "" :else (format "%.1f" (:lasttemp result)))]
                                [:td {:width "100"} (cond (nil? (:averagetemp result)) "" :else (format "%.1f" (:averagetemp result)))]
                                [:td {:width "100"} (cond (nil? (:maxtemp result)) "" :else (format "%.1f" (:maxtemp result)))]
                                [:td {:width "100"} (cond (nil? (:mintemp result)) "" :else (format "%.1f" (:mintemp result)))]
                                [:td {:width "100"} (cond (nil? (:median result)) "" :else (format "%.1f" (:median result)))]
                                [:td {:width "100"} (cond (nil? (:stddev result)) "" :else (format "%.1f" (:stddev result)))]

                                ]
                    ) results)
             ]
            [:script export-script]
            )
          )
    )
  )

(defn display-search []
  [:form {:id "search-form"}
   [:center
    [:div {:id "search-entry"}
     [:table
      [:tr
       [:td [:label "Sensor name:"]]
       [:td {:colspan "2"} (sensor-list)]
       ]
      [:tr
       [:td [:label "Date range:"]]
       [:td [:input {:type "text" :id "txtStartDate" :name "txtStartDate" :size "8" :value (get-current-date 1)}]]
       [:td [:input {:type "text" :id "txtEndDate" :name "txtEndDate" :size "8" :value (get-current-date 0)}]]
       ]
      ]
     ]
    ]
   ]
  )

(defn search-form
  []
  [:div {:id "div-search-form" :class "sixteen columns alpha omega"}
   (display-search)
   [:center
    [:div {:id "search-event"}
     [:table [:tr [:td [:button {:id "btnSearch" :value "Search"} "Search"]] [:td  [:button {:id "btnClear" :value "Clear"} "Clear"]]]]
     ]
    [:div {:id "search-result" :white-space "pre-wrap"}]
    ]
   [:script search-script]
   ]
  )

(defn search
  ([]
   (layout/common-layout "Trend Data"
                  (search-form)
                  ))
  )