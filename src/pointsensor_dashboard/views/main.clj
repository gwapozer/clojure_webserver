(ns pointsensor_dashboard.views.main
  (:require
    [pointsensor_dashboard.views.layout :as layout]
    [hiccup.core :refer [h html]]
    [hiccup.form :as form]
    [ring.util.anti-forgery :as anti-forgery]
    [cljhelpers.time :refer :all]
    [cljhelpers.objEval :as oe]
    [pointsensor_dashboard.controllers.psdb_controller :as psdb_controller]
    [pointsensor-dashboard.router.ip-route :refer :all]
    )
  )

(defn main-form
  []
  [:div {:id "main-form" :class "main-content"}
   (form/form-to
     [:post "/"]
     (anti-forgery/anti-forgery-field)
     (form/label "Main" "")
     ;(form/submit-button "Load Sensor Reading")
     )
   ]
  )

(defn display-main []
  (do
    []
    [:center
     [:div {:id "my-links"}
      [:table [:tr
               [:td [:a {:href (str "http://" (get-locahost) "/sensor/") } "[Sensor Dashboard]"]]
               [:td [:a {:href (str "http://" (get-locahost) "/search/") } "[Trend Data]"]]
               [:td [:a {:href (str "http://" (get-locahost) "/search-sensor-data/") } "[Event Search]"]]
               [:td [:a {:href (str "http://" (get-locahost) "/appsetting-edit/") } "[Settings]"]]
               [:td [:a {:href (str "http://" (get-locahost) "/sensorinfo-edit/") } "[Freezer Assignment]"]]
               ]
       ]
      ]
     ]
    )
  )

(defn __main
  ([]
   (layout/common-layout "Main Menu"
                  (main-form)
                  [:div {:class "clear"}]
                  (display-main)
                  ))
  )