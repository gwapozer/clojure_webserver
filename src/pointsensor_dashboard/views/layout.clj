(ns pointsensor_dashboard.views.layout
  (:require [hiccup.page :as h]
            [clojure.java.io :as io]
            [cljhelpers.file :refer :all]
            [cljhelpers.app_setting :refer :all]
            [pointsensor-dashboard.router.ip-route :refer :all]
            )
  (:use [hiccup.page :only (html5 include-css include-js)]
        [hiccup.element :only (link-to)])
  )

(def menu (str "document.write(\"<a href='http://" (get-locahost) "'>[Home]</a>\");
document.write(\"<a href='http://" (get-locahost) "/sensor/'>[Sensor Dashboard]</a>\");
document.write(\"<a href='http://" (get-locahost) "/search/'>[Trend Data]</a>\");
document.write(\"<a href='http://" (get-locahost) "/search-sensor-data/'>[Event Search]</a>\");
document.write(\"<a href='http://" (get-locahost) "/appsetting-edit/'>[Settings]</a>\");
document.write(\"<a href='http://" (get-locahost) "/sensorinfo-edit/'>[Freezer Assignment]</a>\");
"))

(defn common-layout [title & body]
  (h/html5
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1"}]
     [:title title]
     (h/include-css
       "/css/loader.css"
       )
     (h/include-css
       "http://fonts.googleapis.com/css?family=Sigmar+One&v1"
       )
     (h/include-js
       "/js/jquery.min.js"
       "/js/loader.js"
       )
     [:script menu]
     ]
    [:body
     [:div {:class "loader"}
      [:center [:img {:class "loading-image" :src "/graphics/giphy.gif" :alt ""}]]
      ]
     [:div {:id "header"}
      [:center [:h1 {:class "container"} title]]
      ]
     [:div {:id "content" :class "container"} body]]))

(defn edit-layout [title & body]
  (h/html5
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1"}]
     [:title title]
     (h/include-css
       "/css/loader.css"
       )
     (h/include-css
       "http://fonts.googleapis.com/css?family=Sigmar+One&v1"
       )
     (h/include-js
       "/js/jquery.min.js"
       "/js/loader.js"
       )
     ]
    [:body
     [:div {:class "loader"}
      [:center [:img {:class "loading-image" :src "/graphics/giphy.gif" :alt ""}]]
      ]
     [:div {:id "header"}
      [:center [:h1 {:class "container"} title]]
      ]
     [:div {:id "content" :class "container"} body]]))

(defn html-value [value]
  (h/html5 value))

(defn not-found []
  [:br
   [:center
    [:div [:font {:size "9" :color "orange"} "Page Not Found"]]
    [:div [:font {:size "4" :color "red"} (link-to {:class "btn btn-primary"} "/" "Take me to Home")] ]
    ]
   ]
  )