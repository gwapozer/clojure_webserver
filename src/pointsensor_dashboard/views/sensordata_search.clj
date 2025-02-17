(ns pointsensor-dashboard.views.sensordata-search
  (:require
    [ring.util.codec :as cd]
    [pointsensor_dashboard.views.layout :as layout]
    [hiccup.core :refer [h html]]
    [hiccup.form :as form]
    [ring.util.anti-forgery :as anti-forgery]
    [cljhelpers.time :refer :all]
    [cljhelpers.objEval :as oe]
    [pointsensor_dashboard.controllers.psdb_controller :as psdb_controller]
    [hiccup.page :as h]
    [pointsensor-dashboard.enums.statuses :refer :all]
    [pointsensor_dashboard.enums.enumerator :refer :all]
    [pointsensor-dashboard.views.paging :refer :all]
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

(def search-script (str "function ResetSearch(){
$('#txtStartDate').val('" (get-current-date 1) "');
$('#txtEndDate').val('" (get-current-date 0) "');
$('#ddlSensorId').val('-1');
$('#ddlStatusId').val('" (-> (GetEnumByKey statuses :id 1) :id) "');
$('#chkSummary').prop('checked', false);
}

function JquerySerialToHashMap(serializedData) {\n    var ar1 = serializedData.split(\"&\");\n    var json = \"{\";\n    for (var i = 0; i<ar1.length; i++) {\n        var ar2 = ar1[i].split(\"=\");\n        json += i > 0 ? \", \" : \"\";\n        json += \":\" + ar2[0] + \" \";\n        json += \"\\\"\" + (ar2.length < 2 ? \"\" : ar2[1]) + \"\\\"\";\n    }\n    json += \"}\";\n    return json;\n}\n

function SetPaging(){
var currIndex = $('#txtPageIndex').val();
var currSize = $('#txtPageSize').val();
var currTotal = $('#txtPageTotal').val(); $('#lblCurrPage').text(parseInt(currIndex) + 1);
if(((parseInt(currIndex) + 1) * currSize) > parseInt(currTotal)){ $('#lnkNext').hide();} else { $('#lnkNext').show();}
if(parseInt(currIndex) <= 0){ $('#lnkPrev').hide();} else { $('#lnkPrev').show();}
$('#lblTotalPage').text(Math.ceil(parseInt(currTotal) / parseInt(currSize)));
};

function SearchSensor() {var parms = JquerySerialToHashMap($('form').serialize().replace(/\\+/g,'%20'));$.ajax({type: 'GET', url: '/search-sensor-data/' + encodeURIComponent(parms), success: function(data) {var myJSON = JSON.stringify(eval(\"(\" + data + \")\")); var obj = JSON.parse(myJSON); $('#txtPageTotal').val(obj.count); $('#search-result').html(decodeURIComponent(obj.data)); SetPaging();}, beforeSend: function(){ $('.loader').show()},complete: function(){ $('.loader').hide();}}); }\n

$(document).ready(function (){$('#btnSearch').click(function(){$('#txtPageIndex').val(0);$('#txtPageTotal').val(0); SearchSensor();});});
$(document).ready(function (){$('#btnClear').click(function(){ResetSearch(); $('#search-result').html('');});});
"))

(def export-script (str "
function s2ab(s) {\n  var buf = new ArrayBuffer(s.length);\n  var view = new Uint8Array(buf);\n  for (var i=0; i!=s.length; ++i) view[i] = s.charCodeAt(i) & 0xFF;\n  return buf;\n}
function CreateFileFromBinary(fileName, fileContent){var bin = s2ab(atob(fileContent)); var blob = new Blob([bin], {type: 'application/octet-stream'});const a = document.createElement('a');a.setAttribute('download', fileName);a.setAttribute('href', window.URL.createObjectURL(blob));document.body.appendChild(a);a.click();}
function ExportSearchSensor() {var parms = JquerySerialToHashMap($('form').serialize()); $.ajax({type: 'GET', url: '/export-search-sensor/' + encodeURIComponent(parms), success: function(data) {CreateFileFromBinary('ExportSensor.xlsx', data)}, beforeSend: function(){ $('.loader').show()},complete: function(){ $('.loader').hide();}});}

function SearchNext() {var currIndex = $('#txtPageIndex').val(); $('#txtPageIndex').val(parseInt(currIndex) + 1); SearchSensor()};
function SearchPrev() {var currIndex = $('#txtPageIndex').val(); $('#txtPageIndex').val(parseInt(currIndex) - 1); SearchSensor()};

$('#export').click(function(e) {ExportSearchSensor();return false;});
$('#lnkNext').click(function(e) {SearchNext();return false;});
$('#lnkPrev').click(function(e) {SearchPrev();return false;});
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

(defn status-list []
  (let
    [results (psdb_controller/get-status-list)]
    [:select {:id "ddlStatusId" :name "ddlStatusId"}
     (map (fn [result] [:option {:value (:id result) :selected (= (:id result) (-> (GetEnumByKey statuses :id 1) :id))} (:name result)]
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
            [:table {:border "1"}
             [:tr [:td {:align "right" :colspan "9"} (display-paging)] ]
             [:tr
              [:th {:align "left"} "Sensor"]
              [:th {:align "left"} "Event"]
              [:th {:align "left"} "Temp (C)"]
              [:th {:align "left"} "In Range Temp (C)"]
              [:th {:align "left"} "Time Lap (min)"]
              [:th {:align "left"} "Status"]
              [:th {:align "left"} "Created On"]
              [:th {:align "left"} "Action"]
              ]
             (map (fn [result] [:tr
                                [:td {:width "100"} (:userid result)]
                                [:td {:width "80"} (cond (nil? (:evttext result)) "" :else (:evttext result)) ]
                                [:td {:width "100"} (cond (nil? (:field1float result)) "" :else (format "%.1f" (:field1float result)))]
                                [:td {:width "140"} (cond (nil? (:nextgoodtemp result)) "" :else (format "%.1f" (:nextgoodtemp result)))]
                                [:td {:width "140"} (:timelap result)]
                                [:td {:width "100"} (:statusname result)]
                                [:td {:width "100"} (:createdon result)]
                                [:td {:width "100"} [:a {:target "_blank" :href (str "/search-sensor-data-edit/" (:id result)) } "[Edit]"]]
                                ]
                    ) results)
             ]
            [:script export-script]
            )
          )
    )
  )

(defn sensor-search-data-summary [results]
  (do
    []
    (cond (= (count results) 0) (h/html5 "No results found")
          :else
          (h/html5
            [:table {:border "1"}
             [:tr [:td {:align "right" :colspan "8"} (display-paging)] ]
             [:tr
              [:th {:align "left"} "Sensor"]
              [:th {:align "left"} "Started On"]
              [:th {:align "left"} "Ended On"]
              [:th {:align "left"} "Min Temp (C)"]
              [:th {:align "left"} "Max Temp (C)"]
              [:th {:align "left"} "Avg Temp (C)"]
              [:th {:align "left"} "Temp Event"]
              [:th {:align "left"} "Action"]
              ]
             (map (fn [result] [:tr
                                [:td {:width "100"} (:userid result)]
                                [:td {:width "100"} (:startedon result)]
                                [:td {:width "100"} (:endedon result)]
                                [:td {:width "100"} (cond (nil? (:mintemp result)) "" :else (format "%.1f" (:mintemp result)))]
                                [:td {:width "100"} (cond (nil? (:maxtemp result)) "" :else (format "%.1f" (:maxtemp result)))]
                                [:td {:width "100"} (cond (nil? (:averagetemp result)) "" :else (format "%.1f" (:averagetemp result)))]
                                [:td {:width "100"} (cond (nil? (:evteventtxt result)) "" :else (:evteventtxt result)) ]
                                [:td {:width "100"} [:a {:target "_blank" :href (str "/search-sensor-data-multi-edit/" (cd/url-encode (:dataids result))) } "[Edit]"]]
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
   (display-pagination {:index 0 :size 15 :total 0})
   [:center
    [:div {:id "search-entry"}
     [:table
      [:tr
       [:td [:label "Sensor:"]]
       [:td {:colspan "2" } (sensor-list)]
       [:td [:label "Status:"]]
       [:td {:colspan "2" } (status-list)]
       ]
      [:tr
       [:td [:label "Date range:"]]
       [:td [:input {:type "text" :id "txtStartDate" :name "txtStartDate" :size "8" :value (get-current-date 1)}]]
       [:td [:input {:type "text" :id "txtEndDate" :name "txtEndDate" :size "8" :value (get-current-date 0)}]]
       [:td [:label "Summary?"]]
       [:td [:input {:type "checkbox" :id "chkSummary" :name "chkSummary"}]]
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
   (layout/common-layout "Event Search"
                  (search-form)
                  ))
  )
