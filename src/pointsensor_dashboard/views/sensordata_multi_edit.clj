(ns pointsensor-dashboard.views.sensordata-multi-edit
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
function SaveSensor() {var parms = JquerySerialToHashMap($('form').serialize().replace(/\\+/g,'%20'));$.ajax({type: 'GET', url: '/sensor-data-multi-save/' + encodeURIComponent(parms), success: function(data) {}, beforeSend: function(){ $('.loader').show()},complete: function(){ $('.loader').hide();}}); }\n
$(document).ready(function (){$('#btnSave').click(function(){SaveSensor();});});
$(document).ready(function (){$('#btnClose').click(function(){ window.close();});});
"))

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
  [ids entity-list]
  [:form {:id "edit-form"}
   [:center
    [:div {:id "edit-entry"}
     [:input {:type "hidden" :id "txtId" :name "txtId" :value ids}]
     [:table
      [:tr
       [:td [:label "Sensor name:"]]
       [:td  (:name (first entity-list))]
       [:td [:label "Status:"]]
       [:td  (status-list -1)]
       ]
      [:tr
       [:td [:label "Comment:"]]
       [:td {:colspan "3"} [:textarea {:rows "5" :cols "50" :id "txtComment" :name "txtComment" :resize "none"} ]]
       ]
      ]
     ]
    ]
   ]
  )

(defn edit-form
  [ids entity-list]
  [:div {:id "div-edit-form" :class "sixteen columns alpha omega"}
   (display-edit ids entity-list)
   [:center
    [:div {:id "edit-event"}
     [:table [:tr [:td [:button {:id "btnSave" :value "Save"} "Save"]] [:td  [:button {:id "btnClose" :value "Close"} "Close"]]]]
     ]
    ]
   [:script edit-script]
   ]
  )

(defn edit
  ([ids entity-list]
   (layout/edit-layout "Event Multi Edit"
                       (edit-form ids entity-list)
                       ))
  )
