(ns pointsensor-dashboard.validator.search-validator
  (:require [cljhelpers.validator :refer :all])
  )

(defn is-valid
  "Validate parameters input before data is analyzed"
  [search-inputs]
  (let [my-msg (apply str
                      (if (clojure.string/blank? (:txtStartDate search-inputs)) "Start date cannot be blank<br/>" "")
                      (if (clojure.string/blank? (:txtEndDate search-inputs)) "End date cannot be blank<br/>" "")
                      (if (and (false? (is-valid-date (:txtStartDate search-inputs))) (false? (clojure.string/blank? (:txtStartDate search-inputs)))) "Invalid start date<br/>" "")
                      (if (and (false? (is-valid-date (:txtEndDate search-inputs))) (false? (clojure.string/blank? (:txtEndDate search-inputs)))) "Invalid end date<br/>" ""))
        ]
    my-msg
    )
  )
