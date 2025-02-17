(ns cljhelpers.validator
  (:import (java.time LocalDate)
           (java.time.format DateTimeFormatter)
           (java.util Locale Date)))

(defn numeric? [s]
  (if-let [s (seq s)]
    (let [s (if (= (first s) \-) (next s) s)
          s (drop-while #(Character/isDigit %) s)
          s (if (= (first s) \.) (next s) s)
          s (drop-while #(Character/isDigit %) s)]
      (empty? s))))

(defn bool-string? [s]
  (or (= (clojure.string/lower-case s) "true") (=  (clojure.string/lower-case s) "false"))
  )

(defn hasnum? [x]
  (let [_dat (re-find #"\d+" x)]
    (-> _dat nil? not)
    )
  )

(defn is-valid-email
  [email]
  (let [pattern #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"]
    (and (string? email) (re-matches pattern email))))

(defn is-quoted
  [quote]
  (let [pattern #"\".*\""]
    (re-matches pattern quote))
  )

(defn is-valid-name
  [name]
  (let [pattern #"[a-zA-Z]*"]
    (re-matches pattern name)))

(defn is-valid-date
  [s]
  (try
    (let [dt (Date/parse s)]
      true
      )
    (catch Exception e false))
  )
