(ns pointsensor-dashboard.views.sensordata-edit
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

(def edit-script (str "function ResetSearch() {$('#txtStartDate').val('" (get-current-date 1) "'); $('#txtEndDate').val('" (get-current-date 0) "'); $('#ddlSensorId').val('-1');}
function JquerySerialToHashMap(serializedData) {var ar1 = serializedData.split('&');var json = \"{\";for (var i = 0; i<ar1.length; i++) {var ar2 = ar1[i].split(\"=\");json += i > 0 ? \", \" : \"\";json += \":\" + ar2[0] + \" \";json += \"\\\"\" + (ar2.length < 2 ? \"\" : ar2[1]) + \"\\\"\";}json += \"}\";return json;}
function SaveSensor() {var parms = JquerySerialToHashMap($('form').serialize().replace(/\\+/g,'%20'));$.ajax({type: 'GET', url: '/sensor-data-save/' + encodeURIComponent(parms), success: function(data) {}, beforeSend: function(){ $('.loader').show()},complete: function(){ $('.loader').hide();}}); }\n
$(document).ready(function (){$('#btnSave').click(function(){SaveSensor();});});
$(document).ready(function (){$('#btnClose').click(function(){ window.close();});});
"))

(defn sensor-list [sensor-id]
  (let
    [results (psdb_controller/get-sensor-list)]
    [:select {:id "ddlSensorId" :name "ddlSensorId"}
     (map (fn [result] [:option {:value (:id result)} (:userid result)]
            ) results)
     ]
    )
  )

(defn status-list [status-id]
  (let
    [results (psdb_controller/get-status-list)]
    [:select {:id "ddlStatusId" :name "ddlStatusId"}
     (map (fn [result] [:option {:value (:id result) :selected (= status-id (:id result))} (:name result)]
            ) results)
     ]
    )
  )

(defn display-edit
  [entity]
  [:form {:id "edit-form"}
   [:center
    [:div {:id "edit-entry"}
     [:input {:type "hidden" :id "txtId" :name "txtId" :value (:id entity)}]
     [:table
      [:tr
       [:td [:label "Sensor name:"]]
       [:td  (:name entity)]
       [:td [:label "Status:"]]
       [:td  (status-list (:statusid entity))]
       ]
      [:tr
       [:td [:label "Description:"]]
       [:td  (:description entity)]
       [:td [:label "Created On:"]]
       [:td  (:createdon entity)]
       ]
      [:tr
       [:td [:label "Comment:"]]
       [:td {:colspan "3"} [:textarea {:rows "5" :cols "50" :id "txtComment" :name "txtComment" :resize "none"} (:comment entity)]]
       ]
      ]
     ]
    ]
   ]
  )

(defn edit-form
  [entity]
  [:div {:id "div-edit-form" :class "sixteen columns alpha omega"}
   (display-edit entity)
   [:center
    [:div {:id "edit-event"}
     [:table [:tr [:td [:button {:id "btnSave" :value "Save"} "Save"]] [:td  [:button {:id "btnClose" :value "Close"} "Close"]]]]
     ]
    ]
   [:script edit-script]
   ]
  )

(defn edit
  ([entity]
   (layout/edit-layout "Event Edit"
                  (edit-form entity)
                  ))
  )

