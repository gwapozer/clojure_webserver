(ns cljhelpers.time
  (:import (java.text SimpleDateFormat)
           (java.util Date)
           (java.time LocalDateTime)
           (java.time.format DateTimeFormatter)))

(defn getCurrentDateTime
  [format]
  (let [dateFormat (SimpleDateFormat. format)]
    (.format dateFormat (Date.))
    )
  )

(defn getCurrentLocalDateTime
  []
  (java.time.LocalDateTime/now)
  )

(defn getCurrentDateTimeforFile
  []
  (let [dateFormat (SimpleDateFormat. "MMddYYYYHHmmssSSS")]
    (.format dateFormat (Date.))
    )
  )

(defn getDateTime
  [date]
  (let [dateFormat (SimpleDateFormat. "MM/dd/YYYY HH:mm:ss.SSS")]
    (.format dateFormat (date))
    )
  )

(defn getLocalCurrDateTimeToString
  [dt]
  (let [dateFormat (SimpleDateFormat. "MM/dd/YYYY")]
    (.format dateFormat dt)
    )
  )

(defn getDateTimeByFormat
  [dt format]
  (let [dateFormat (SimpleDateFormat. format)]
    (.format dateFormat dt)
    )
  )

