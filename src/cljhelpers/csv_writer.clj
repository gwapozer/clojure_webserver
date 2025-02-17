(ns cljhelpers.csv-writer
  (:require [ring.util.codec :as cd]
            [cljhelpers.objEval :as oe])
  )

(defn get-csv [headers _xlrslt]
  (let [header-str (str (apply str (butlast (apply str (map #(str % ",") headers)))) "\n")]
    (loop [i 0 my-str header-str]
      (if (< i (count _xlrslt))
        (let [_data (nth _xlrslt i)
              _str (apply str (butlast (apply str (map #(str % ",") _data))))
              ]
          (recur (inc i) (str my-str _str "\n"))
          )
        (do
          (cd/base64-encode (.getBytes my-str))
          )
        )
      )
    )
  )

(defn write-csv [file-path headers _xlrslt]
  (let [header-str (str (apply str (butlast (apply str (map #(str % ",") headers)))) "\n")]
    (loop [i 0 my-str header-str]
      (if (< i (count _xlrslt))
        (let [_data (nth _xlrslt i)
              _str (apply str (butlast (apply str (map #(str % ",") _data))))
              ]
          (recur (inc i) (str my-str _str "\n"))
          )
        (do
          (spit file-path my-str)
          )
        )
      )
    )
  )