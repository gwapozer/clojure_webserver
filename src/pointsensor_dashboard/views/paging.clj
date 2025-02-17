(ns pointsensor-dashboard.views.paging
  (:require [hiccup.page :as h]))

(defn display-pagination
  [pagination]
  (h/html5
    [:input {:type "hidden" :id "txtPageIndex" :name "txtPageIndex" :value (:index pagination)}]
    [:input {:type "hidden" :id "txtPageSize" :name "txtPageSize" :value (:size pagination)}]
    [:input {:type "hidden" :id "txtPageTotal" :name "txtPageTotal" :value (:total pagination)}]
    )
  )

(defn display-paging
  []
  (h/html5
    [:table
     [:tr
      [:td "Page " [:label {:id "lblCurrPage"}] " of " [:label {:id "lblTotalPage"}] " total pages."] [:td [:a {:id "lnkPrev" :href "#"} "<"]] [:td [:a {:id "lnkNext" :href "#"} ">"]]
      ]
     ]
    )
  )

