(ns pointsensor-dashboard.views.sensor-edit
  (:require
    [pointsensor_dashboard.views.layout :as layout]
    [hiccup.core :refer [h html]]
    [hiccup.form :as form]
    [ring.util.anti-forgery :as anti-forgery]
    [cljhelpers.time :refer :all]
    [cljhelpers.objEval :as oe]
    [pointsensor_dashboard.controllers.psdb_controller :as psdb_controller]
    [hiccup.page :as h]
    [ring.util.codec :as cd])
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
function JquerySerialToHashMap(serializedData) \n{\nvar ar1 = serializedData.split('&');\nvar json = \"{\";\nfor (var i = 0; i<ar1.length; i++) \n{\nvar ar2 = ar1[i].split(\"=\");\nvar id = ar2[0];\nvar value = ar2[1];\nvar res = value.replace(/%5C/g,\"%5C%5C\");\njson += i > 0 ? \", \" : \"\";\njson += \":\" + id + \" \";\njson += \"\\\"\" + (ar2.length < 2 ? \"\" : res) + \"\\\"\";\n}\njson += \"}\";\nreturn json;\n}\n
function SaveSensor() {var parms = JquerySerialToHashMap($('form').serialize().replace(/\\+/g,'%20'));$.ajax({type: 'GET', url: '/sensor-multi-save/' + encodeURIComponent(parms), success: function(data) {}, beforeSend: function(){ $('.loader').show()},complete: function(){ $('.loader').hide();}}); }\n
$(document).ready(function (){$('#btnSave').click(function(){SaveSensor();});});
"))

(defn display-edit [results]
  (do
    ;(oe/eval-obj 'pointsensor-dashboard.views.sensor-edit [results] "Sensor Data") (oe/load-gui-tracer)
    []
    [:form {:id "edit-form"}
     [:center
      [:table {:border "1"}
       [:tr
        [:th {:align "left"} "Name"]
        [:th {:align "left"} "UserID"]
        ]
       (map (fn [result] [:tr
                          [:input {:type "hidden" :id (str "id_" (:id result))  :name (str "id_" (:id result)) :value (:id result)}]
                          [:td {:width "100"} [:input {:style "color: Grey; opacity: 1;" :readonly true :type "text" :id (str "name_" (:id result))  :name (str "name_" (:id result)) :value (:name result)}]]
                          [:td {:width "150"}  [:input {:size "50" :maxlength 10 :type "text" :id (str "userid_" (:id result)) :name (str "userid_" (:id result)) :value (:userid result)}]]
                          ]
              ) results)
       ]
      ]
     ]
    )
  )

(defn edit-form
  [entity-list]
  [:div {:id "div-edit-form" :class "sixteen columns alpha omega"}
   (display-edit entity-list)
   [:center
    [:div {:id "edit-event"}
     [:table [:tr [:td [:button {:id "btnSave" :value "Save"} "Save"]]]]
     ]
    ]
   [:script edit-script]
   ]
  )

(defn edit
  ([entity-list]
   (layout/common-layout "Sensor Edit"
                         (edit-form entity-list)
                         ))
  )
