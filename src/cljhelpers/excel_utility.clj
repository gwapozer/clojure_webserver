(ns cljhelpers.excel-utility
  (:import (org.apache.poi.hssf.util CellReference)))


(defn get-cell
  [sheet r c]
  (let [_row (.getRow sheet r)
        row (cond (nil? _row) (.createRow sheet r) :else _row)
        cell (.getCell row c)
        ]
    (cond (nil? cell) (.createCell row c) :else cell)
    )
  )

(defn get-cell-ref
  [sheet cellref]
  (let [cr (CellReference. cellref)
        r (.getRow cr)
        c (.getCol cr)]
    (get-cell sheet r (int c))
    )
  )

(defn clone-cells-ref
  "Clone cells"
  [sheet init-c cells]
  (let [init (get-cell-ref sheet init-c)]
    (loop [i 0]
      (if (< i (count cells))
        (let [_curr (get-cell-ref sheet (nth cells i))]
          (doto _curr  (.setCellStyle (.getCellStyle init)))
          (recur (inc i))
          )
        )
      )
    )
  )

(defn clone-cells-col
  "Clone cells"
  [sheet init-c cols]
  (let [init (get-cell-ref sheet init-c)
        cr (CellReference. init-c)
        r (.getRow cr)]
    (loop [i 0]
      (if (< i (count cols))
        (let [_curr (get-cell sheet r (nth cols i))]
          (doto _curr  (.setCellStyle (.getCellStyle init)))
          (recur (inc i))
          )
        )
      )
    )
  )
